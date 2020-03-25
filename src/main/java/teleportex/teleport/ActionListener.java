package teleportex.teleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ActionListener implements Listener {
    private TeleportPlugin plugin;

    public ActionListener(TeleportPlugin p ) {
        plugin = p;
        p.getServer().getPluginManager().registerEvents(this, p);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e){
        //System.out.println(e.getPlayer().getWorld().getName());
        World w = plugin.getServer().getWorld("abc");
        if(w == null) return;
//        if(e.getPlayer().getWorld() != w) return;
//        String msg = e.getMessage();
//        if(msg.contains("/spawn") ||
//                msg.contains("/home") ||
//                msg.contains("/home bed") ||
//                msg.contains("/tpa") ||
//                msg.contains("/tpyes") ||
//                msg.contains("/tpahere") ||
//                msg.contains(("/sethome"))
//        ){
//            e.getPlayer().sendMessage(ChatColor.RED+"ここでこのコマンドは使えません。");
//            e.setCancelled(true);
//        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        World w = plugin.getServer().getWorld("abc");
        if(w == null) return;
        if(e.getPlayer().getWorld() != w) return;
        e.setRespawnLocation(new Location(w, 12, 28, 106));

    }



}
