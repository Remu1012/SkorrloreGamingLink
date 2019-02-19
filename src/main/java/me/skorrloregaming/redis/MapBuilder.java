package me.skorrloregaming.redis;

import me.skorrloregaming.LinkServer;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder {

	private String serverName;
	private String discordChannel = "n/a";
	private boolean json = false;
	private String message;
	private int range = -2;
	private boolean consoleOnly = false;
	private String playerName;

	public MapBuilder server(String serverName) {
		this.serverName = serverName;
		return this;
	}

	public MapBuilder channel(String discordChannel) {
		this.discordChannel = discordChannel;
		return this;
	}

	public MapBuilder json(boolean json) {
		this.json = json;
		return this;
	}

	public MapBuilder message(String message) {
		this.message = message;
		return this;
	}

	public MapBuilder range(int range) {
		this.range = range;
		return this;
	}

	public MapBuilder consoleOnly(boolean consoleOnly) {
		this.consoleOnly = consoleOnly;
		return this;
	}

	public MapBuilder playerName(String playerName) {
		this.playerName = playerName;
		return this;
	}

	public Map<String, String> build() {
		Map<String, String> map = new HashMap<String, String>();
		if (serverName == null)
			serverName = LinkServer.getServerName();
		if (message == null)
			throw new IllegalArgumentException();
		map.put("serverName", serverName);
		map.put("discordChannel", discordChannel);
		map.put("json", json + "");
		map.put("message", message);
		map.put("range", range + "");
		map.put("consoleOnly", consoleOnly + "");
		map.put("playerName", playerName);
		return map;
	}

}
