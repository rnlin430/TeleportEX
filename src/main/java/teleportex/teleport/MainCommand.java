package teleportex.teleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.*;


@SuppressWarnings("ALL")
public class MainCommand implements CommandExecutor {

    // static List<ItemInfo> itemInfoList = new ArrayList<>();
    public static HashMap<String, ItemInfo> itemInfoList = new HashMap<>();
    private TeleportPlugin plugin;

    private static final String[] MESSAGE = new String[] {
            "テレポートしました。",
            "アイテムの持ち込みはできません。",
            "インベントリの中のアイテムを空にしてから使用してください。",
            "ただし、以下のアイテムは持ち込みできます。(交換用アイテム)",
            "強化MOBワールド入りしたプレイヤーがいます。"
    };



    public MainCommand(TeleportPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // extpコマンド
        if(command.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[0])) {
            if (sender instanceof BlockCommandSender) {
                onExtpCommandBlock(sender);
                return true;
            }
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("extp.command.extp")) {
                sender.sendMessage(ChatColor.DARK_RED + command.getPermissionMessage());
                return true;
            }
            if     (args.length == 4) onExtpArg(sender, args);
            else if(args.length == 0) onExtp(sender);
            else return false;
            return true;
        }

        // regitemコマンド
        if(command.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[1])) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("extp.command.regitem")) {
                sender.sendMessage(ChatColor.DARK_RED + command.getPermissionMessage());
                return true;
            }
            if(args.length == 1) {
                onRegitem(sender, args[0]);
            } else {
                return false;
            }
            return true;
        }

        // itemlistコマンド
        if(command.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[2])) {
            if (!(sender instanceof Player)) return true;
            if (!checkPermission(command, sender, "extp.command.itemlist")) return true;
            onItemlist(sender);
            return true;
        }

        //deleteitemコマンド
        if(command.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[3])) {
            if (!(sender instanceof Player)) return true;
            if (!checkPermission(command, sender, "extp.command.deleteitem")) return true;
            if(args.length == 1) {
                onDeleteItem(args[0], sender);
            } else {
                sender.sendMessage(ChatColor.RED + "登録アイテム名を指定してください。/deleteitem <登録名>");
            }
            return true;
        }
        return false;
    }


    //deleteitemコマンド処理
    private void onDeleteItem(String key, CommandSender sender) {
        if (itemInfoList.containsKey(key) == true){
            itemInfoList.remove(key);
            plugin.getItemData().getConfig().set(key, null);
            plugin.getItemData().saveConfig();
            sender.sendMessage(ChatColor.GRAY + "アイテム名 " + ChatColor.AQUA + key+ ChatColor.GRAY + " は削除されました。");
        } else {
            sender.sendMessage(ChatColor.RED + "指定したアイテムが見つかりませんでした。");
        }
    }


    // extp(引数あり)コマンド処理
    private void onExtpArg(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (isEmptyInventory(player)) {
            plugin.teleport(player, args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + MESSAGE[0]);
            // plugin.getServer().broadcastMessage(ChatColor.BOLD + "[春イベント] " + ChatColor.GOLD + "" + ChatColor.BOLD + MESSAGE[2]);
        } else {
            sender.sendMessage(ChatColor.GOLD + MESSAGE[1]);sender.sendMessage(ChatColor.GOLD + MESSAGE[2]);
        }
        return;
    }

    // extp(コマンドブロック)コマンド処理
    private void onExtpCommandBlock(CommandSender sender) {
        BlockCommandSender bcs = (BlockCommandSender) sender;
        CommandBlock cb = (CommandBlock) bcs.getBlock();
        Location location = cb.getLocation();
        Player player = searchNearestPlayer(location);
        if(player == null) {
           System.out.println("onExtpCommandBlock#player is null");
        }
        plugin.dispatchCommandByOperator(player, "/" + TeleportPlugin.COMMANDS[0]);
    }

    @Nullable
    private Player searchNearestPlayer(@NotNull Location location) {
        World w = location.getWorld();
        List<Player> playerList =  w.getPlayers();
        HashMap<Player, Double> d = new HashMap<>();
        for (Player p : playerList) {
            Double distance = p.getLocation().distanceSquared(location);
            d.put(p, distance);
        }
        List<Double> distances = new ArrayList<>(d.values());
        Collections.sort(distances);
        Double min = distances.get(0);
        for (Map.Entry<Player, Double> e : d.entrySet()) {
            if (!e.getValue().equals(min)) continue;
           else {
                return e.getKey();
            }
        }
        return null;
    }

    // extp(引数なし)コマンド処理
    private void onExtp(CommandSender sender) {
        Player player = (Player) sender;
        //plugin.dispatchCommandByConsole("say from Console");
        if (isEmptyInventory(player)) {
            plugin.teleport(player, "abc", 12, 28, 106);
            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + MESSAGE[0]);
           //  plugin.getServer().broadcastMessage(ChatColor.BOLD + "[春イベント] " + ChatColor.GOLD + "" + ChatColor.BOLD + MESSAGE[2]);
        } else {
            sender.sendMessage(ChatColor.GOLD + MESSAGE[1]);sender.sendMessage(ChatColor.GOLD + MESSAGE[2]);
        }

    }

    //Regitem処理
    private void onRegitem(CommandSender sender, @NotNull String key) {
        Player player = (Player) sender;
        ItemStack handis =  player.getInventory().getItemInMainHand();
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            // クライアントに中身を表示
//            player.sendMessage(
//                    "§e[" + entry.getKey() + "]§f"
//                            + entry.getValue().toString());
//        }
        if (player.getInventory().getItemInMainHand() == null) {
            sender.sendMessage(ChatColor.RED + "アイテムを手に持ってください。");
            return;
        }
        if (itemInfoList.containsKey(key)) {
            sender.sendMessage(ChatColor.RED + "既にアイテム名は使用されています。");
            return;
        }

        ItemInfo ii = new ItemInfo();
        if (handis.getItemMeta() != null ) {
            ii.setName(handis.getItemMeta().getDisplayName());
        }
        if (handis.getItemMeta().getLore() != null) {
            ii.setLore(handis.getItemMeta().getLore().toString());
        }
        if (handis.getEnchantments() != null) {
            ii.setEnchant(handis.getEnchantments().toString());
        }
        if (handis.getType() != null) {
            ii.setItemType(handis.getType().toString());
        }
        itemInfoList.put(key, ii);
        sender.sendMessage(ChatColor.GOLD + "アイテム名 " + ChatColor.AQUA + key + ChatColor.GOLD + " で登録されました。");
        itemSave();
//System.out.println("ii.getName=" + ii.getName());System.out.println("ii.getLore=" + ii.getLore());System.out.println("ii.getEnchantment=" + ii.getEnchant());
    }

    // itemlistコマンド
    private void onItemlist(CommandSender sender) {
        sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GRAY + "登録アイテムリスト");
        for (String i : itemInfoList.keySet())
        {
            sender.sendMessage(ChatColor.BOLD + "・" + ChatColor.GOLD + i);
        }
    }

    private void itemSave() {
        for (String key : itemInfoList.keySet()) {
            System.out.println(key + "  " + itemInfoList.get(key).serialize());
            plugin.getItemData().getConfig().set(key, itemInfoList.get(key).serialize());
        }
        plugin.getItemData().saveConfig();
    }

    private boolean isEmptyInventory(@NotNull Player player) {
        ItemStack[] armorlist = player.getInventory().getArmorContents();
        if (isEmptyItemStacks(player, armorlist) == false) return false;

        ItemStack[] inventorylist = player.getInventory().getStorageContents();
        if (isEmptyItemStacks(player, inventorylist) == false) return false;

        return true;
    }

    private boolean checkPermission(Command command, CommandSender sender, String permission) {
        if (!sender.hasPermission("extp.command.regitem")) {
            sender.sendMessage(ChatColor.DARK_RED + command.getPermissionMessage());
            return false;
        }
        return true;
    }

    private boolean isEmptyItemStacks(@NotNull Player player, @NotNull ItemStack[] itemStacks) {
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
                for (String key : itemInfoList.keySet()) {
                    ItemInfo i = itemInfoList.get(key);
                    String name = "";
                    if (is.getItemMeta() != null) {name = is.getItemMeta().getDisplayName();}
                    String lore = "";
                    if (is.getItemMeta().getLore() != null ) {
                        lore = is.getItemMeta().getLore().toString();
                    }
                    String enchant = "";
                    if (is.getEnchantments() != null) {
                        enchant = is.getEnchantments().toString();
                    }
                    String itemType = "";
                    if (is.getType() != null) {
                        itemType = is.getType().toString();
                    }
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



    public static void Broadcast(String message) {
        Bukkit.getServer().broadcastMessage(message);
    }
}
