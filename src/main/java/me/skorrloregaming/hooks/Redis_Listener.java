package me.skorrloregaming.hooks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.skorrloregaming.LinkServer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Optional;
import java.util.UUID;

public class Redis_Listener extends JedisPubSub implements Listener {

	private Optional<JedisPool> jedisPool = Optional.empty();

	private final Gson gson = new Gson();

	/**
	 * Just a random UUID as a placeholder for what could be a configurable
	 * server ID which would allow other servers to identify where a message
	 * came from. Currently this is just used to make sure messages are not
	 * duped.
	 */
	private final UUID serverID = UUID.randomUUID();

	private Redis_Listener instance;

	private boolean connectToRedis() {
		instance = this;
		LinkServer.getPlugin().getLogger().info("Connecting to Redis..");
		String hostname = LinkServer.getPlugin().getConfig().getString("settings.redis.hostname", "localhost");
		int port = LinkServer.getPlugin().getConfig().getInt("settings.redis.port", 6379);
		String password = LinkServer.getPlugin().getConfig().getString("settings.redis.password");
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxWaitMillis(10 * 1000);
		if (password == null || password.equals("")) {
			jedisPool = Optional.ofNullable(new JedisPool(poolConfig, hostname, port, 0));
		} else {
			jedisPool = Optional.ofNullable(new JedisPool(poolConfig, hostname, port, 0, password));
		}
		return jedisPool.isPresent();
	}

	private Redis_Listener getInstance() {
		return instance;
	}

	private boolean close() {
		if (jedisPool.isPresent()) {
			jedisPool.get().destroy();
			return true;
		}
		return false;
	}

	public Optional<JedisPool> getPool() {
		if (!jedisPool.isPresent() || jedisPool.get().isClosed()) {
			connectToRedis();
		}
		return jedisPool;
	}

	public void register() {
		connectToRedis();
		LinkServer.getPlugin().getLogger().info("Connected to Redis!");
		Bukkit.getScheduler().runTaskAsynchronously(LinkServer.getPlugin(), new Runnable() {

			@Override
			public void run() {
				getPool().ifPresent((pool) -> {
					try (Jedis jedis = pool.getResource()) {
						jedis.subscribe(getInstance(), "slgn:chat");
					} catch (Exception ex) {
					}
				});
			}
		});
	}

	public void unregister() {
		close();
	}

	public void broadcastMessage(String message) {
		Bukkit.getScheduler().runTaskAsynchronously(LinkServer.getPlugin(), new Runnable() {

			@Override
			public void run() {
				Gson gson = new GsonBuilder().create();
				JsonObject obj = new JsonObject();
				obj.addProperty("serverName", serverID.toString());
				obj.addProperty("message", message);
				getPool().ifPresent((pool) -> {
					try (Jedis jedis = pool.getResource()) {
						jedis.publish("slgn:chat", obj.toString());
					} catch (Exception ex) {
					}
				});
			}
		});
	}

	public void broadcastDiscordMessage(String message, String channel) {
		Bukkit.getScheduler().runTaskAsynchronously(LinkServer.getPlugin(), new Runnable() {

			@Override
			public void run() {
				Gson gson = new GsonBuilder().create();
				JsonObject obj = new JsonObject();
				obj.addProperty("serverName", serverID.toString());
				obj.addProperty("message", message);
				obj.addProperty("discordChannel", channel);
				getPool().ifPresent((pool) -> {
					try (Jedis jedis = pool.getResource()) {
						jedis.publish("slgn:discord", obj.toString());
					} catch (Exception ex) {
					}
				});
			}
		});
	}

	@Override
	public void onMessage(String channel, String request) {
		if (channel.equalsIgnoreCase("slgn:chat")) {
			JsonObject obj = gson.fromJson(request, JsonObject.class);
			if (obj != null) {
				String servername = obj.get("serverName").getAsString();
				if (!servername.equalsIgnoreCase(serverID.toString())) {
					String message = obj.get("message").getAsString();
					Bukkit.broadcastMessage(message);
				}
			}
		}
	}

}
