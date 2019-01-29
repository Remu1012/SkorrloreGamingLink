package me.skorrloregaming.commands;

import me.skorrloregaming.Link$;
import me.skorrloregaming.LinkServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnsubscribeCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player player = ((Player) sender);
		String path = "config." + player.getUniqueId().toString();
		boolean subscribed = Boolean.parseBoolean(LinkServer.getPlugin().getConfig().getString(path + ".subscribed", "true"));
		LinkServer.getPlugin().getConfig().set(path + ".subscribed", !subscribed + "");
		if (subscribed) {
			sender.sendMessage(Link$.Legacy.tag + ChatColor.RED + "Success. " + ChatColor.GRAY + "Advertisements has been disabled.");
		} else {
			sender.sendMessage(Link$.Legacy.tag + ChatColor.RED + "Success. " + ChatColor.GRAY + "Advertisements has been enabled.");
		}
		return true;
	}
}
