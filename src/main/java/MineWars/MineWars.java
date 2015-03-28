package MineWars;

import java.awt.List;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MineWars extends JavaPlugin implements Listener{
	public final Map<Player, String> playerList = new ConcurrentHashMap<Player, String>();
	public final Map<Player, String> pvpList = new ConcurrentHashMap<Player, String>();
	public final Map<Location, String> signLoc = new ConcurrentHashMap<Location, String>();
	public final Map<String, Integer> teamSize = new ConcurrentHashMap<String, Integer>();
	public final Map<Player, Location> selection = new ConcurrentHashMap<Player, Location>();
	public final Map<Player, Location> selection2 = new ConcurrentHashMap<Player, Location>();
	public final Map<Player, String> selectionList = new ConcurrentHashMap<Player, String>();
	public Integer timer = 120;
	@SuppressWarnings("deprecation")
	public void onEnable() {
		Logger logger = Logger.getLogger("Minecraft");
		teamSize.put("red", 0);
		teamSize.put("blue", 0);
		logger.info("[MineWars] MineWars has been enabled!");
		if (!(getConfig().contains("max_players"))) {
			getConfig().set("max_players", 40);
		}
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				MineUpdate();
			}
		}, 2400L, 2400L);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
			public void run() {
				if (timer == 60) {
					for (Player player : playerList.keySet()){
						player.sendMessage("[MineWars] " + ChatColor.RED + ("The mine will reset in " + timer + " seconds!"));
					}
				}
				if (timer == 30) {
					for (Player player : playerList.keySet()){
						player.sendMessage("[MineWars] " + ChatColor.RED + ("The mine will reset in " + timer + " seconds!"));
					}
				}
				if (timer == 20) {
					for (Player player : playerList.keySet()){
						player.sendMessage("[MineWars] " + ChatColor.RED + ("The mine will reset in " + timer + " seconds!"));
					}
				}
				if (timer < 11) {
					for (Player player : playerList.keySet()){
						player.sendMessage("[MineWars] " + ChatColor.RED + ("The mine will reset in " + timer + " seconds!"));
					}
				}
				if (timer < 1) {
					timer = 120;
				}
				timer --;
			}
		}, 0L, 20L);
	}
	public void onDisable() {
		Logger logger = Logger.getLogger("Minecraft");
		logger.info("[MineWars] MineWars has been disabled!");
	}
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		player.sendMessage("made it");
		Block sign = event.getBlock();
		if (event.getLine(0).equalsIgnoreCase("[MineWars]")) {
			if (player.isOp()) {
				if (event.getLine(1).equalsIgnoreCase("Join")) {
					event.setLine(0, ChatColor.DARK_GREEN + "[MineWars]");
					signLoc.put(sign.getLocation(), "join");
					event.setLine(1, ChatColor.DARK_BLUE + "[Join]");
					event.setLine(2, ChatColor.GOLD + (("" + playerList.size()) + "/" + ("" + getConfig().getInt("max_players"))));
					player.sendMessage("[MineWars] " + ChatColor.BLUE + "MineWars join sign succesfully created!");
				}
				else if (event.getLine(1).equalsIgnoreCase("Exit")) {
					event.setLine(0, ChatColor.DARK_GREEN + "[MineWars]");
					signLoc.put(sign.getLocation(), "exit");
					event.setLine(1, ChatColor.DARK_BLUE + "[Exit]");
					player.sendMessage("[MineWars] " + ChatColor.BLUE + "MineWars exit sign succesfully created!");
				}
			}
			player.sendMessage("[MineWars] " + ChatColor.DARK_RED + "Only Ops can create MineWars signs!");
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteractBlock(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		if (block != null) {
			if (block.getType().getId() == 63 || block.getType().getId() == 68 || block.getType().getId() == 323) {
				if (signLoc.containsKey(block.getLocation())) {
					String value = signLoc.get(block.getLocation());
					if (value.equals("join")) {
						if (playerList.size() > getConfig().getInt("arena_size")) {
							player.sendMessage("[MineWars] " + ChatColor.DARK_RED + "MineWars is full!");
							return;
						}
						if (playerList.containsKey(player)) {
							player.sendMessage("[MineWars] " + ChatColor.DARK_RED + "You are already in MineWars!");
							return;
						}
						
						else {
							Location loc = new Location(getServer().getWorld(getConfig().getString("arena_world")), 
									getConfig().getDouble("arena_x"),
									getConfig().getDouble("arena_y"),
									getConfig().getDouble("arena_z"));
							player.teleport(loc);
							if (teamSize.get("red") > teamSize.get("blue")) {
								String team = "blue";
								playerList.put(player, team);
								int  i = teamSize.get("blue");
								teamSize.remove("blue");
								teamSize.put("blue", i + 1);
							}
							if (teamSize.get("red") < teamSize.get("blue")) {
								String team = "red";
								playerList.put(player, team);
								int  i = teamSize.get("red");
								teamSize.remove("red");
								teamSize.put("red", i + 1);
							}
							else {
								String team = "red";
								playerList.put(player, team);
								int  i = teamSize.get("red");
								teamSize.remove("red");
								teamSize.put("red", i + 1);
							}
							player.sendMessage("[MineWars] " + ChatColor.BLUE + "You have succesfully joined MineWars!");
							player.sendMessage("[MineWars] " + ChatColor.BLUE + ("You joined the " + playerList.get(player) + " team!"));
							return;
						}
					}
					else if (value.equals("exit")) {
						if (!(playerList.containsKey(player))) {
							player.sendMessage("[MineWars] " + ChatColor.DARK_RED + "You are not currently in MineWars!");
							return;
						}
						else {
							playerList.remove(player);
							player.sendMessage("[MineWars] " + ChatColor.BLUE + "You have succesfully exited MineWars!");
							return;
						}
					}
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (block.getType().getId() == 63||block.getType().getId() == 68||block.getType().getId() == 323) {
			if (signLoc.containsKey(block.getLocation())) {
				if (event.getPlayer().isOp()) {
					signLoc.remove(block.getLocation());
					event.getPlayer().sendMessage("[MineWars] " + ChatColor.BLUE + "MineWars sign succesfully removed!");
					return;
				}
				event.getPlayer().sendMessage("[MineWars] " + ChatColor.DARK_RED + "Only Ops can remove MineWars signs!");
				event.setCancelled(true);
				return;
			}
		}
		Player player = event.getPlayer();
		if (selectionList.containsKey(player)){
			select(player, block);
			event.setCancelled(true);
		}
	}
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		Entity player1 = event.getEntity();
		Entity player2 = event.getDamager();
		if (player1 instanceof Player && player2 instanceof Player) {
			player1 = (Player) player1;
			player2 = (Player) player2;
			if (playerList.containsKey(player1) && playerList.containsKey(player2)) {
				if (pvpList.containsKey(player1) && pvpList.containsKey(player2)) {
					if (playerList.get(player1).equals(playerList.get(player2))) {
						event.setCancelled(true);
						return;
					}
					return;
				}
				event.setCancelled(true);
			}
		}
	}
	public void select(Player player, Block block) {
		Location loc = block.getLocation();
		if (player == null) {
			return;
		}
		if (selection.containsKey(player)) {
			if (selection2.containsKey(player)) {
				selection2.remove(player);
			}
			selection2.put(player, block.getLocation());
			player.sendMessage("[MineWars] " + ChatColor.BLUE + ("Second block has been set to " + loc.getX() + ", " +  loc.getY() +", " + loc.getZ() + "!"));
			return;
		}
		selection.put(player, block.getLocation());
		player.sendMessage("[MineWars] " + ChatColor.BLUE + ("First block has been set to " + loc.getX() + ", " +  loc.getY() +", " + loc.getZ() + "!"));
	}
	public boolean MineCreate(Player player, String[] args) {
		if (player.isOp()) {
			if (selection.containsKey(player) && selection2.containsKey(player)) {
				Location loc = selection.get(player);
				Location loc2 = selection2.get(player);
				if (args.length > 0) {
					getConfig().set("mineX1", loc.getX());
					getConfig().set("mineY1", loc.getY());
					getConfig().set("mineZ1", loc.getZ());
					getConfig().set("mineX2", loc2.getX());
					getConfig().set("mineY2", loc2.getY());
					getConfig().set("mineZ2", loc2.getZ());
					getConfig().set("mineWorld", loc.getWorld().getName());
					saveConfig();
					reloadConfig();
					selection.remove(player);
					selection2.remove(player);
					player.sendMessage("[MineWars] " + ChatColor.DARK_RED + "Mine succesfully created!");
					MineUpdate();
					return true;
				}
				player.sendMessage("[MineWars] " + ChatColor.DARK_RED + "Invalid number of arguments!");
				return false;
			}
			player.sendMessage("[MineWars] " + ChatColor.DARK_RED + "You have not selected any locations!");
			return false;
		}
		player.sendMessage("[MineWars] " + ChatColor.DARK_RED + "Only Ops can create mines!");
		return false;
	}
	@SuppressWarnings("deprecation")
	public void MineUpdate() {
		ArrayList<Material> matList = new ArrayList<Material>(); 
		for (int s = 0; s < 40; s ++) {
			matList.add(Material.STONE);
		}
		for (int s = 0; s < 1; s ++) {
			matList.add(Material.DIAMOND_ORE);
		}
		for (int s = 0; s < 3; s ++) {
			matList.add(Material.IRON_ORE);
		}
		for (int s = 0; s < 7; s ++) {
			matList.add(Material.COAL_ORE);
		}
		for (int s = 0; s < 4; s ++) {
			matList.add(Material.GOLD_ORE);
		}
		for (int s = 0; s < 1; s ++) {
			matList.add(Material.OBSIDIAN);
		}
		for (int s = 0; s < 1; s ++) {
			matList.add(Material.OBSIDIAN);
		}
		if (!(getConfig().contains("mineWorld"))) {
			return;
		}
		int X1 = getConfig().getInt("mineX1");
		int Y1 = getConfig().getInt("mineY1");
		int Z1 = getConfig().getInt("mineZ1");
		int X2 = getConfig().getInt("mineX2");
		int Y2 = getConfig().getInt("mineY2");
		int Z2 = getConfig().getInt("mineZ2");
		World world = getServer().getWorld(getConfig().getString("mineWorld"));
		int i = Math.abs(X1 - X2);
		for (int x  = i; x >= 0; x--) {
			i = Math.abs(Y1 - Y2);
			for (int y = i; y >= 0; y --) {
				i = Math.abs(Z1 - Z2);
				for (int z = i; z >= 0; z --) {
					Location blockloc = new Location(world, 
							Math.max(X1, X2) - x, Math.max(Y1, Y1) - y, Math.max(Z1, Z2) - z);
					Block block = world.getBlockAt(blockloc);
					Random ran = new Random();
					int randomNum = ran.nextInt(matList.size() - 1);
					block.setType((Material) matList.toArray()[randomNum]);
				}
			}
		}
	}
	public boolean setArenaSpawn(Player player, String[] args) {
		if (player.isOp()) {
			Location loc = player.getLocation();
			getConfig().set("arena_x", loc.getX());
			getConfig().set("arena_y", loc.getY());
			getConfig().set("arena_z", loc.getZ());
			getConfig().set("arena_world", loc.getWorld().getName());
			saveConfig();
			reloadConfig();
			player.sendMessage("[MineWars] " + ChatColor.BLUE + "The arena spawnpoint was succesfully set!");
			return true;
		}
		player.sendMessage("[MineWars] " + ChatColor.DARK_RED + "Only Ops can set the arena spawnpoint!");
		return false;
	}
	public boolean onCommand(CommandSender player1, Command cmd, String label, String[] args) {
		Player player = (Player) player1;
		if (label.equalsIgnoreCase("MWars")) {
			if (!(args.length > 0)) {
				return false;
			}
			if (args[0].equalsIgnoreCase("createmine")) {
				MineCreate(player, args);
				return true;
			}
			if (args[0].equalsIgnoreCase("select")) {
				player.sendMessage("[MineWars] " + ChatColor.BLUE + "You have been put into selection mode!");
				return true;
			}
			if (args[0].equalsIgnoreCase("setarenaspawn") ) {
				setArenaSpawn(player, args);
			}
		}
		return false;
	}
}