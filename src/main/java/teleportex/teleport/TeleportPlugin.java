package teleportex.teleport;

import com.github.rnlin.rnlibrary.CustomConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class TeleportPlugin extends JavaPlugin {

    public static final String SURVIVAL_WORLD_NAME = "world";
    public static final String[] MESSAGE = new String[]
    {
            "イベントの地にテレポートしました。",
            "アイテムの持ち込みは§4§l装備含めて§6できません＞＜",
            "インベントリの中のアイテムをすべて空にしてからポータルに入ってね！",
            "ただし、以下のアイテムは持ち込みできます。(交換用アイテム)",
            "強化MOBワールド入りしたプレイヤーがいます。",
            "ごめんなさい！＞＜ 交換アイテム以外のアイテムの持ち帰りできないよ！",
            "§l§n交換アイテム以外§r§6を前のチェストに預けてくださいね！\n§c（チェストを看板保護してねっ！）。"
    };
    static String[] COMMANDS = new String[] {"extp", "regitem", "itemlist", "deleteitem", "debug"};
    private CustomConfig itemData = null;
    public static final String EVENT_WORLD_NAME = "abc";

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

    public static boolean isEmptyInventory(@NotNull Player player) {
        ItemStack[] armorers = player.getInventory().getArmorContents();
        if (!isEmptyItemStacks(player, armorers)) return false;

        ItemStack[] inventories = player.getInventory().getStorageContents();
        if (!isEmptyItemStacks(player, inventories)) return false;

        return true;
    }

    private static boolean isEmptyItemStacks(@NotNull Player player, @NotNull ItemStack[] itemStacks) {
        int count = 0;
        // ItemStack[] inventorylist = player.getInventory().getStorageContents();
        int a = itemStacks.length;
        for (ItemStack is : itemStacks) {
            if (count == a - 1) break;
            if (is == null) {
                continue;
            }
            else { // アイテムが見つかった場合
                boolean flag = false;
                for (String key : MainCommand.itemInfoList.keySet()) {
                    ItemInfo i = MainCommand.itemInfoList.get(key);
                    String name = "";
                    if (is.getItemMeta() != null) {name = is.getItemMeta().getDisplayName();}
                    String lore = "";
                    if (is.getItemMeta().getLore() != null ) {
                        lore = is.getItemMeta().getLore().toString();
                    }
                    String enchant = "";
                    is.getEnchantments();
                    enchant = is.getEnchantments().toString();
                    String itemType = "";
                    is.getType();
                    itemType = is.getType().toString();
//System.out.println("\ni.getName()=\n" + i.getName() + "\nname=\n"  + name + "\ni.getLore()=\n" + i.getLore() + "\nlore=\n" + lore + "\ni.getEnchant()=\n" + i.getEnchant() + "\nenchant=\n" + enchant);
                    if (((i.getName().equalsIgnoreCase(name) && i.getLore().equalsIgnoreCase(lore)) &&
                            i.getEnchant().equalsIgnoreCase(enchant)) && i.getItemType().equalsIgnoreCase(itemType) ) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }
}

