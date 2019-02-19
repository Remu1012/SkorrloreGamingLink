package me.skorrloregaming.commands;

import me.skorrloregaming.Link$;
import me.skorrloregaming.LinkServer;
import me.skorrloregaming.redis.MapBuilder;
import me.skorrloregaming.redis.RedisChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TellCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player player = ((Player) sender);
		if (args.length < 2) {
			player.sendMessage(Link$.Legacy.tag + ChatColor.GRAY + "Syntax " + ChatColor.RED + "/" + label + " <player> <message>");
			return true;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				sb.append(args[i] + " ");
			}
			int rank = Link$.getRankId(player);
			int donorRank = Link$.getDonorRankId(player);
			String message = sb.toString();
			if (player.isOp() || rank > -1 || donorRank < -2) {
				message = ChatColor.translateAlternateColorCodes('&', message);
			}
			Player targetPlayer = LinkServer.getPlugin().getServer().getPlayer(args[0]);
			if (targetPlayer == null) {
				player.sendMessage(Link$.Legacy.tag + ChatColor.RED + "Warning. " + ChatColor.GRAY + "The specified player is not on this server.");
				String rawForwardMessage = ChatColor.WHITE + "[" + ChatColor.RED + player.getName() + ChatColor.WHITE + " " + '\u00BB' + " " + ChatColor.RED + "me" + ChatColor.WHITE + "] " + message;
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "me" + ChatColor.WHITE + " " + '\u00BB' + " " + ChatColor.RED + targetPlayer.getName() + ChatColor.WHITE + "] " + message);
				Map<String, String> forwardMessage = new MapBuilder().message(rawForwardMessage).playerName(args[0]).origin(player.getName()).notify(true).build();
				LinkServer.getInstance().getRedisMessenger().broadcast(RedisChannel.CHAT, forwardMessage);
				LinkServer.getMessageRequests().put(player.getName(), args[0]);
				LinkServer.getMessageRequests().put(args[0], player.getName());
				LinkServer.getInstance().getRedisMessenger().broadcast(RedisChannel.CHAT, new MapBuilder().message(rawForwardMessage).range(0).notify(true).send(false).build());
				return true;
			} else {
				message = LinkServer.getInstance().getAntiCheat().processAntiSwear(player, message);
				player.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "me" + ChatColor.WHITE + " " + '\u00BB' + " " + ChatColor.RED + targetPlayer.getName() + ChatColor.WHITE + "] " + message);
				targetPlayer.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + player.getName() + ChatColor.WHITE + " " + '\u00BB' + " " + ChatColor.RED + "me" + ChatColor.WHITE + "] " + message);
				targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
				LinkServer.getMessageRequests().put(player.getName(), targetPlayer.getName());
				LinkServer.getMessageRequests().put(targetPlayer.getName(), player.getName());
				LinkServer.getInstance().getRedisMessenger().broadcast(RedisChannel.CHAT, new MapBuilder().message(message).range(0).notify(true).send(false).build());
			}
		}
		return true;
	}

}
