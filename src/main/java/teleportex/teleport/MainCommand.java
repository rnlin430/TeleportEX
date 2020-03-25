package teleportex.teleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


@SuppressWarnings("ALL")
public class MainCommand implements CommandExecutor {

    // static List<ItemInfo> itemInfoList = new ArrayList<>();
    static HashMap<String, ItemInfo> itemInfoList = new HashMap<>();
    private TeleportPlugin plugin;

    private static final String[] MESSAGE = new String[] {
            "テレポートしました。",
            "アイテムの持ち込みはできません。インベントリの中身を空にしてからアイテムを使用してください。",
            "強化MOBワールド入りしたプレイヤーがいます。"
    };



    public MainCommand(TeleportPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // extpコマンド
        if(command.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[0])) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("extp.command.extp")) {
                sender.sendMessage(ChatColor.DARK_RED + command.getPermissionMessage());
                return true;
            }
            if     (args.length == 4) doExtpArg (sender, args);
            else if(args.length == 0) doExtp    (sender);
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
                doRegitem(sender, args[0]);
            } else {
                return false;
            }
            return true;
        }

        // itemlistコマンド
        if(command.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[2])) {
            if (!(sender instanceof Player)) return true;
            if(!checkPermission(command, sender, "extp.command.itemlist")) return true;
        }

        //deleteitemコマンド
        if(command.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[3])) {
            if (!(sender instanceof Player)) return true;
            if(!checkPermission(command, sender, "extp.command.deleteitem")) return true;
        }
        return false;
    }

    // extp(引数あり)コマンド処理
    private void doExtpArg(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(isEmptyInventory(player)){
            plugin.teleport(player, args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + MESSAGE[0]);
            // plugin.getServer().broadcastMessage(ChatColor.BOLD + "[春イベント] " + ChatColor.GOLD + "" + ChatColor.BOLD + MESSAGE[2]);
        } else {
            sender.sendMessage(ChatColor.GOLD + MESSAGE[1]);
        }
        return;
    }

    // extp(引数なし)コマンド処理
    private void doExtp(CommandSender sender) {
        Player player = (Player) sender;
        //plugin.dispatchCommandByConsole("say from Console");
        if(isEmptyInventory(player)){
            plugin.teleport(player, "abc", 0, 4, 0);
            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + MESSAGE[0]);
           //  plugin.getServer().broadcastMessage(ChatColor.BOLD + "[春イベント] " + ChatColor.GOLD + "" + ChatColor.BOLD + MESSAGE[2]);
        } else {
            sender.sendMessage(ChatColor.GOLD + MESSAGE[1]);
        }

    }

    //Regitem処理
    private void doRegitem(CommandSender sender, @NotNull String key) {
        Player player = (Player) sender;
        ItemStack handis =  player.getInventory().getItemInMainHand();
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            // クライアントに中身を表示
//            player.sendMessage(
//                    "§e[" + entry.getKey() + "]§f"
//                            + entry.getValue().toString());
//        }

        ItemInfo ii = new ItemInfo();
        if(handis.getItemMeta() != null ) {
            ii.setName(handis.getItemMeta().getDisplayName());
        }
        if(handis.getItemMeta().getLore() != null) {
            ii.setLore(handis.getItemMeta().getLore().toString());
        }
        if(handis.getEnchantments() != null) {
            ii.setEnchant(handis.getEnchantments().toString());
        }
        if(handis.getType() != null) {
            ii.setItemType(handis.getType().toString());
        }
        itemInfoList.put(key, ii);
//System.out.println("ii.getName=" + ii.getName());System.out.println("ii.getLore=" + ii.getLore());System.out.println("ii.getEnchantment=" + ii.getEnchant());
    }


    private boolean isEmptyInventory(@NotNull Player player) {
        ItemStack[] armorlist = player.getInventory().getArmorContents();
        if(isEmptyItemStacks(player, armorlist) == false) return false;

        ItemStack[] inventorylist = player.getInventory().getStorageContents();
        if(isEmptyItemStacks(player, inventorylist) == false) return false;

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
                for(String key : itemInfoList.keySet()) {
                    ItemInfo i = itemInfoList.get(key);
                    String name = "";
                    if (is.getItemMeta() != null) {name = is.getItemMeta().getDisplayName();}
                    String lore = "";
                    if (is.getItemMeta().getLore() != null ){
                        lore = is.getItemMeta().getLore().toString();
                    }
                    String enchant = "";
                    if(is.getEnchantments() != null) {
                        enchant = is.getEnchantments().toString();
                    }
                    String itemType = "";
                    if(is.getType() != null) {
                        itemType = is.getType().toString();
                    }
//System.out.println("\ni.getName()=\n" + i.getName() + "\nname=\n"  + name + "\ni.getLore()=\n" + i.getLore() + "\nlore=\n" + lore + "\ni.getEnchant()=\n" + i.getEnchant() + "\nenchant=\n" + enchant);
                    if(((i.getName().equalsIgnoreCase(name) && i.getLore().equalsIgnoreCase(lore)) &&
                            i.getEnchant().equalsIgnoreCase(enchant)) && i.getItemType().equalsIgnoreCase(itemType) ) {
                        flag = true;
                        break;
                    }
                }
                if(flag){
//System.out.println("flag == true");
                    continue;
                }
//System.out.println("flag == false");
                return false;
            }

        }
        return true;
    }



    public static void Broadcast(String message) {
        Bukkit.getServer().broadcastMessage(message);
    }
}
