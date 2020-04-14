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


//@SuppressWarnings("ALL")
public class MainCommand implements CommandExecutor {

    // static List<ItemInfo> itemInfoList = new ArrayList<>();
    public static HashMap<String, ItemInfo> itemInfoList = new HashMap<>();
    private TeleportPlugin plugin;

    public MainCommand(TeleportPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        // extpコマンド
        if (cmd.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[0])) {
            if (sender instanceof BlockCommandSender) {
                onExtpCommandBlock(sender);
                return true;
            }
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("extp.command.extp")) {
                sender.sendMessage(ChatColor.DARK_RED + cmd.getPermissionMessage());
                return true;
            }
            if (args.length == 4) onExtpArg(sender, args);
            else if (args.length == 0) onExtp(sender);
            else return false;
            return true;
        }

        // regitemコマンド
        if (cmd.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[1])) {
            if (!(sender instanceof Player)) return true;
            if (!sender.hasPermission("extp.command.regitem")) {
                sender.sendMessage(ChatColor.DARK_RED + cmd.getPermissionMessage());
                return true;
            }
            if (args.length == 1) {
                onRegitem(sender, args[0]);
            } else {
                return false;
            }
            return true;
        }

        // itemlistコマンド
        if (cmd.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[2])) {
            if (!(sender instanceof Player)) return true;
            if (!checkPermission(cmd, sender, "extp.command.itemlist")) return true;
            onItemlist(sender);
            return true;
        }

        // deleteitemコマンド
        if (cmd.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[3])) {
            if (!(sender instanceof Player)) return true;
            if (!checkPermission(cmd, sender, "extp.command.deleteitem")) return true;
            if (args.length == 1) {
                onDeleteItem(args[0], sender);
            } else {
                sender.sendMessage(ChatColor.RED + "登録アイテム名を指定してください。/deleteitem <登録名>");
            }
            return true;
        }

        // debugコマンド
        if (cmd.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[4])) {
            Player p = (Player) sender;
            TeleportPlugin.savePlayerInventory(p);
        }

        // debug2コマンド
        if (cmd.getName().equalsIgnoreCase(TeleportPlugin.COMMANDS[5])) {
            Player p = (Player) sender;
            TeleportPlugin.restorePlayerInventory(p);
        }
       return false;
    }


    // deleteitemコマンド処理
    private void onDeleteItem(String key, CommandSender sender) {
        if (itemInfoList.containsKey(key) == true) {
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
        if (TeleportPlugin.isEmptyInventory(player)) {
            plugin.teleport(player, args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + TeleportPlugin.MESSAGE[0]);
            // plugin.getServer().broadcastMessage(ChatColor.BOLD + "[春イベント] " + ChatColor.GOLD + "" + ChatColor.BOLD + MESSAGE[2]);
        } else {
            sender.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[1]);sender.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[2]);
        }
    }

    // extp(コマンドブロック)コマンド処理
    private void onExtpCommandBlock(CommandSender sender) {
        BlockCommandSender bcs = (BlockCommandSender) sender;
        CommandBlock cb = (CommandBlock) bcs.getBlock();
        Location location = cb.getLocation();
        Player player = searchNearestPlayer(location);
        if (player == null) {
           System.out.println("onExtpCommandBlock#player is null");
        }
        plugin.dispatchCommandByOperator(player, "/" + TeleportPlugin.COMMANDS[0]);
    }

    @Nullable
    private Player searchNearestPlayer(@NotNull Location location) {
        World w = location.getWorld();
        List<Player> playerList =  Objects.requireNonNull(w, "MainCommand#searchNearestPlayer(Location...)#w = null:167").getPlayers();
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
        if (TeleportPlugin.isEmptyInventory(player)) {
            plugin.teleport(player, "abc", 12, 28, 106);
            sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GOLD + TeleportPlugin.MESSAGE[0]);
           //  plugin.getServer().broadcastMessage(ChatColor.BOLD + "[春イベント] " + ChatColor.GOLD + "" + ChatColor.BOLD + MESSAGE[2]);
        } else {
            sender.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[1]);sender.sendMessage(ChatColor.GOLD + TeleportPlugin.MESSAGE[2]);
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
        for (String i : itemInfoList.keySet()) {
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

    private boolean checkPermission(Command command, CommandSender sender, String permission) {
        if (!sender.hasPermission("extp.command.regitem")) {
            sender.sendMessage(ChatColor.DARK_RED + command.getPermissionMessage());
            return false;
        }
        return true;
    }


    public static void Broadcast(String message) {
        Bukkit.getServer().broadcastMessage(message);
    }
}
