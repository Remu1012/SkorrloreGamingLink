package me.skorrloregaming.redis;

import com.google.gson.Gson;
import me.skorrloregaming.LinkServer;
import me.skorrloregaming.Logger;
import org.bukkit.Bukkit;
import redis.clients.jedis.*;

import java.util.Optional;
import java.util.Set;

public class RedisDatabase {

	private Optional<JedisPool> jedisPool = Optional.empty();

	private RedisDatabase instance;

	private Jedis jedis;

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

	private RedisDatabase getInstance() {
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
		Bukkit.getScheduler().runTask(LinkServer.getPlugin(), new Runnable() {

			@Override
			public void run() {
				connectToRedis();
				jedis = jedisPool.get().getResource();
				LinkServer.getPlugin().getLogger().info("Connected to Redis!");
			}
		});
	}

	public void unregister() {
		Bukkit.getScheduler().runTask(LinkServer.getPlugin(), new Runnable() {

			@Override
			public void run() {
				close();
			}
		});
	}

	/**
	 * This can cause issues if executed async
	 *
	 * @deprecated Make sure this is executed on the main thread
	 */
	@Deprecated
	public void setUnsafe(String table, String key, String value) {
		if (value == null) {
			jedis.del(table + "." + key);
			jedis.sync();
		} else {
			jedis.set(table + "." + key, value);
			jedis.sync();
		}
	}

	public void set(String table, String key, String value) {
		Bukkit.getScheduler().runTask(LinkServer.getPlugin(), new Runnable() {

			@Override
			public void run() {
				setUnsafe(table, key, value);
			}
		});
	}

	private String getString(String table, String key, boolean callback) {
		Pipeline pipeline = jedis.pipelined();
		String response = null;
		try {
			Response<String> preResponse = pipeline.get(table + "." + key);
			pipeline.sync();
			response = preResponse.get();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (callback) {
				Logger.severe("Clearing jedis pipeline due to error encountered during use..");
				pipeline.clear();
				response = getString(table, key, false);
			}
		}
		return response;
	}

	/**
	 * This can cause issues if executed async
	 *
	 * @deprecated Make sure this is executed on the main thread
	 */
	@Deprecated
	public String getString(String table, String key) {
		return getString(table, key, true);
	}

	/**
	 * This can cause issues if executed async
	 *
	 * @deprecated Make sure this is executed on the main thread
	 */
	@Deprecated
	public boolean contains(String table, String key) {
		String response;
		return !((response = getString(table, key)) == null || response.length() == 0);
	}

	private Set<String> getKeys(String pattern, boolean callback) {
		Pipeline pipeline = jedis.pipelined();
		Set<String> response = null;
		try {
			Response<Set<String>> preResponse = pipeline.keys(pattern);
			pipeline.sync();
			response = preResponse.get();
		} catch (Exception ex) {
			ex.printStackTrace();
			if (callback) {
				Logger.severe("Clearing jedis pipeline due to error encountered during use..");
				pipeline.clear();
				response = getKeys(pattern, false);
			}
		}
		return response;
	}

	/**
	 * This can cause issues if executed async
	 *
	 * @deprecated Make sure this is executed on the main thread
	 */
	@Deprecated
	public Set<String> getKeys(String pattern) {
		return getKeys(pattern, true);
	}
}
