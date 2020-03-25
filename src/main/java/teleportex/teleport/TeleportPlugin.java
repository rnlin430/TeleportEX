package teleportex.teleport;

import com.github.rnlin.rnlibrary.CustomConfig;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public final class TeleportPlugin extends JavaPlugin {

    static String[] COMMANDS = new String[] {"extp", "regitem", "itemlist", "deleteitem"};
    private CustomConfig itemData = null;

    @Override
    public void onEnable() {
        // Plugin startup logic

        for(String c : COMMANDS)
        getCommand(c).setExecutor(new MainCommand(this));
        itemData = new CustomConfig(this, "item_data.yml");
        itemData.saveDefaultConfig();

        new ActionListener(this);

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
            System.out.println("World " + world.getName() + " is invalid!");
            return;
        }
        Location loc = new Location(world, x, y, z);
        player.teleport(loc);
    }

    // プレイヤーでコマンドを送信するメソッド
    public boolean dispatchCommandByPlayer(Player p, String a) {
        return this.getServer().dispatchCommand(p,a);
    }

    private Map<String, Command> knownCommands;
    // オペレーター権限でコマンドを送信するメソッド
    public boolean dispatchCommandByOperator(Player player, String command){
        String[] args = command.split(" ");

        if(args.length == 0)
            return false;

        String label = args[0].toLowerCase(Locale.ENGLISH);
        org.bukkit.command.Command target = knownCommands.get(label);

        if(target == null)
            return false;

        try {
            target.timings.startTiming();

            target.execute(player, label, (String[]) Arrays.copyOfRange(args, 1, args.length));
            target.timings.stopTiming();
        } catch (CommandException e) {
            target.timings.stopTiming();
            throw e;
        } catch (Throwable t) {
            target.timings.stopTiming();
            throw new CommandException("Unhandled exception executing '" + command + "' in " + target, t);
        }

        return true;
    }

    // コンソールでコマンドを送信
    public boolean dispatchCommandByConsole(String command){
        return getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }
}
