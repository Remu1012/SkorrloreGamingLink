package me.skorrloregaming;

import me.lucko.luckperms.LuckPerms;
import me.skorrloregaming.impl.EnchantInfo;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Link$ {

	public static String italicGray = ChatColor.GRAY + "" + ChatColor.ITALIC;
	public static String modernMsgPrefix = ChatColor.BOLD + "\u00BB" + " ";

	public static List<String> validRanks = Arrays.asList(new String[]{"default", "founder", "owner", "manager", "admin", "moderator", "helper", "developer", "builder"});
	public static List<String> validDonorRanks = Arrays.asList(new String[]{"default", "youtube", "donator", "redstone", "obsidian", "bedrock"});

	public static void playLackPermissionMessage(CommandSender player) {
		player.sendMessage(Legacy.tag + ChatColor.RED + "You do not have permission to use this command.");
	}

	public static boolean isRankingEnabled() {
		if (LinkServer.getPlugin().getConfig().contains("settings.ranking.enable")) {
			return LinkServer.getPlugin().getConfig().getBoolean("settings.ranking.enable");
		} else {
			return true;
		}
	}

	public static boolean isPrefixedRankingEnabled() {
		if (!isRankingEnabled())
			return false;
		if (LinkServer.getPlugin().getConfig().contains("settings.ranking.prefix")) {
			return LinkServer.getPlugin().getConfig().getBoolean("settings.ranking.prefix");
		} else {
			return true;
		}
	}

	public static boolean isOld(long timestamp, long timespan) {
		if (timestamp + timespan <= System.currentTimeMillis()) {
			return true;
		}
		return false;
	}

	public static boolean isPluginLoaded(String pluginName) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
		if (plugin == null)
			return false;
		return true;
	}

	public static boolean isPluginEnabled(String pluginName) {
		Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
		if (plugin == null)
			return false;
		if (plugin.isEnabled())
			return true;
		return false;
	}

	public static String formatPackageVersion(String version) {
		return version.replace("_", ".").substring(0, version.lastIndexOf("R") - 1).substring(1);
	}

	public static String formatTime(long seconds) {
		if (seconds < 60) {
			return seconds + "s";
		}
		long minutes = seconds / 60;
		long s = 60 * minutes;
		long secondsLeft = seconds - s;
		if (minutes < 60) {
			if (secondsLeft > 0) {
				return String.valueOf(minutes + "m " + secondsLeft + "s");
			}
			return String.valueOf(minutes + "m");
		}
		if (minutes < 1440) {
			String time = "";
			long hours = minutes / 60;
			time = hours + "h";
			long inMins = 60 * hours;
			long leftOver = minutes - inMins;
			if (leftOver >= 1) {
				time = time + " " + leftOver + "m";
			}
			if (secondsLeft > 0) {
				time = time + " " + secondsLeft + "s";
			}
			return time;
		}
		String time = "";
		long days = minutes / 1440;
		time = days + "d";
		long inMins = 1440 * days;
		long leftOver = minutes - inMins;
		if (leftOver >= 1) {
			if (leftOver < 60) {
				time = time + " " + leftOver + "m";
			} else {
				long hours = leftOver / 60;
				time = time + " " + hours + "h";
				long hoursInMins = 60 * hours;
				long minsLeft = leftOver - hoursInMins;
				if (leftOver >= 1) {
					time = time + " " + minsLeft + "m";
				}
			}
		}
		if (secondsLeft > 0) {
			time = time + " " + secondsLeft + "s";
		}
		return time;
	}

	public static String formatMonthId(int month) {
		switch (month % 12) {
			case 0:
				return "January";
			case 1:
				return "February";
			case 2:
				return "March";
			case 3:
				return "April";
			case 4:
				return "May";
			case 5:
				return "June";
			case 6:
				return "July";
			case 7:
				return "August";
			case 8:
				return "September";
			case 9:
				return "October";
			case 10:
				return "November";
			case 11:
				return "December";
			default:
				return null;
		}
	}

	public static String formatMonthIdAbbrev(int month) {
		switch (month % 12) {
			case 0:
				return "Jan.";
			case 1:
				return "Feb.";
			case 2:
				return "Mar.";
			case 3:
				return "Apr.";
			case 4:
				return "May";
			case 5:
				return "June";
			case 6:
				return "July";
			case 7:
				return "Aug.";
			case 8:
				return "Sept.";
			case 9:
				return "Oct.";
			case 10:
				return "Nov.";
			case 11:
				return "Dec.";
			default:
				return null;
		}
	}

	public static String formatDayOfMonthSuffix(int dayOfMonth) {
		if (dayOfMonth > 10 && dayOfMonth < 21)
			return "th";
		if (dayOfMonth % 10 == 1)
			return "st";
		if (dayOfMonth % 10 == 2)
			return "nd";
		if (dayOfMonth % 10 == 3)
			return "rd";
		return "th";
	}

	public static int retrieveMonthId(String month) {
		switch (month) {
			case "January":
				return 0;
			case "February":
				return 1;
			case "March":
				return 2;
			case "April":
				return 3;
			case "May":
				return 4;
			case "June":
				return 5;
			case "July":
				return 6;
			case "August":
				return 7;
			case "September":
				return 8;
			case "October":
				return 9;
			case "November":
				return 10;
			case "December":
				return 11;
			default:
				return -1;
		}
	}

	public static String basicFormatCalendarTime(Calendar cal) {
		return formatMonthId(cal.get(Calendar.MONTH)) + " " + cal.get(Calendar.DAY_OF_MONTH) + ", " + cal.get(Calendar.YEAR);
	}

	public static ItemStack createMaterial(Material material) {
		return createMaterial(material, 1, "unspecified", (short) 0, new String[0]);
	}

	public static ItemStack createMaterial(Material material, int amount) {
		return createMaterial(material, amount, "unspecified", (short) 0, new String[0]);
	}

	public static ItemStack createMaterial(Material material, int amount, short durability) {
		return createMaterial(material, amount, "unspecified", durability, new String[0]);
	}

	public static ItemStack createMaterial(Material material, int amount, String displayName) {
		return createMaterial(material, amount, displayName, (short) 0, new String[0]);
	}

	public static ItemStack createMaterial(Material material, String displayName) {
		return createMaterial(material, 1, displayName, (short) 0, new String[0]);
	}

	public static ItemStack createMaterial(Material material, int amount, String displayName, short durability, List<String> lore) {
		return createMaterial(material, amount, displayName, durability, lore.toArray(new String[0]));
	}

	public static ItemStack createMaterial(Material material, int amount, String displayName, short durability, String[] lore) {
		try {
			ItemStack itemStack = new ItemStack(material, amount, durability);
			if (displayName.equals("unspecified"))
				return itemStack;
			ItemMeta meta = itemStack.getItemMeta();
			meta.setDisplayName(displayName);
			if (lore.length > 0)
				meta.setLore(Arrays.asList(lore));
			itemStack.setItemMeta(meta);
			return itemStack;
		} catch (Exception ex) {
			ex.printStackTrace();
			return createMaterial(Material.AIR);
		}
	}

	public static ItemStack addLore(ItemStack stack, String[] lore) {
		ItemStack item = stack.clone();
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setLore(Arrays.asList(lore));
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack appendLore(ItemStack stack, String[] lore) {
		ItemStack item = stack.clone();
		ItemMeta itemMeta = item.getItemMeta();
		List<String> currLore = new ArrayList<String>();
		if (itemMeta.hasLore())
			currLore = itemMeta.getLore();
		for (String append : lore)
			currLore.add(append);
		itemMeta.setLore(currLore);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack appendLore(ItemStack stack, String lore) {
		return appendLore(stack, new String[]{lore});
	}

	public static ItemStack addEnchant(ItemStack stack, EnchantInfo enchantment) {
		ItemStack item = stack;
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addEnchant(enchantment.enchant, enchantment.power, true);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack addDamage(ItemStack stack, int damage) {
		ItemStack item = new ItemStack(stack.getType(), stack.getAmount(), (short) damage);
		return item;
	}

	public static ItemStack addLeatherColor(ItemStack stack, Color color) {
		if (color == null)
			return stack;
		ItemStack itm = stack;
		LeatherArmorMeta im = (LeatherArmorMeta) itm.getItemMeta();
		im.setColor(color);
		itm.setItemMeta(im);
		return itm;
	}

	public static Color getLeatherColor(ItemStack stack) {
		ItemStack itm = stack;
		LeatherArmorMeta im = (LeatherArmorMeta) itm.getItemMeta();
		return im.getColor();
	}

	public static ItemStack addBookEnchantment(ItemStack item, Enchantment enchantment, int level) {
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.addStoredEnchant(enchantment, level, true);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack setUnbreakable(ItemStack stack, boolean value) {
		ItemMeta meta = stack.getItemMeta();
		meta.setUnbreakable(value);
		stack.setItemMeta(meta);
		return stack;
	}

	public static boolean isShulkerBox(ItemStack stack) {
		if (stack.getType() == Material.BLACK_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.BLUE_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.BROWN_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.CYAN_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.GREEN_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.GRAY_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.LIGHT_BLUE_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.LIME_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.MAGENTA_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.ORANGE_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.PINK_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.PURPLE_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.RED_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.LIGHT_GRAY_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.WHITE_SHULKER_BOX)
			return true;
		if (stack.getType() == Material.YELLOW_SHULKER_BOX)
			return true;
		return false;
	}

	public static BlockFace getBlockFaceFromId(byte id) {
		switch (id) {
			case 2:
				return BlockFace.NORTH;
			case 5:
				return BlockFace.SOUTH;
			case 3:
				return BlockFace.WEST;
			case 4:
				return BlockFace.EAST;
			default:
				return null;
		}
	}


	public static String formatEnchantPower(int power) {
		if (power > 10) {
			return "STATE_UNKNOWN_POWER";
		} else if (power == 10) {
			return "X";
		} else if (power == 9) {
			return "IX";
		} else if (power == 8) {
			return "VIII";
		} else if (power == 7) {
			return "VII";
		} else if (power == 6) {
			return "VI";
		} else if (power == 5) {
			return "V";
		} else if (power == 4) {
			return "IV";
		} else if (power == 3) {
			return "III";
		} else if (power == 2) {
			return "II";
		} else if (power == 1) {
			return "I";
		} else {
			return "";
		}
	}

	public static String formatEnchantment(String enchantmentID, int enchantPower) {
		String power = formatEnchantPower(enchantPower);
		if (power.length() > 0)
			power = " " + power;
		if (enchantmentID.equals("PROTECTION_ENVIRONMENTAL")) {
			return "Protection" + power;
		} else if (enchantmentID.equals("PROTECTION_FALL")) {
			return "Feather Falling" + power;
		} else if (enchantmentID.equals("DIG_SPEED")) {
			return "Efficiency" + power;
		} else if (enchantmentID.equals("DAMAGE_ALL")) {
			return "Sharpness" + power;
		} else if (enchantmentID.equals("ARROW_DAMAGE")) {
			return "Power" + power;
		} else if (enchantmentID.equals("ARROW_KNOCKBACK")) {
			return "Punch" + power;
		} else if (enchantmentID.equals("ARROW_FIRE")) {
			return "Flame" + power;
		} else if (enchantmentID.equals("DURABILITY")) {
			return "Unbreaking" + power;
		} else if (enchantmentID.equals("LOOT_BONUS_BLOCKS")) {
			return "Fortune" + power;
		} else if (enchantmentID.equals("LOOT_BONUS_MOBS")) {
			return "Looting" + power;
		} else {
			return capitalizeAll(enchantmentID, "_") + " " + power;
		}
	}

	public static String unformatEnchantment(String enchantment) {
		if (enchantment.equalsIgnoreCase("Protection")) {
			return "PROTECTION_ENVIRONMENTAL";
		} else if (enchantment.equalsIgnoreCase("Feather Falling")) {
			return "PROTECTION_FALL";
		} else if (enchantment.equalsIgnoreCase("Efficiency")) {
			return "DIG_SPEED";
		} else if (enchantment.equalsIgnoreCase("Sharpness")) {
			return "DAMAGE_ALL";
		} else if (enchantment.equalsIgnoreCase("Power")) {
			return "ARROW_DAMAGE";
		} else if (enchantment.equalsIgnoreCase("Punch")) {
			return "ARROW_KNOCKBACK";
		} else if (enchantment.equalsIgnoreCase("Flame")) {
			return "ARROW_FIRE";
		} else if (enchantment.equalsIgnoreCase("Unbreaking")) {
			return "DURABILITY";
		} else if (enchantment.equalsIgnoreCase("Fortune")) {
			return "LOOT_BONUS_BLOCKS";
		} else if (enchantment.equalsIgnoreCase("Looting")) {
			return "LOOT_BONUS_MOBS";
		} else {
			return enchantment.toUpperCase().replace(" ", "_");
		}
	}

	public static String formatMaterial(Material mat) {
		return capitalizeAll(mat.toString(), "_");
	}

	public static String capitalizeAll(String str) {
		return capitalizeAll(str, " ");
	}

	public static String capitalizeAll(String str, String var) {
		String[] arr = str.split(var);
		StringBuilder sb = new StringBuilder();
		for (String s : arr) {
			sb.append(WordUtils.capitalize(s.toLowerCase()) + " ");
		}
		return sb.toString().trim();
	}

	public static String toDonorRankTag(String rank) {
		return toRankTag(rank).substring(0, toRankTag(rank).lastIndexOf(" ")) + ChatColor.RESET;
	}

	public static String toRankTag(String rank) {
		if (rank.equals("default"))
			return ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Member" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
		if (rank.equals("helper"))
			return ChatColor.RED + "[" + ChatColor.GRAY + "Helper" + ChatColor.RED + "] " + ChatColor.RED;
		if (rank.equals("developer"))
			return ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "Developer" + ChatColor.DARK_PURPLE + "] " + ChatColor.LIGHT_PURPLE;
		if (rank.equals("builder"))
			return ChatColor.GRAY + "[" + ChatColor.WHITE + "Builder" + ChatColor.GRAY + "] " + ChatColor.WHITE;
		if (rank.equals("moderator"))
			return ChatColor.RED + "[" + ChatColor.GRAY + "Moderator" + ChatColor.RED + "] " + ChatColor.RED;
		if (rank.equals("admin"))
			return ChatColor.RED + "[" + ChatColor.GRAY + "Admin" + ChatColor.RED + "] " + ChatColor.RED;
		if (rank.equals("manager"))
			return ChatColor.RED + "[" + ChatColor.GRAY + "Manager" + ChatColor.RED + "] " + ChatColor.RED;
		if (rank.equals("owner"))
			return ChatColor.RED + "[" + ChatColor.GRAY + "Owner" + ChatColor.RED + "] " + ChatColor.RED;
		if (rank.equals("founder"))
			return ChatColor.RED + "[" + ChatColor.GRAY + "Founder" + ChatColor.RED + "] " + ChatColor.RED;
		if (rank.equals("donator"))
			return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Donator" + ChatColor.DARK_GREEN + "] " + ChatColor.GRAY;
		if (rank.equals("youtube"))
			return ChatColor.GRAY + "[" + ChatColor.RED + "You" + ChatColor.WHITE + "Tube" + ChatColor.GRAY + "] " + ChatColor.RESET;
		if (rank.equals("redstone"))
			return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Redstone" + ChatColor.DARK_GREEN + "] " + ChatColor.GRAY;
		if (rank.equals("obsidian"))
			return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Obsidian" + ChatColor.DARK_GREEN + "] " + ChatColor.GRAY;
		if (rank.equals("bedrock"))
			return ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Bedrock" + ChatColor.DARK_GREEN + "] " + ChatColor.GRAY;
		return "";
	}

	public static String toRankDisplayName(String rank) {
		if (rank.equals("default"))
			return "member";
		if (rank.equals("helper"))
			return "helper";
		if (rank.equals("developer"))
			return "developer";
		if (rank.equals("builder"))
			return "builder";
		if (rank.equals("moderator"))
			return "moderator";
		if (rank.equals("admin"))
			return "admin";
		if (rank.equals("manager"))
			return "manager";
		if (rank.equals("owner"))
			return "owner";
		if (rank.equals("founder"))
			return "founder";
		if (rank.equals("donator"))
			return "donator";
		if (rank.equals("youtube"))
			return "youtube";
		if (rank.equals("redstone"))
			return "redstone";
		if (rank.equals("obsidian"))
			return "obsidian";
		if (rank.equals("bedrock"))
			return "bedrock";
		return "";
	}

	public static String getRank(UUID id) {
		if (isRankingEnabled()) {
			if (LinkServer.getSqlDatabase().contains("rank", id.toString())) {
				return LinkServer.getSqlDatabase().getString("rank", id.toString());
			} else {
				LinkServer.getSqlDatabase().set("rank", id.toString(), validRanks.get(0));
				return validRanks.get(0);
			}
		} else {
			return validRanks.get(0);
		}
	}

	public static String getRank(Player player) {
		return getRank(player.getUniqueId());
	}

	public static String getDonorRank(UUID id) {
		if (isRankingEnabled()) {
			if (LinkServer.getSqlDatabase().contains("donorRank", id.toString())) {
				return LinkServer.getSqlDatabase().getString("donorRank", id.toString());
			} else {
				LinkServer.getSqlDatabase().set("donorRank", id.toString(), validDonorRanks.get(0));
				return validDonorRanks.get(0);
			}
		} else {
			return validDonorRanks.get(0);
		}
	}

	public static String getDonorRank(Player player) {
		return getDonorRank(player.getUniqueId());
	}

	public static int getRankId(UUID id) {
		String rank = getRank(id);
		if (rank.equals("default") || !isRankingEnabled())
			return -1;
		if (rank.equals("helper") || rank.equals("developer") || rank.equals("builder"))
			return 0;
		if (rank.equals("moderator"))
			return 1;
		if (rank.equals("admin") || rank.equals("manager"))
			return 2;
		if (rank.equals("founder") || rank.equals("owner"))
			return 3;
		return -100;
	}

	public static int getRankId(Player player) {
		return getRankId(player.getUniqueId());
	}

	public static int getDonorRankId(UUID id) {
		String rank = getDonorRank(id);
		if (rank.equals("default") || rank.equals("youtube") || !isRankingEnabled())
			return -1;
		if (rank.equals("bedrock"))
			return -5;
		if (rank.equals("obsidian"))
			return -4;
		if (rank.equals("redstone"))
			return -3;
		if (rank.equals("donator"))
			return -2;
		return -100;
	}

	public static int getDonorRankId(Player player) {
		return getDonorRankId(player.getUniqueId());
	}

	public static String getLuckPermsRank(OfflinePlayer player) {
		Plugin luckPerms;
		if ((luckPerms = Bukkit.getPluginManager().getPlugin("LuckPerms")) == null) {
			return null;
		} else {
			return (LuckPerms.getApi().getUser(player.getUniqueId()).getPrimaryGroup()).toUpperCase();
		}
	}

	public static void flashPlayerDisplayName(Player player) {
		String rank = getRank(player);
		String donorRank = getDonorRank(player);
		String luckPermsRank = getLuckPermsRank(player);
		String luckyPrefix = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + luckPermsRank + ChatColor.DARK_GREEN + "] ";
		String rankColour = toRankTag(rank).substring(toRankTag(rank).lastIndexOf("] ") + 2);
		String donorRankColour = toRankTag(donorRank).substring(toRankTag(donorRank).lastIndexOf("] ") + 2);
		if (donorRank.equals("default")) {
			if (luckPermsRank == null) {
				player.setDisplayName(toRankTag(rank) + player.getName() + ChatColor.RESET);
			} else {
				player.setDisplayName(toRankTag(rank) + luckyPrefix + rankColour + player.getName() + ChatColor.RESET);
			}
		} else {
			if (rank.equals("default")) {
				if (luckPermsRank == null) {
					player.setDisplayName(toRankTag(donorRank) + player.getName() + ChatColor.RESET);
				} else {
					player.setDisplayName(toRankTag(donorRank) + luckyPrefix + donorRankColour + player.getName() + ChatColor.RESET);
				}
			} else {
				if (luckPermsRank == null) {
					player.setDisplayName(toRankTag(rank) + player.getName() + " " + toDonorRankTag(donorRank) + ChatColor.RESET);
				} else {
					player.setDisplayName(toRankTag(rank) + luckyPrefix + rankColour + player.getName() + " " + toDonorRankTag(donorRank) + ChatColor.RESET);
				}
			}
		}
		player.setPlayerListName(player.getDisplayName());
	}

	public static String getFlashPlayerDisplayName(OfflinePlayer player) {
		String rank = getRank(player.getUniqueId());
		String donorRank = getDonorRank(player.getUniqueId());
		String luckPermsRank = getLuckPermsRank(player);
		String luckyPrefix = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + luckPermsRank + ChatColor.DARK_GREEN + "] ";
		String rankColour = toRankTag(rank).substring(toRankTag(rank).lastIndexOf("] ") + 2);
		String donorRankColour = toRankTag(donorRank).substring(toRankTag(donorRank).lastIndexOf("] ") + 2);
		if (donorRank.equals("default")) {
			if (luckPermsRank == null) {
				return toRankTag(rank) + player.getName() + ChatColor.RESET;
			} else {
				return toRankTag(rank) + luckyPrefix + rankColour + player.getName() + ChatColor.RESET;
			}
		} else {
			if (rank.equals("default")) {
				if (luckPermsRank == null) {
					return toRankTag(donorRank) + player.getName() + ChatColor.RESET;
				} else {
					return toRankTag(donorRank) + luckyPrefix + donorRankColour + player.getName() + ChatColor.RESET;
				}
			} else {
				if (luckPermsRank == null) {
					return toRankTag(rank) + player.getName() + " " + toDonorRankTag(donorRank) + ChatColor.RESET;
				} else {
					return toRankTag(rank) + luckyPrefix + rankColour + player.getName() + " " + toDonorRankTag(donorRank) + ChatColor.RESET;
				}
			}
		}
	}

	public static String getFlashPlayerDisplayName(String playerName) {
		UUID id = UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes());
		if (Bukkit.getOnlineMode())
			id = UUID.fromString(CraftGo.Player.getUUID(playerName, true));
		String rank = getRank(id);
		String donorRank = getDonorRank(id);
		String luckPermsRank = getLuckPermsRank(CraftGo.Player.getOfflinePlayer(id));
		String luckyPrefix = ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + luckPermsRank + ChatColor.DARK_GREEN + "] ";
		String rankColour = toRankTag(rank).substring(toRankTag(rank).lastIndexOf("] ") + 2);
		String donorRankColour = toRankTag(donorRank).substring(toRankTag(donorRank).lastIndexOf("] ") + 2);
		if (donorRank.equals("default")) {
			if (luckPermsRank == null) {
				return toRankTag(rank) + playerName + ChatColor.RESET;
			} else {
				return toRankTag(rank) + luckyPrefix + rankColour + playerName + ChatColor.RESET;
			}
		} else {
			if (rank.equals("default")) {
				if (luckPermsRank == null) {
					return toRankTag(donorRank) + playerName + ChatColor.RESET;
				} else {
					return toRankTag(donorRank) + luckyPrefix + donorRankColour + playerName + ChatColor.RESET;
				}
			} else {
				if (luckPermsRank == null) {
					return toRankTag(rank) + playerName + " " + toDonorRankTag(donorRank) + ChatColor.RESET;
				} else {
					return toRankTag(rank) + luckyPrefix + rankColour + playerName + " " + toDonorRankTag(donorRank) + ChatColor.RESET;
				}
			}
		}
	}

	public static class Legacy {
		public static String tag = ChatColor.GRAY + "[" + ChatColor.RESET + "Minecraft" + ChatColor.GRAY + "] " + ChatColor.RESET;
	}

}
