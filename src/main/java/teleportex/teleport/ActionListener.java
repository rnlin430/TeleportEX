package teleportex.teleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ActionListener implements Listener {
    private TeleportPlugin plugin;

    private static final List<Function<String, Boolean>> cList = new ArrayList<>();
    private static final Map<String, Region> regionMap = new HashMap<>();
    private static final String[] REGION_NAMES = new String[]{"floor", "lv1", "lv2", "lv3", "lv4", "lv5"};

    public ActionListener(TeleportPlugin p ) {
        plugin = p;
        p.getServer().getPluginManager().registerEvents(this, p);
        // init();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        //System.out.println(e.getPlayer().getWorld().getName());
        World w = plugin.getServer().getWorld("abc");
        if(e.getPlayer().isOp()) return;
        if(w == null) return;
        if(e.getPlayer().getWorld() != w) return;
        String msg = e.getMessage();
        for(Function<String, Boolean> c : cList) {
            System.out.println("ActionListener:46");
            if(c.apply(msg)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + "不思議な力で封じられているようだ・・・！");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent e) throws IllegalArgumentException {
        World w = plugin.getServer().getWorld("abc");
        if(w == null) return;
        Player p = e.getPlayer();
        if(p.getWorld() != w) return;
        double x = p.getLocation().getX();
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();
        for(String s : regionMap.keySet()) {
            if(regionMap.get(s).contains(x, y, z)) {
                if(s.equalsIgnoreCase(REGION_NAMES[1])){
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                }else if(s.equalsIgnoreCase(REGION_NAMES[2])){
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                }else if(s.equalsIgnoreCase(REGION_NAMES[3])){
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                }else if(s.equalsIgnoreCase(REGION_NAMES[4])){
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                }else if(s.equalsIgnoreCase(REGION_NAMES[5])){
                    e.setRespawnLocation(new Location(w, 12, 28, 106, 90, 0));
                }
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        Player p = (Player) entity;
        World w = plugin.getServer().getWorld("abc");
        if(w == null) return;
        if(p.getWorld() != w) return;
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

        regionMap.put(REGION_NAMES[1], new Region(10, 3, 164, -118, 54, 54));
        regionMap.put(REGION_NAMES[2], new Region(10, 3, 164, -118, 54, 54));
        regionMap.put(REGION_NAMES[3], new Region(10, 3, 164, -118, 54, 54));
        regionMap.put(REGION_NAMES[4], new Region(10, 3, 164, -118, 54, 54));
        regionMap.put(REGION_NAMES[5], new Region(10, 3, 164, -118, 54, 54));
    }
    static {
        init();
    }
}
