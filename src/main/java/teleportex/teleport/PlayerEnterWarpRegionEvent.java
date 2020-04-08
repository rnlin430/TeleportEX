package teleportex.teleport;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

public class PlayerEnterWarpRegionEvent extends PlayerEvent implements Cancellable {
    private String message;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean cancel = false;
    private Location location;
    private BoundingBox boundingBox;
    private String regionName;

    public PlayerEnterWarpRegionEvent(@NotNull Player player, @NotNull Location location, @NotNull String regionName, @NotNull BoundingBox boundingBox) {
        super(player);
        this.location = location;
        this.boundingBox = boundingBox;
        this.regionName = regionName;
    }

    public Location getLocation() {
        return location;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public String getRegionName() {
        return regionName;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
