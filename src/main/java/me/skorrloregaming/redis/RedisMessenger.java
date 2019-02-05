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

	private final Gson gson = new Gson();

	public void register() {
		final RedisMessenger instance = this;
		Bukkit.getScheduler().runTaskAsynchronously(LinkServer.getPlugin(), new Runnable() {

			@Override
			public void run() {
				LinkServer.getRedisDatabase().getPool().ifPresent((pool) -> {
					try (Jedis jedis = pool.getResource()) {
						jedis.subscribe(instance, "slgn:chat");
					} catch (Exception ex) {
					}
				});
			}
		});
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
				LinkServer.getRedisDatabase().getPool().ifPresent((pool) -> {
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
