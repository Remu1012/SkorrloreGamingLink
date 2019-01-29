package me.skorrloregaming;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Logger {
	public static final int LOG_DEFAULT_RANKID = 0;

	private static void broadcast(String msg, int minRankId) {
		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
			if (minRankId == -1 || Link$.getRankId(otherPlayer) >= minRankId) {
				otherPlayer.sendMessage(Link$.italicGray + msg);
			}
		}
	}

	private static void broadcast(String msg) {
		broadcast(msg, LOG_DEFAULT_RANKID);
	}

	public static void info(String msg, boolean consoleOnly, int minRankId) {
		Bukkit.getLogger().info(ChatColor.stripColor(msg));
		if (!consoleOnly) {
			broadcast(msg);
		}
	}

	public static void info(String msg, boolean consoleOnly) {
		info(msg, consoleOnly, LOG_DEFAULT_RANKID);
	}

	public static void warning(String msg, boolean consoleOnly) {
		Bukkit.getLogger().warning(ChatColor.stripColor(msg));
		if (!consoleOnly)
			broadcast(msg);
	}

	public static void severe(String msg, boolean consoleOnly) {
		Bukkit.getLogger().severe(ChatColor.stripColor(msg));
		if (!consoleOnly)
			broadcast(msg);
	}

	public static void info(String msg) {
		info(msg, false);
	}

	public static void debug(String msg) {
		info(msg, true);
	}

	public static void warning(String msg) {
		warning(msg, false);
	}

	public static void severe(String msg) {
		severe(msg, false);
	}
}
