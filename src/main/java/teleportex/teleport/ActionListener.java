package teleportex.teleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ActionListener implements Listener {
    private TeleportPlugin plugin;

    private static final List<Function<String, Boolean>> cList  = new ArrayList<>();
    private static final Map<String, Region> deathRegionMap     = new HashMap<>();
    private static final Map<String, Region> warpRegionMap      = new HashMap<>();
    private static final String[] DEATH_REGION_NAMES            = new String[]{"floor", "lv1", "lv2", "lv3", "lv4", "lv5"};
    private static final String[] WARP_REGION_NAMES             = new String[]{"floor-spawn", "spawn-floor"};
    private static final BiFunction<String, Boolean, Boolean> existedPlayerInRegion = makeExistedPlayerInRegionFunc();


    public ActionListener(TeleportPlugin p ) {
        this.plugin = p;
        p.getServer().getPluginManager().registerEvents(this, p);
        // init();
    }
    static {
        init();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double x = p.getLocation().getX();
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();
        for (String s : warpRegionMap.keySet()) {
            if (warpRegionMap.get(s).contains(x, y, z)) {
                if (warpRegionMap.get(s).getWorldName() == p.getWorld().getName()) {
                    if (existedPlayerInRegion.apply(p.getName(), true)) {
                    } else {
                        Event event = new PlayerEnterWarpRegionEvent(p, p.getLocation(), s, warpRegionMap.get(s));
                        Bukkit.getPluginManager().callEvent(event);
                    }
                } else {
                    existedPlayerInRegion.apply(p.getName(), false);
                }
            } else {
                existedPlayerInRegion.apply(p.getName(), false);
            }
        }
    }

    // This is closure to check that player was in the WARP-REGION just before.
    private static BiFunction<String, Boolean, Boolean> makeExistedPlayerInRegionFunc() {
        Map<String, Boolean> hashMap = new HashMap<>();
        BiFunction<String, Boolean, Boolean> fc = (name, b) -> {
            if (hashMap.containsKey(name)) {
                if(!b) {
                    hashMap.remove(name);
                }
                System.out.println("hashMap.containsKey(name)=true");
                return true;
            } else {
                if(b) {
                    hashMap.put(name, b);
                }
                System.out.println("hashMap.containsKey(name)=false");
                return false;
            }
        };
        return fc;
    }

    @EventHandler
    public void onPlayerWarpGate(PlayerEnterWarpRegionEvent e) {
        Player p = e.getPlayer();
        String rname = e.getRegionName();
        if (rname.equalsIgnoreCase(WARP_REGION_NAMES[0])) {
            if (TeleportPlugin.isEmptyInventory(p)) {
                plugin.dispatchCommandByOperator(p, "spawn");
            } else {
                p.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[1]);
                p.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[2]);
            }
            return;
        }else if (rname.equalsIgnoreCase(WARP_REGION_NAMES[1])) {
            if (TeleportPlugin.isEmptyInventory(p)) {
                plugin.dispatchCommandByOperator(p, "spawn");
            } else {
                p.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[1]);
                p.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[2]);
            }
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        //System.out.println(e.getPlayer().getWorld().getName());
        World w = plugin.getServer().getWorld("abc");
        if (e.getPlayer().isOp()) return;
        if (w == null) return;
        if (e.getPlayer().getWorld() != w) return;
        String msg = e.getMessage();
        for (Function<String, Boolean> c : cList) {
            System.out.println("ActionListener:46");
            if (c.apply(msg)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "不思議な力で封じられているようだ・・・！");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent e) throws IllegalArgumentException {
        World w = plugin.getServer().getWorld(TeleportPlugin.EVENT_WORLD_NAME);
        if (w == null) return;
        Player p = e.getPlayer();
        if (p.getWorld() != w) return;
        double x = p.getLocation().getX();
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();
        for (String s : deathRegionMap.keySet()) {
            if (deathRegionMap.get(s).contains(x, y, z)) {
                if (s.equalsIgnoreCase(DEATH_REGION_NAMES[1])){
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[2])) {
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[3])) {
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[4])) {
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                } else if (s.equalsIgnoreCase(DEATH_REGION_NAMES[5])) {
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                }
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        World w = plugin.getServer().getWorld(TeleportPlugin.EVENT_WORLD_NAME);
        if (w == null) return;
        if (p.getWorld() != w) return;
        e.setKeepInventory(true);e.setKeepLevel(true);
    }

    private static Function<String, Boolean> makePatternCheckFunc(String regex) {
        Pattern p = Pattern.compile(regex);
        Function<String, Boolean> fc = (target) -> p.matcher(target).find();
        return fc;
    }

    private static void init() {
        cList.add(makePatternCheckFunc("^/spawn$"));
        cList.add(makePatternCheckFunc("^/home.*"));
        cList.add(makePatternCheckFunc("^/tpa.*"));
        cList.add(makePatternCheckFunc("^/tpyes$"));
        cList.add(makePatternCheckFunc("^/tpaccept$"));
        cList.add(makePatternCheckFunc("^/tpahere.*"));
        if(Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME) != null) {
            // CREATE THE DEATH REGION MAP
            deathRegionMap.put(
                    DEATH_REGION_NAMES[1],
                    new Region(10D, 3D, 164D, -118D, 54D, 54D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            deathRegionMap.put(
                    DEATH_REGION_NAMES[2],
                    new Region(10D, 3D, 164D, -118D, 54D, 54D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            deathRegionMap.put(
                    DEATH_REGION_NAMES[3],
                    new Region(10D, 3D, 164, -118D, 54D, 54D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            deathRegionMap.put(
                    DEATH_REGION_NAMES[4],
                    new Region(10D, 3D, 164, -118D, 54D, 54D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
            );
            deathRegionMap.put(
                    DEATH_REGION_NAMES[5],
                    new Region(10D, 3D, 164, -118D, 54D, 54D, Bukkit.getWorld(TeleportPlugin.EVENT_WORLD_NAME))
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
                    new Region(21D, 28D, 99D, 21D, 32D, 103D, Bukkit.getWorld(TeleportPlugin.SURVIVAL_WORLD_NAME))
            );
        } else {
            throw new NullPointerException("[" + TeleportPlugin.SURVIVAL_WORLD_NAME + "] が見つからなかったため領域を一部作成できませんでした。");
        }
    }

}
