package teleportex.teleport;

import com.github.rnlin.rnlibrary.ConsoleLog;
import com.github.rnlin.rnlibrary.CustomConfig;
import com.github.rnlin.rnlibrary.PlayersData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class TeleportPlugin extends JavaPlugin {

    public static final String SURVIVAL_WORLD_NAME = "world";
    public static final String[] MESSAGE = {
        "イベントの地にテレポートしました。",
        "アイテムの持ち込みは§4§l装備含めて§6できません＞＜",
        "インベントリの中のアイテムをすべて空にしてからポータルに入ってね！",
        "ただし、以下のアイテムは持ち込みできます。(交換用アイテム)",
        "強化MOBワールド入りしたプレイヤーがいます。",
        "ごめんなさい！! 景品交換アイテム以外のアイテムの持ち帰りできないよ！",
        "§l§n交換アイテム以外§r§6を前のチェストに預けてくださいね！\n§c（チェストを看板保護してねっ！）。"
    };
    static String[] COMMANDS = new String[] {"extp", "regitem", "itemlist", "deleteitem", "debug", "debug2"};
    private static final String[] YML_FILE_NAMES = {"items.yml", "player.inv.yml", "player.armor.yml", "player.yml"};
    private CustomConfig itemData = null;
    public static final String EVENT_WORLD_NAME = "abc";
    public static final String BOSS_WORLD_NAME = "boss";
    private CustomConfig inventoryData = null;
    private CustomConfig armorData = null;
    private static TeleportPlugin instance;
    private static CustomConfig playerData = null;
    public static TeleportPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        ConsoleLog.setPluginName(this.getDescription().getName());
        instance = this;
        for (String c : COMMANDS) getCommand(c).setExecutor(new MainCommand(this));
        initConfig();
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
    }

    private void initConfig() {
        itemData = new CustomConfig(this, YML_FILE_NAMES[0]);
        itemData.saveDefaultConfig();
        itemData.reloadConfig();
        inventoryData = new CustomConfig(this, YML_FILE_NAMES[1]);
        inventoryData.saveDefaultConfig();
        inventoryData.reloadConfig();
        armorData = new CustomConfig(this, YML_FILE_NAMES[2]);
        armorData.saveDefaultConfig();
        armorData.reloadConfig();
        playerData = new CustomConfig(this, YML_FILE_NAMES[3]);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @NotNull
    public CustomConfig getItemData() {
        if(itemData == null) {
            itemData = new CustomConfig(this, YML_FILE_NAMES[0]);
            itemData.saveDefaultConfig();
            return  itemData;
        }
        return itemData;
    }

    @NotNull
    public CustomConfig getInventoryData() {
        if(inventoryData == null) {
            inventoryData = new CustomConfig(this, YML_FILE_NAMES[1]);
            inventoryData.saveDefaultConfig();
            return  inventoryData;
        }
        return inventoryData;
    }

    @NotNull
    public CustomConfig getArmorData() {
        if(armorData == null) {
            armorData = new CustomConfig(this, YML_FILE_NAMES[2]);
            armorData.saveDefaultConfig();
            return  armorData;
        }
        return armorData;
    }

    @NotNull
    public CustomConfig getPlayerData() {
        if(playerData == null) {
            playerData = new PlayersData(this, YML_FILE_NAMES[3]);
            return  playerData;
        }
        return playerData;
    }

    public void teleport(Player player, String worldName, final double x, final double y, final double z) {
        World world = this.getServer().getWorld(worldName);
        if(world == null) {
            System.out.println("World " + worldName + " is invalid!");
            return;
        }
        Location loc = new Location(world, x, y, z);
        player.teleport(loc);
    }

    public void teleport(Player player, String worldName, final double x, final double y, final double z, final float yaw, final float pitch) {
        World world = this.getServer().getWorld(worldName);
        if(world == null) {
            System.out.println("World " + worldName + " is invalid!");
            return;
        }
        Location loc = new Location(world, x, y, z, yaw, pitch);
        player.teleport(loc);
    }


    public boolean dispatchCommandByPlayer(@NotNull Player p, @NotNull String a) {
        return this.getServer().dispatchCommand(p,a);
    }

    public boolean dispatchCommandByOperator(@NotNull Player p, @NotNull String a) {
        boolean tmp = p.isOp();
        p.setOp(true);
        boolean result = this.getServer().dispatchCommand(p,a);
        p.setOp(tmp);return result;
    }

    public boolean dispatchCommandByConsole(@NotNull String command){
        return getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }

    public static boolean isEmptyInventory(@NotNull Player player) {
        ItemStack[] armorers = player.getInventory().getArmorContents();
        if (!isEmptyItemStacks(armorers)) return false;

        ItemStack[] inventories = player.getInventory().getStorageContents();
        if (!isEmptyItemStacks(inventories)) return false;
        return true;
    }

    private static boolean isEmptyItemStacks(@NotNull ItemStack[] itemStacks) {
        int count = 0;
        // ItemStack[] inventorylist = player.getInventory().getStorageContents();
        int a = itemStacks.length;
        for (ItemStack is : itemStacks) {
            if (count == a - 1) break;
            if (is == null) {
                continue;
            } else { // アイテムが見つかった場合
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
// ConsoleLog.sendDebugMessage("\ni.getName()=\n" + i.getName() + "\nname=\n"  + name + "\ni.getLore()=\n" + i.getLore() + "\nlore=\n" + lore + "\ni.getEnchant()=\n" + i.getEnchant() + "\nenchant=\n" + enchant);
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

    public static void savePlayerInventory(@NotNull Player p) {
        ItemStack[] storage = p.getInventory().getStorageContents();
        Map<Integer, ItemStack> im = new HashMap<>();
        TeleportPlugin inst = TeleportPlugin.getInstance();
        for (int i = 0; i < storage.length; i++) {
//            System.out.println(i);
            ItemStack is = storage[i];
            if (is != null) {
                im.put(i, is);
            } else {
                im.put(i, null);
            }
        }
        for (int i : im.keySet()) {
            inst.getInventoryData().getConfig().set(String.valueOf(p.getUniqueId()) + "." + String.valueOf(i), im.get(i));
        }
        inst.getInventoryData().saveConfig();

        ItemStack[] armor = p.getInventory().getArmorContents();
        Map<Integer, ItemStack> jm = new HashMap<>();
        for (int i = 0; i < armor.length; i++) {
//            System.out.println(i);
            ItemStack is = armor[i];
            if (is != null) {
                jm.put(i, is);
            } else {
                jm.put(i, null);
            }
        }
        for (int i : jm.keySet()) {
            inst.getArmorData().getConfig().set(String.valueOf(p.getUniqueId()) + "." + String.valueOf(i), jm.get(i));
        }
        inst.getArmorData().getConfig().set(String.valueOf(p.getUniqueId()) + "." + "offhand", p.getInventory().getExtraContents()[0]);
        inst.getArmorData().saveConfig();
        inst.getArmorData().reloadConfig();
    }

    public static void restorePlayerInventory(@NotNull Player p) {
        ItemStack[] items = new ItemStack[36];
        TeleportPlugin inst = TeleportPlugin.getInstance();
        inst.getInventoryData().reloadConfig();
        String uuid = p.getUniqueId().toString();
        String errormsg = p.getPlayerListName() + "の装備アイテムデータが見つかりませんでした。";
        for (String slotnum : Objects.requireNonNull(
                inst.getInventoryData().getConfig().getConfigurationSection(uuid), YML_FILE_NAMES[1] + ":" +  errormsg).getKeys(false)) {
            ItemStack is = inst.getInventoryData().getConfig().getItemStack(uuid + "." + slotnum);
            items[Integer.valueOf(slotnum)] = is;
        }

        p.getInventory().setStorageContents(items);

        items = new ItemStack[4];
        ItemStack[] offhand = new ItemStack[1];
        inst.getInventoryData().reloadConfig();
        for (String slotnum : Objects.requireNonNull(
                inst.getArmorData().getConfig().getConfigurationSection(uuid), YML_FILE_NAMES[2] + ":" + errormsg).getKeys(false)) {
            if (slotnum.equalsIgnoreCase("offhand")) {
                offhand[0] = inst.getArmorData().getConfig().getItemStack(uuid + "." + slotnum);
                continue;
            }
            ItemStack is = inst.getArmorData().getConfig().getItemStack(uuid + "." + slotnum);
            items[Integer.valueOf(slotnum)] = is;
        }
        p.getInventory().setArmorContents(items);
        p.getInventory().setExtraContents(offhand);
    }
}

