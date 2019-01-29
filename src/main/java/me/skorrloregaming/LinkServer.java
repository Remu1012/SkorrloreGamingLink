package me.skorrloregaming;

import me.skorrloregaming.commands.DebugCmd;
import me.skorrloregaming.commands.LoggerCmd;
import me.skorrloregaming.commands.UnsubscribeCmd;
import me.skorrloregaming.impl.ServerMinigame;
import me.skorrloregaming.mysql.SQLDatabase;
import me.skorrloregaming.runnable.AutoBroadcaster;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LinkServer extends JavaPlugin implements Listener {

	private static Plugin plugin;

	private static CraftGo.BarApi barApi = null;

	private static SQLDatabase sqlDatabase;

	private static PlaytimeManager playtimeManager;

	private static AntiCheat anticheat;

	private static ConfigurationManager geolCacheConfig;
	private static ConfigurationManager uuidCacheConfig;

	private static boolean pluginDebug = false;
	private static boolean ingameAnticheatDebug = true;

	private static final long BASIC_INVENTORY_UPDATE_DELAY = 5L;

	private static ConcurrentMap<UUID, Integer> barApiTitleIndex = new ConcurrentHashMap<>();

	@Override
	public void onEnable() {
		this.plugin = this;
		getConfig().options().copyDefaults(true);
		saveConfig();
		String dbUsername = getConfig().getString("settings.database.username", "username");
		String dbPassword = getConfig().getString("settings.database.password", "password");
		barApi = new CraftGo.BarApi();
		barApi.onEnable();
		sqlDatabase = new SQLDatabase("localhost", dbUsername, dbPassword);
		playtimeManager = new PlaytimeManager();
		anticheat = new AntiCheat();
		anticheat.register();
		geolCacheConfig = new ConfigurationManager();
		geolCacheConfig.setup(new File(this.getDataFolder(), "geolocation_cache.yml"));
		uuidCacheConfig = new ConfigurationManager();
		uuidCacheConfig.setup(new File(this.getDataFolder(), "uuid_cache.yml"));
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("debug").setExecutor(new DebugCmd());
		getCommand("logger").setExecutor(new LoggerCmd());
		getCommand("unsubscribe").setExecutor(new UnsubscribeCmd());
		Bukkit.getScheduler().runTaskTimer(this, new AutoBroadcaster(), 6000L, 12000L);
		Bukkit.getScheduler().runTaskTimer(getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					String path = "config." + player.getUniqueId().toString();
					boolean subscribed = Boolean.parseBoolean(getPlugin().getConfig().getString(path + ".subscribed", "true"));
					if (subscribed) {
						String message = "   Thank you for playing on the server, please invite your friends. ";
						message += message.substring(0, 32);
						getBarApiTitleIndex().putIfAbsent(player.getUniqueId(), 0);
						int index = getBarApiTitleIndex().get(player.getUniqueId());
						if (message.length() <= 32) {
							getBarApi().setMessage(player, message, BarColor.RED, BarStyle.SOLID);
						} else {
							int finalIndex = index + 32;
							if (index < message.length() && finalIndex < message.length()) {
								getBarApi().setMessage(player, message.substring(index, finalIndex), BarColor.RED, BarStyle.SOLID);
								getBarApiTitleIndex().put(player.getUniqueId(), index + 1);
							} else {
								getBarApiTitleIndex().put(player.getUniqueId(), 0);
							}
						}
					}
				}
			}
		}, 7L, 7L);
	}

	@Override
	public void onDisable() {
		barApi.onDisable();
		sqlDatabase.close();
	}

	public static Plugin getPlugin() {
		return plugin;
	}

	public static CraftGo.BarApi getBarApi() {
		return barApi;
	}

	public static SQLDatabase getSqlDatabase() {
		return sqlDatabase;
	}

	public static PlaytimeManager getPlaytimeManager() {
		return playtimeManager;
	}

	public static AntiCheat getAntiCheat() {
		return anticheat;
	}

	public static ConfigurationManager getGeolocationCache() {
		return geolCacheConfig;
	}

	public static ConfigurationManager getUUIDCache() {
		return uuidCacheConfig;
	}

	public static long getInventoryUpdateDelay() {
		return BASIC_INVENTORY_UPDATE_DELAY;
	}

	public static boolean getPluginDebug() {
		return pluginDebug;
	}

	public static void setPluginDebug(boolean enabled) {
		pluginDebug = enabled;
	}

	public static boolean getIngameAnticheatDebug() {
		return ingameAnticheatDebug;
	}

	public static void setIngameAnticheatDebug(boolean enabled) {
		ingameAnticheatDebug = enabled;
	}

	public static ConcurrentMap<UUID, Integer> getBarApiTitleIndex() {
		return barApiTitleIndex;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		getPlaytimeManager().handle_JoinEvent(player);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		getPlaytimeManager().handle_QuitEvent(player);
		if (getBarApiTitleIndex().containsKey(player.getUniqueId()))
			getBarApiTitleIndex().remove(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (getAntiCheat().antiafk.lastPlayerLocation.containsKey(player.getUniqueId()) && getAntiCheat().antiafk.lastPlayerLocation.get(player.getUniqueId()).getWorld().getName().equals(player.getWorld().getName())) {
			if (!player.isInsideVehicle() && getAntiCheat().antiafk.lastPlayerLocation.get(player.getUniqueId()).distance(player.getLocation()) > 1.25) {
				if (getAntiCheat().antiafk.lackingActivityMinutes.containsKey(player.getUniqueId()))
					getAntiCheat().antiafk.lackingActivityMinutes.remove(player.getUniqueId());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if (getAntiCheat().antiafk.lackingActivityMinutes.containsKey(player.getUniqueId()))
			getAntiCheat().antiafk.lackingActivityMinutes.remove(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityAttack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Firework && event.getEntity() instanceof Player) {
			event.setCancelled(true);
			return;
		}
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			if (getAntiCheat().antiafk.lackingActivityMinutes.containsKey(damager.getUniqueId()))
				getAntiCheat().antiafk.lackingActivityMinutes.remove(damager.getUniqueId());
		}
	}

}
