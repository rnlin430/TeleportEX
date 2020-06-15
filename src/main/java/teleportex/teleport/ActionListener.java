package teleportex.teleport;

import com.github.rnlin.rnlibrary.ConsoleLog;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginEnableEvent;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ActionListener implements Listener {
    private TeleportPlugin plugin;

    private static final List<Function<String, Boolean>> commandOnEventWorldList = new ArrayList<>();
    private static final List<Function<String, Boolean>> commandOnSurvivalWorldList = new ArrayList<>();
    private static final Map<String, Region> deathRegionMap     = new HashMap<>();
    private static final Map<String, Region> warpRegionMap      = new HashMap<>();
    private static final String[] DEATH_REGION_NAMES            = {"floor", "lv1", "lv2", "lv3", "lv4", "lv5", "boss"};
    private static final String[] WARP_REGION_NAMES             = {"floor-spawn", "spawn-floor"};
    private static final String WORLD_ERROR_MESSAGE = "の読み込みが失敗している可能性があります。";
    private static boolean initFlag = true;

    public ActionListener(TeleportPlugin p) {
        this.plugin = p;
        p.getServer().getPluginManager().registerEvents(this, p);
        // init();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double x = p.getLocation().getX();
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();
        for (String s : warpRegionMap.keySet()) {
            if (warpRegionMap.get(s).contains(x, y, z)) {
                // System.out.println("[:52] ");
                if (warpRegionMap.get(s).getWorldName() == p.getWorld().getName()) {
                    Event event = new PlayerEnterWarpRegionEvent(p, p.getLocation(), s, warpRegionMap.get(s));
                    Bukkit.getPluginManager().callEvent(event);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerWarpGate(PlayerEnterWarpRegionEvent e) {
        Player p = e.getPlayer();
        String rname = e.getRegionName();
        if (rname.equalsIgnoreCase(WARP_REGION_NAMES[0])) {
            if (TeleportPlugin.isEmptyInventory(p)) {
                plugin.dispatchCommandByOperator(p, "spawn");
            } else {
                plugin.teleport(p, "abc", 13D , 28D , 113D);
                p.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[5]);
                p.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[6]);
            }
            return;
        }else if (rname.equalsIgnoreCase(WARP_REGION_NAMES[1])) {
            if (TeleportPlugin.isEmptyInventory(p)) {
                plugin.teleport(p, "abc", 12.5D, 240D, 106.5D, 90F, 90F);
            } else {
                plugin.dispatchCommandByOperator(p, "spawn");
                p.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[1]);
                p.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[2]);
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        //System.out.println(e.getPlayer().getWorld().getName());
        if (e.getPlayer().isOp()) return;
        String msg = e.getMessage();
        for (Function<String, Boolean> c : commandOnSurvivalWorldList) {
// System.out.println("ActionListener:92");
            if (c.apply(msg)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.GRAY + "申し訳ありません。\nイベント期間中はwarpコマンドは使えません。");
                return;
            }
        }
        World ew = plugin.getServer().getWorld(TeleportPlugin.EVENT_WORLD_NAME);
        World bw = plugin.getServer().getWorld(TeleportPlugin.BOSS_WORLD_NAME);
        ConsoleLog.sendDebugMessage("onCommand");
        ConsoleLog.sendDebugMessage("e.getPlayer().getWorld()=" + e.getPlayer().getWorld() +"\new=" + ew + " bw=" + bw);
        boolean b = false;
        b |= e.getPlayer().getWorld() == ew;
        b |= e.getPlayer().getWorld() == bw;
        if (!b) return;
        ConsoleLog.sendDebugMessage("onCommand#if(e.getPlayer().getWorld() != ew...)");
        for (Function<String, Boolean> c : commandOnEventWorldList) {
// System.out.println("ActionListener:103");
            if (c.apply(msg)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "不思議な力で封じられてしまっている・・・！");
                return;
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent e) throws IllegalArgumentException {
        ConsoleLog.sendDebugMessage("onPlayerRespawn");
        World ew = Objects.requireNonNull(
                plugin.getServer().getWorld(TeleportPlugin.EVENT_WORLD_NAME), "onPlayerRespawn" + WORLD_ERROR_MESSAGE);
        World bw = Objects.requireNonNull(
                plugin.getServer().getWorld(TeleportPlugin.BOSS_WORLD_NAME), "onPlayerRespawn" + WORLD_ERROR_MESSAGE);
        Player p = e.getPlayer();

        boolean b = false;
        b |= e.getPlayer().getWorld() == ew;
        b |= e.getPlayer().getWorld() == bw;
        if (!b) return;

        for (String s : deathRegionMap.keySet()) {
            if (
                    deathRegionMap.get(s).contains(
                            p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getWorld()
                    )
            ) {
                ConsoleLog.sendDebugMessage("onPlayerRespawn#if (deathRegionMap.get(s).contains(x, y, z))");
                if (s.equalsIgnoreCase(DEATH_REGION_NAMES[1])) {
                    ConsoleLog.sendDebugMessage("onPlayerRespawn#if (...DEATH_REGION_NAMES[1])");
                    e.setRespawnLocation(new Location(ew, 12.5D, 28D, 106.5D, 90F, 0F));
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[2])) {
                    ConsoleLog.sendDebugMessage("onPlayerRespawn#if (...DEATH_REGION_NAMES[2])");
                    e.setRespawnLocation(new Location(ew, -152D, 10D, 112D, -126F, 11F));
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[3])) {
                    ConsoleLog.sendDebugMessage("onPlayerRespawn#if (...DEATH_REGION_NAMES[3])");
                    e.setRespawnLocation(new Location(ew, -238.5D, 30D, 101.5D, 90F, 0F));
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[4])) {
                    ConsoleLog.sendDebugMessage("onPlayerRespawn#if (...DEATH_REGION_NAMES[4])");
                    e.setRespawnLocation(new Location(ew, -266.5D, 21D, 192.5D, 0F, 0F));
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[5])) {
                    ConsoleLog.sendDebugMessage("onPlayerRespawn#if (...DEATH_REGION_NAMES[5])");
                    e.setRespawnLocation(new Location(ew, -167.5D, 6D, -61.5D, 180F, 0F));
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[6])) {
                    ConsoleLog.sendDebugMessage("onPlayerRespawn#if (...DEATH_REGION_NAMES[6])");
                    e.setRespawnLocation(new Location(ew, 12.5D, 28D, 106.5D, 90F, 78.3F));
                    return;
                }
            }
        }
        e.setRespawnLocation(new Location(ew, 12D, 28D, 106D, 90F, 0F));
    }

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) return;
        Player p = (Player) entity;

        World ew = Objects.requireNonNull(
                plugin.getServer().getWorld(TeleportPlugin.EVENT_WORLD_NAME), "onEntityTeleport" + WORLD_ERROR_MESSAGE);
        World bw = Objects.requireNonNull(
                plugin.getServer().getWorld(TeleportPlugin.BOSS_WORLD_NAME), "onEntityTeleport" + WORLD_ERROR_MESSAGE);

        boolean b = false;
        b |= p.getWorld() == ew;
        b |= p.getPlayer().getWorld() == bw;
        if (!b) return;

        for (String s : deathRegionMap.keySet()) {
            if (
                    deathRegionMap.get(s).contains(
                            p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), p.getWorld()
                    )
            ) {
                ConsoleLog.sendDebugMessage("onEntityTeleport#if (deathRegionMap.get(s).contains(x, y, z))");
                if (s.equalsIgnoreCase(DEATH_REGION_NAMES[1])) {
                    ConsoleLog.sendDebugMessage("onEntityTeleport#if (...DEATH_REGION_NAMES[1])");
                    plugin.getPlayerStatusData().getConfig().set(p.getUniqueId().toString() + "." + "1", true);
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[2])) {
                    ConsoleLog.sendDebugMessage("onEntityTeleport#if (...DEATH_REGION_NAMES[2])");
                    plugin.getPlayerStatusData().getConfig().set(p.getUniqueId().toString() + "." + "2", true);
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[3])) {
                    ConsoleLog.sendDebugMessage("onEntityTeleport#if (...DEATH_REGION_NAMES[3])");
                    plugin.getPlayerStatusData().getConfig().set(p.getUniqueId().toString() + "." + "3", true);
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[4])) {
                    ConsoleLog.sendDebugMessage("onEntityTeleport#if (...DEATH_REGION_NAMES[4])");
                    plugin.getPlayerStatusData().getConfig().set(p.getUniqueId().toString() + "." + "4", true);
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[5])) {
                    ConsoleLog.sendDebugMessage("onEntityTeleport#if (...DEATH_REGION_NAMES[5])");
                    plugin.getPlayerStatusData().getConfig().set(p.getUniqueId().toString() + "." + "5", true);
                    return;
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[6])) {
                    ConsoleLog.sendDebugMessage("onEntityTeleport#if (...DEATH_REGION_NAMES[6])");
                    plugin.getPlayerStatusData().getConfig().set(p.getUniqueId().toString() + "." + "boss", true);
                    return;
                }
                plugin.getPlayerStatusData().saveConfig();
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        World ew = plugin.getServer().getWorld(TeleportPlugin.EVENT_WORLD_NAME);
        World bw = plugin.getServer().getWorld(TeleportPlugin.BOSS_WORLD_NAME);
        boolean b = false;
        b |= p.getWorld() == ew;
        b |= p.getWorld() == bw;
        if (!b) return;
        p.sendMessage(TeleportPlugin.MESSAGE[7]);
        e.setKeepLevel(true);
        e.setKeepInventory(true);
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        TeleportPlugin.playerLogoutProcess(p);
    }

    @EventHandler
    public void onPlayerAccess(AsyncPlayerPreLoginEvent e) {
        if (initFlag) {
            boolean b = false;
            b |= plugin.getServer().getWorld(TeleportPlugin.BOSS_WORLD_NAME) == null;
            b |= plugin.getServer().getWorld(TeleportPlugin.EVENT_WORLD_NAME) == null;
            if (b) return;
//System.out.println("onPlayerAccess#if(initFlag)");
            plugin.getPlayerData().saveDefaultConfig();
            plugin.getPlayerData().saveConfig();
            init();
            initFlag = false;
        }
    }

    @EventHandler
    public void onEnnablePlugin(PluginEnableEvent e) {
        if (initFlag) {
            boolean b = false;
            b |= plugin.getServer().getWorld(TeleportPlugin.BOSS_WORLD_NAME) == null;
            b |= plugin.getServer().getWorld(TeleportPlugin.EVENT_WORLD_NAME) == null;
            if (b) return;
//System.out.println("onEnnablePlugin#if(initFlag)");
            plugin.getPlayerData().saveDefaultConfig();
            plugin.getPlayerData().saveConfig();
            init();
            initFlag = false;
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {
        ConsoleLog.sendDebugMessage("onPlayerLogin");
        Player p = e.getPlayer();
        World w = plugin.getServer().getWorld(TeleportPlugin.EVENT_WORLD_NAME);
        plugin.getPlayerData().reloadConfig();
        FileConfiguration cf = plugin.getPlayerData().getConfig();
        if (!cf.contains(p.getUniqueId() + ".GAME")) return;
        if (!cf.contains(p.getUniqueId() + ".LOCATION")) return;
        ConsoleLog.sendDebugMessage("onPlayerLogin#if(!cf.contains...):231");
        if (cf.getBoolean(p.getUniqueId() + ".GAME")) {
            Object obj = cf.get(p.getUniqueId() + ".LOCATION");
            if (obj.getClass().getName().equalsIgnoreCase("org.bukkit.Location")) {
                Location loc = (Location) obj;
                if (p.teleport(loc)) {
                    TeleportPlugin.restorePlayerInventory(p);
                } else {
                    ConsoleLog.sendWarning(p.getName() + " は死亡しています。転送できないためリスポーン地点にワープします。");
                    p.spigot().respawn();
                    if (p.getWorld().getName().equalsIgnoreCase(TeleportPlugin.EVENT_WORLD_NAME) || p.getWorld().getName().equalsIgnoreCase(TeleportPlugin.BOSS_WORLD_NAME)) {
                        TeleportPlugin.restorePlayerInventory(p);
                    } else {
                        ConsoleLog.sendWarning(p.getName() + " のリスポーン地点へのワープを試みましたがイベントワールドが見つからなかったため失敗しました。");
                    }

                }

            } else {
                throw new ClassCastException();
            }
//
//            for (String key : Objects.requireNonNull(
//                    cf.getConfigurationSection(p.getUniqueId().toString()), p.getName() + "の復元位置がみつかりませんでした。").getConfigurationSection("LOCATION").getKeys(false)) {
//                String value = cf.getString(p.getUniqueId() + ".LOCATION" + "." + key);
//                hashMap.put(key, value);
//            }
//            Location loc = Location.deserialize(hashMap);
//            p.teleport(loc);
        }
    }

//    public static <T> T autoCast(Object obj) {
//        T castObj = (T) obj;
//        return castObj;
//    }


    // MAKE CLOSER REGULAR EXPRESSION
    private static Function<String, Boolean> makePatternCheckFunc(String regex) {
        Pattern p = Pattern.compile(regex);
        Function<String, Boolean> fc = (target) -> p.matcher(target).find();
        return fc;
    }

    private static void init() {
        commandOnEventWorldList.add(makePatternCheckFunc("^/spawn$"));
        commandOnEventWorldList.add(makePatternCheckFunc("^/home.*"));
        commandOnEventWorldList.add(makePatternCheckFunc("^/tpa.*"));
        commandOnEventWorldList.add(makePatternCheckFunc("^/tpyes$"));
        commandOnEventWorldList.add(makePatternCheckFunc("^/tpaccept$"));
        commandOnEventWorldList.add(makePatternCheckFunc("^/tpahere.*"));
        commandOnEventWorldList.add(makePatternCheckFunc("^/warp.*"));
        commandOnSurvivalWorldList.add(makePatternCheckFunc("^/warp abc$"));
        commandOnSurvivalWorldList.add(makePatternCheckFunc("^/warp boss$"));
        if(Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME) != null) {
            // CREATE THE DEATH REGION MAP
            deathRegionMap.put(
                    DEATH_REGION_NAMES[1],
                    new Region(-119D, 27D, 50D, 27D, 42D, 158D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            deathRegionMap.put(
                    DEATH_REGION_NAMES[2],
                    new Region(-163D, 3D, 151D, -12D, 26D, 54D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            deathRegionMap.put(
                    DEATH_REGION_NAMES[3],
                    new Region(-223D, 22D, 152D, -398D, 46D, 34D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            deathRegionMap.put(
                    DEATH_REGION_NAMES[4],
                    new Region(-319D, 35D, 174D, -156D, 3D, 351D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            deathRegionMap.put(
                    DEATH_REGION_NAMES[5],
                    new Region(-60D, 3D, -39D, -242D, 55D, -212D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            deathRegionMap.put(
                    DEATH_REGION_NAMES[6],
                    new Region(142D, 10D, -142D, -108D, 234D, 169D, Bukkit.getWorld(TeleportPlugin.BOSS_WORLD_NAME))
            );
        } else {
            throw new NullPointerException("[" + TeleportPlugin.EVENT_WORLD_NAME + "] が見つからなかったためDEATH_REGIONを作成できませんでした。");
        }
        if(Bukkit.getWorld(TeleportPlugin.SURVIVAL_WORLD_NAME) != null | Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME) != null ) {
            // CREATE THE WARP REGION MAP
            warpRegionMap.put(
                    WARP_REGION_NAMES[0],
                    new Region(21D, 28D, 99D, 21D, 32D, 103D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            warpRegionMap.put(
                    WARP_REGION_NAMES[1],
                    new Region(14993D, 61D, -14993D, 14997D, 58D, -14989D, Bukkit.getWorld(TeleportPlugin.SURVIVAL_WORLD_NAME))
            );
        } else {
            throw new NullPointerException("[" + TeleportPlugin.SURVIVAL_WORLD_NAME + "] が見つからなかったため領域を一部作成できませんでした。");
        }
    }

}
