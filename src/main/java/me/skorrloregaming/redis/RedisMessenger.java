package me.skorrloregaming.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.skorrloregaming.CraftGo;
import me.skorrloregaming.Link$;
import me.skorrloregaming.LinkServer;
import me.skorrloregaming.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

	public void ping(RedisChannel channel, String ping, String playerName) {
		Bukkit.getScheduler().runTask(LinkServer.getPlugin(), new Runnable() {

			@Override
			public void run() {
				Gson gson = new GsonBuilder().create();
				JsonObject obj = new JsonObject();
				obj.addProperty("ping", ping);
				obj.addProperty("playerName", playerName);
				LinkServer.getRedisDatabase().getPool().ifPresent((pool) -> {
					try (Jedis jedis = pool.getResource()) {
						jedis.publish("slgn:" + channel.toString().toLowerCase(), obj.toString());
					} catch (Exception ex) {
					}
				});
			}
		});
	}

	public void broadcast(RedisChannel channel, Map<String, String> message) {
		Bukkit.getScheduler().runTask(LinkServer.getPlugin(), new Runnable() {

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

	private void bukkitBroadcast(String origin, String message, boolean json) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (json) {
				CraftGo.Player.sendJson(player, message);
			} else
				player.sendMessage(message);
		}
	}

	@Override
	public void onMessage(String channel, String request) {
		if (channel.equalsIgnoreCase("slgn:chat")) {
			JsonObject obj = gson.fromJson(request, JsonObject.class);
			if (obj != null) {
				String ping = obj.get("ping").getAsString();
				switch (ping) {
					case "MESSAGE":
						String serverName = obj.get("serverName").getAsString();
						boolean json = obj.get("json").getAsBoolean();
						String message = obj.get("message").getAsString();
						int range = obj.get("range").getAsInt();
						boolean consoleOnly = obj.get("consoleOnly").getAsBoolean();
						String origin = obj.get("origin").getAsString();
						String playerName = obj.get("playerName").getAsString();
						boolean notify = obj.get("notify").getAsBoolean();
						boolean send = obj.get("send").getAsBoolean();
						if (serverName.equals(LinkServer.getServerName())) {
							if (LinkServer.getPlugin().getConfig().getBoolean("settings.subServer", false)) {
								if (playerName.equals("ALL")) {
									if (range == -2) {
										bukkitBroadcast(origin, message, json);
									} else {
										if (send) {
											Logger.info(message, consoleOnly, range, notify);
										} else if (notify) {
											for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
												if (range == -1 || Link$.getRankId(otherPlayer) >= range) {
													otherPlayer.playSound(otherPlayer.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
												}
											}
										}
									}
								} else {
									Player player = Bukkit.getPlayerExact(playerName);
									if (player != null) {
										if (notify)
											player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
										if (json) {
											CraftGo.Player.sendJson(player, message);
										} else {
											player.sendMessage(message);
										}
									}
								}
							}
						} else {
							if (playerName.equals("ALL")) {
								if (range == -2) {
									bukkitBroadcast(origin, message, json);
								} else {
									if (send) {
										Logger.info(message, consoleOnly, range, notify);
									} else if (notify) {
										for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
											if (range == -1 || Link$.getRankId(otherPlayer) >= range) {
												otherPlayer.playSound(otherPlayer.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
											}
										}
									}
								}
							} else {
								Player player = Bukkit.getPlayerExact(playerName);
								if (player != null) {
									if (notify)
										player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
									if (json) {
										CraftGo.Player.sendJson(player, message);
									} else {
										player.sendMessage(message);
									}
								}
							}
						}
						break;
					case "RANK_UPDATE":
						OfflinePlayer targetPlayer = CraftGo.Player.getOfflinePlayer(obj.get("playerName").getAsString());
						if (Link$.isPrefixedRankingEnabled() && targetPlayer.isOnline()) {
							Link$.flashPlayerDisplayName(targetPlayer.getPlayer());
						}
						break;
					default:
						break;
				}
			}
		}

	}
}
