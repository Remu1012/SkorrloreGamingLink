package me.skorrloregaming.hooks;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.skorrloregaming.LinkServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class BungeeCord_Listener implements PluginMessageListener {

	public void register() {
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(LinkServer.getPlugin(), "BungeeCord", this);
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(LinkServer.getPlugin(), "BungeeCord");
	}

	public void unregister() {
		Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(LinkServer.getPlugin(), "BungeeCord", this);
		Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(LinkServer.getPlugin(), "BungeeCord");
	}

	public void broadcastMessage(Player player, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ALL");
		out.writeUTF("Broadcast");
		out.writeUTF(ChatColor.translateAlternateColorCodes('&', message));
		player.sendPluginMessage(LinkServer.getPlugin(), "BungeeCord", out.toByteArray());
		if (LinkServer.getPlugin().getConfig().getBoolean("settings.subserver.is", true))
			Bukkit.broadcastMessage(message);
	}

	public void broadcastDiscordMessage(Player player, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF(LinkServer.getPlugin().getConfig().getString("settings.discordBotServer", "main"));
		out.writeUTF("DiscordBroadcast");
		out.writeUTF(ChatColor.translateAlternateColorCodes('&', message));
		player.sendPluginMessage(LinkServer.getPlugin(), "BungeeCord", out.toByteArray());
	}

	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		try {
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
			if (in.readUTF().equals("Broadcast")) {
				if (LinkServer.getPlugin().getConfig().getBoolean("settings.subserver.is", true))
					Bukkit.broadcastMessage(in.readUTF());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
