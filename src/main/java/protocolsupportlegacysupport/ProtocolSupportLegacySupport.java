package protocolsupportlegacysupport;

import java.math.BigInteger;
import java.text.MessageFormat;

import me.skorrloregaming.LinkServer;
import me.skorrloregaming.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import protocolsupport.api.ProtocolSupportAPI;
import protocolsupportlegacysupport.bossbar.BossBarHandler;
import protocolsupportlegacysupport.brewingstandfuel.BrewingStandFuelHandler;
import protocolsupportlegacysupport.enchantingtable.EnchantingTableHandler;
import protocolsupportlegacysupport.hologram.HologramHandler;

public class ProtocolSupportLegacySupport {

	private static ProtocolSupportLegacySupport instance;

	public static ProtocolSupportLegacySupport getInstance() {
		return instance;
	}

	public ProtocolSupportLegacySupport() {
		instance = this;
	}

	private static final BigInteger requiredAPIversion = BigInteger.ONE;

	public void onEnable() {
		try {
			if (ProtocolSupportAPI.getAPIVersion().compareTo(requiredAPIversion) < 0) {
				Logger.info(MessageFormat.format("Too low ProtocolSupport API version, required at least {0}, got {1}", requiredAPIversion, ProtocolSupportAPI.getAPIVersion()));
				getPlugin().getServer().getPluginManager().disablePlugin(getPlugin());
				return;
			}
		} catch (Throwable t) {
			Logger.info("Unable to detect ProtocolSupport API version");
			getPlugin().getServer().getPluginManager().disablePlugin(getPlugin());
			return;
		}
		new BrewingStandFuelHandler().start();
		new EnchantingTableHandler().start();
		new HologramHandler().start();
		new BossBarHandler().start();
	}

	public Plugin getPlugin() {
		return LinkServer.getPlugin();
	}

}
