package teleportex.teleport;

import com.github.rnlin.rnlibrary.CustomConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class TeleportPlugin extends JavaPlugin {

    static String[] COMMANDS = new String[] {"extp", "regitem", "itemlist", "deleteitem", "debug"};
    private CustomConfig itemData = null;

    @Override
    public void onEnable() {
        // Plugin startup logic

        for(String c : COMMANDS)
        getCommand(c).setExecutor(new MainCommand(this));
        itemData = new CustomConfig(this, "item_data.yml");
        itemData.saveDefaultConfig();
        itemData.reloadConfig();
        HashMap<String, Object> hashMap = new HashMap<>();
        for (String regName : itemData.getConfig().getKeys(false)) {
            for (String key : itemData.getConfig().getConfigurationSection(regName).getKeys(false)) {
                String value = itemData.getConfig().getString(regName + "." + key);
                hashMap.put(key, value);
            }
            ItemInfo ii = ItemInfo.deserialize(hashMap);
            MainCommand.itemInfoList.put(regName, ii);
        }

        new ActionListener(this);

//        BoundingBox boundingBox = new BoundingBox(-10, 3, -8, 5, 19, 12);
//        BukkitScheduler scheduler = this.getServer().getScheduler();
//        scheduler.scheduleSyncRepeatingTask(this, () ->
//        {
//            Location lc = this.getServer().getPlayer("rnlin").getLocation();
//
//            if(boundingBox.contains(lc.getX(), lc.getY(), lc.getZ())) {
//                System.out.println("領域に入ってます。");
//            }
//            else {
//                //System.out.println("入ってません。");
//            }
//        }, 0L, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @NotNull
    public CustomConfig getItemData() {
        if(itemData == null) {
            itemData = new CustomConfig(this, "item_data.yml");
            itemData.saveDefaultConfig();
            return  itemData;
        }
        return itemData;
    }

    public void teleport(Player player, String worldName, int x, int y, int z) {
        World world = this.getServer().getWorld(worldName);
        if(world == null) {
            System.out.println("World " + worldName + " is invalid!");
            return;
        }
        Location loc = new Location(world, x, y, z);
        player.teleport(loc);
    }


    public boolean dispatchCommandByPlayer(Player p, String a) {
        return this.getServer().dispatchCommand(p,a);
    }

    public boolean dispatchCommandByOperator(Player p, String a) {
        boolean tmp = p.isOp();
        p.setOp(true);
        boolean result = this.getServer().dispatchCommand(p,a);
        p.setOp(tmp);return result;
    }

    public boolean dispatchCommandByConsole(String command){
        return getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }
}
