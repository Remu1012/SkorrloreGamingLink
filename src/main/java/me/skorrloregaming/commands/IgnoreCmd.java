package me.skorrloregaming.commands;

import me.skorrloregaming.Link$;
import me.skorrloregaming.LinkServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IgnoreCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player player = ((Player) sender);
		if (args.length == 0) {
			player.sendMessage(Link$.Legacy.tag + ChatColor.GRAY + "Syntax " + ChatColor.RED + "/" + label + " <player>");
		} else {
			Player targetPlayer = Bukkit.getPlayer(args[0]);
			if (targetPlayer == null) {
				player.sendMessage(Link$.Legacy.tag + ChatColor.RED + "Failed. " + ChatColor.GRAY + "The specified player could not be found.");
				return true;
			}
			UUID uid;
			if ((uid = Link$.getIgnoredPlayer(player.getUniqueId())) != null) {
				Player existingIgnore = Bukkit.getPlayer(uid);
				if (existingIgnore.getName().toString().equals(targetPlayer.getName().toString())) {
					Link$.setIgnoredPlayer(player.getUniqueId(), targetPlayer.getUniqueId());
					player.sendMessage(Link$.Legacy.tag + ChatColor.RED + "Success. " + ChatColor.GRAY + "You are no longer ignoring the specified player. If you had an issue while using the /ignore feature, please let the server administrators know about the issue.");
					return true;
				}
			}
			Link$.setIgnoredPlayer(player.getUniqueId(), targetPlayer.getUniqueId());
			player.sendMessage(Link$.Legacy.tag + ChatColor.RED + "Success. " + ChatColor.GRAY + "You are now ignoring the specified player. This will only last until the next server update and/or restart.");
		}
		return true;
	}

}
