package me.skorrloregaming;

import me.skorrloregaming.commands.*;
import me.skorrloregaming.hooks.ProtocolSupport_Listener;
import me.skorrloregaming.redis.MapBuilder;
import me.skorrloregaming.redis.RedisChannel;
import me.skorrloregaming.redis.RedisMessenger;
import me.skorrloregaming.mysql.SQLDatabase;
import me.skorrloregaming.runnable.AutoBroadcaster;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LinkServer extends JavaPlugin implements Listener {

	private static Plugin plugin;

	private static LinkServer instance;

	private static CraftGo.BarApi barApi = null;

	private static SQLDatabase sqlDatabase;

	private static PlaytimeManager playtimeManager;

	private static AntiCheat anticheat;

	private static RedisMessenger redisMessenger;
	private static ProtocolSupport_Listener protoSupportListener;

	private static ConfigurationManager geolCacheConfig;
	private static ConfigurationManager uuidCacheConfig;

	private static boolean pluginDebug = false;
	private static boolean ingameAnticheatDebug = true;

	private static String serverName;
	private static String discordChannel;

	private static final long BASIC_INVENTORY_UPDATE_DELAY = 5L;

	private static ArrayList<String> disabledVersions = new ArrayList<String>();

	private static ConcurrentMap<UUID, Integer> barApiTitleIndex = new ConcurrentHashMap<>();

	@Override
	public void onEnable() {
		this.instance = this;
		this.plugin = this;
		getConfig().options().copyDefaults(true);
		saveConfig();
		reload();
		String dbUsername = getConfig().getString("settings.database.username", "username");
		String dbPassword = getConfig().getString("settings.database.password", "password");
		serverName = getConfig().getString("settings.serverName", "lobby");
		discordChannel = getConfig().getString("settings.discordChannel", "SERVER_CHAT");
		barApi = new CraftGo.BarApi();
		barApi.onEnable();
		sqlDatabase = new SQLDatabase("localhost", dbUsername, dbPassword);
		playtimeManager = new PlaytimeManager();
		anticheat = new AntiCheat();
		anticheat.register();
		if (getConfig().getBoolean("settings.bungeecord", false)) {
			redisMessenger = new RedisMessenger();
			redisMessenger.register();
		}
		if (Link$.isPluginEnabled("ProtocolSupport")) {
			protoSupportListener = new ProtocolSupport_Listener();
			protoSupportListener.register();
			protoSupportListener.disableProtocolVersions();
		}
		geolCacheConfig = new ConfigurationManager();
		geolCacheConfig.setup(new File(this.getDataFolder(), "geolocation_cache.yml"));
		uuidCacheConfig = new ConfigurationManager();
		uuidCacheConfig.setup(new File(this.getDataFolder(), "uuid_cache.yml"));
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("debug").setExecutor(new DebugCmd());
		getCommand("logger").setExecutor(new LoggerCmd());
		getCommand("unsubscribe").setExecutor(new UnsubscribeCmd());
		getCommand("playtime-report").setExecutor(new PlaytimeReportCmd());
		getCommand("playtime").setExecutor(new PlaytimeCmd());
		getCommand("desync").setExecutor(new DesyncCmd());
		getCommand("sync").setExecutor(new SyncCmd());
		getCommand("discord").setExecutor(new DiscordCmd());
		getCommand("address").setExecutor(new AddressCmd());
		getCommand("vote").setExecutor(new VoteCmd());
		getCommand("modifications").setExecutor(new ModificationsCmd());
		getCommand("website").setExecutor(new WebsiteCmd());
		getCommand("vote").setExecutor(new VoteCmd());
		getCommand("rules").setExecutor(new RulesCmd());
		getCommand("workbench").setExecutor(new WorkbenchCmd());
		getCommand("dispose").setExecutor(new DisposeCmd());
		getCommand("ping").setExecutor(new PingCmd());
		getCommand("buy").setExecutor(new BuyCmd());
		Bukkit.getScheduler().runTaskTimer(this, new AutoBroadcaster(), 6000L, 12000L);
		Bukkit.getScheduler().runTaskTimer(getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (getPlugin().getConfig().getBoolean("settings.essentials", false))
						player.setPlayerListName(player.getDisplayName());
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
		redisMessenger.unregister();
	}

	public void reload() {
		reloadConfig();
		boolean _1m13 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m12"));
		boolean _1m12 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m12"));
		boolean _1m11 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m11"));
		boolean _1m10 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m10"));
		boolean _1m9 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m9"));
		boolean _1m8 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m8"));
		boolean _1m7 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m7"));
		boolean _1m6 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m6"));
		boolean _1m5 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m5"));
		boolean _1m4 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PC.1m4"));
		boolean _1m3 = Boolean.parseBoolean(getConfig().getString("settings.enable.protocolsupport.versions.PE"));
		disabledVersions.clear();
		if (!_1m13) {
			disabledVersions.add("1.13.2");
			disabledVersions.add("1.13.1");
			disabledVersions.add("1.13");
		}
		if (!_1m12) {
			disabledVersions.add("1.12.2");
			disabledVersions.add("1.12.1");
			disabledVersions.add("1.12");
		}
		if (!_1m11) {
			disabledVersions.add("1.11.1");
			disabledVersions.add("1.11");
		}
		if (!_1m10) {
			disabledVersions.add("1.10");
		}
		if (!_1m9) {
			disabledVersions.add("1.9.4");
			disabledVersions.add("1.9.2");
			disabledVersions.add("1.9.1");
			disabledVersions.add("1.9");
		}
		if (!_1m8) {
			disabledVersions.add("1.8");
		}
		if (!_1m7) {
			disabledVersions.add("1.7.10");
			disabledVersions.add("1.7.5");
		}
		if (!_1m6) {
			disabledVersions.add("1.6.4");
			disabledVersions.add("1.6.2");
			disabledVersions.add("1.6.1");
		}
		if (!_1m5) {
			disabledVersions.add("1.5.2");
			disabledVersions.add("1.5.1");
		}
		if (!_1m4) {
			disabledVersions.add("1.4.7");
		}
		if (!_1m3) {
			disabledVersions.add("pe");
		}
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

	public static RedisMessenger getRedisMessenger() {
		return redisMessenger;
	}

	public static ProtocolSupport_Listener getProtoSupportListener() {
		return protoSupportListener;
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

	public static String getServerName() {
		return serverName;
	}

	public static String getDiscordChannel() {
		return discordChannel;
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

	public static ArrayList<String> getDisabledVersions() {
		return disabledVersions;
	}

	public static ConcurrentMap<UUID, Integer> getBarApiTitleIndex() {
		return barApiTitleIndex;
	}

	public static LinkServer getInstance() {
		return instance;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		getPlaytimeManager().handle_JoinEvent(player);
		if (getConfig().getBoolean("settings.bungeecord", true)) {
			if (getConfig().getBoolean("settings.subServer", false)) {
				String message = Link$.Legacy.tag + ChatColor.RED + player.getName() + ChatColor.GRAY + " has logged into " + ChatColor.RED + getServerName();
				redisMessenger.broadcast(RedisChannel.CHAT, new MapBuilder().message(message).build());
				message = message.replace(player.getName(), "**" + player.getName() + "**");
				redisMessenger.broadcast(RedisChannel.DISCORD, new MapBuilder().message(message).channel(discordChannel).build());
				event.setJoinMessage(null);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		getPlaytimeManager().handle_QuitEvent(player);
		if (getBarApiTitleIndex().containsKey(player.getUniqueId()))
			getBarApiTitleIndex().remove(player.getUniqueId());
		if (getConfig().getBoolean("settings.bungeecord", true)) {
			if (getConfig().getBoolean("settings.subServer", false)) {
				String message = Link$.Legacy.tag + ChatColor.RED + player.getName() + ChatColor.GRAY + " has quit " + ChatColor.RED + getServerName();
				redisMessenger.broadcast(RedisChannel.CHAT, new MapBuilder().message(message).build());
				message = message.substring(message.indexOf(ChatColor.RED + ""));
				message = message.replace(player.getName(), "**" + player.getName() + "**");
				redisMessenger.broadcast(RedisChannel.DISCORD, new MapBuilder().message(message).channel(discordChannel).build());
				event.setQuitMessage(null);
			}
		}
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
		String world = player.getWorld().getName();
		if (getConfig().getBoolean("settings.bungeecord", true)) {
			if (getConfig().getBoolean("settings.subServer", false)) {
				String processedMessage = getAntiCheat().processAntiSwear(player, event.getMessage());
				String msg = ChatColor.GRAY + "[" + ChatColor.WHITE + getServerName().toLowerCase() + ChatColor.GRAY + "] " + ChatColor.RESET + player.getDisplayName() + ChatColor.RESET + " " + '\u00BB' + " " + processedMessage;
				redisMessenger.broadcast(RedisChannel.CHAT, new MapBuilder().message(msg).build());
				if (Link$.isPrefixedRankingEnabled()) {
					String rankName = WordUtils.capitalize(Link$.toRankDisplayName(Link$.getRank(player)));
					if (rankName.equals("Youtube"))
						rankName = "YouTube";
					String message = "**" + rankName + "** " + player.getName() + " " + '\u00BB' + " " + processedMessage;
					redisMessenger.broadcast(RedisChannel.DISCORD, new MapBuilder().message(message).channel(discordChannel).build());
				} else {
					String message = "**" + player.getName() + "** " + '\u00BB' + " " + processedMessage;
					redisMessenger.broadcast(RedisChannel.DISCORD, new MapBuilder().message(message).channel(discordChannel).build());
				}
				event.setCancelled(true);
				return;
			}
		}
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		if (getPlaytimeManager().onInventoryClick(event))
			event.setCancelled(true);
	}

}
