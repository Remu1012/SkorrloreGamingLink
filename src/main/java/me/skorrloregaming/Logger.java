package me.skorrloregaming;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Logger {
	public static final int LOG_DEFAULT_RANKID = 0;

	private static void broadcast(String msg, int minRankId, boolean notify) {
		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
			if (minRankId == -1 || Link$.getRankId(otherPlayer) >= minRankId) {
				if (notify)
					otherPlayer.playSound(otherPlayer.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
				otherPlayer.sendMessage(Link$.italicGray + msg);
			}
		}
	}

	private static void broadcast(String msg, int minRankId) {
		broadcast(msg, minRankId, false);
	}

	private static void broadcast(String msg) {
		broadcast(msg, LOG_DEFAULT_RANKID, false);
	}

	public static void info(String msg, boolean consoleOnly, int minRankId, boolean notify) {
		Bukkit.getLogger().info(ChatColor.stripColor(msg));
		if (!consoleOnly) {
			broadcast(msg, LOG_DEFAULT_RANKID, notify);
		}
	}

	public static void info(String msg, boolean consoleOnly, int minRankId) {
		info(msg, consoleOnly, minRankId, false);
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
