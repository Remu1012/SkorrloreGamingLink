package me.skorrloregaming.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DisposeCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player player = ((Player) sender);
		Inventory inv = Bukkit.createInventory(null, 27);
		player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
		player.openInventory(inv);
		return true;
	}

}
