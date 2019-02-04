package me.skorrloregaming.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.skorrloregaming.CraftGo;
import me.skorrloregaming.LinkServer;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.Optional;

public class RedisMessenger extends JedisPubSub implements Listener {

	private Optional<JedisPool> jedisPool = Optional.empty();

	private final Gson gson = new Gson();

	private RedisMessenger instance;

	private boolean connectToRedis() {
		instance = this;
		LinkServer.getPlugin().getLogger().info("Connecting to Redis..");
		String hostname = LinkServer.getPlugin().getConfig().getString("settings.redis.hostname", LinkServer.getPlugin().getConfig().getString("settings.redis.hostname", "localhost"));
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

	private RedisMessenger getInstance() {
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

	public void broadcast(RedisChannel channel, Map<String, String> message) {
		Bukkit.getScheduler().runTaskAsynchronously(LinkServer.getPlugin(), new Runnable() {

			@Override
			public void run() {
				Gson gson = new GsonBuilder().create();
				JsonObject obj = new JsonObject();
				for (Map.Entry<String, String> entry : message.entrySet()) {
					obj.addProperty(entry.getKey(), entry.getValue());
				}
				getPool().ifPresent((pool) -> {
					try (Jedis jedis = pool.getResource()) {
						jedis.publish("slgn:" + channel.toString().toLowerCase(), obj.toString());
					} catch (Exception ex) {
					}
				});
			}
		});
	}

	private void bukkitBroadcast(String message, boolean json) {
		if (json) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				CraftGo.Player.sendJson(player, message);
			}
		} else {
			Bukkit.broadcastMessage(message);
		}
	}

	@Override
	public void onMessage(String channel, String request) {
		if (channel.equalsIgnoreCase("slgn:chat")) {
			JsonObject obj = gson.fromJson(request, JsonObject.class);
			if (obj != null) {
				String serverName = obj.get("serverName").getAsString();
				boolean json = obj.get("json").getAsBoolean();
				String message = obj.get("message").getAsString();
				if (serverName.equals(LinkServer.getServerName())) {
					if (LinkServer.getPlugin().getConfig().getBoolean("settings.subServer", false))
						bukkitBroadcast(message, json);
				} else
					bukkitBroadcast(message, json);
			}
		}
	}

}
