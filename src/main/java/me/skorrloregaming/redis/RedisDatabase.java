package me.skorrloregaming.redis;

import com.google.gson.Gson;
import me.skorrloregaming.LinkServer;
import me.skorrloregaming.Logger;
import org.bukkit.Bukkit;
import redis.clients.jedis.*;

import java.util.Optional;
import java.util.Set;

public class RedisDatabase {

	private RedisDatabase instance;

	private Jedis connectToRedis() {
		String hostname = LinkServer.getPlugin().getConfig().getString("settings.redis.hostname", LinkServer.getPlugin().getConfig().getString("settings.redis.hostname", "localhost"));
		int port = LinkServer.getPlugin().getConfig().getInt("settings.redis.port", 6379);
		String password = LinkServer.getPlugin().getConfig().getString("settings.redis.password");
		if (password != null && password.equals(""))
			password = null;
		Jedis jedis = new Jedis(hostname, port, 5000);
		if (password != null)
			jedis.auth(password);
		return jedis;
	}

	private RedisDatabase getInstance() {
		return instance;
	}

	public void register() {
		instance = this;
	}

	public void unregister() {
	}

	public void set(String table, String key, String value) {
		Jedis jedis = connectToRedis();
		if (value == null) {
			jedis.del(table + "." + key);
		} else {
			jedis.set(table + "." + key, value);
		}
		jedis.close();
	}

	private String getString(String table, String key, boolean callback) {
		Jedis jedis = connectToRedis();
		String response = jedis.get(table + "." + key);
		jedis.close();
		return response;
	}


	public String getString(String table, String key) {
		return getString(table, key, true);
	}

	public boolean contains(String table, String key) {
		Jedis jedis = connectToRedis();
		boolean response = jedis.exists(table + "." + key);
		jedis.close();
		return response;
	}

	private Set<String> getKeys(String pattern, boolean callback) {
		Jedis jedis = connectToRedis();
		Set<String> response = jedis.keys(pattern);
		jedis.close();
		return response;
	}

	public Set<String> getKeys(String pattern) {
		return getKeys(pattern, true);
	}

	public void subscribe(JedisPubSub clazz, String... channels) {
		Jedis jedis = connectToRedis();
		jedis.subscribe(clazz, channels);
		jedis.close();
	}

	public void publish(String channel, String message) {
		Jedis jedis = connectToRedis();
		jedis.publish(channel, message);
		jedis.close();
	}
}
