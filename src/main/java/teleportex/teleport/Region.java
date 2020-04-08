package teleportex.teleport;

import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Region extends BoundingBox {

    private World world = null;

    public Region(double startX, double startY, double startZ, double endX, double endY, double endZ, @NotNull World world) {
        if ((startX <= endX)) {
            endX++;
        } else {
            startX++;
        }
        if ((startY <= endY)) {
            endY++;
        } else {
            startX++;
        }
        if ((startZ <= endZ)) {
            endZ++;
        } else {
            startZ++;
        }
        this.resize(startX,  startY,  startZ,  endX,  endY,  endZ);
        this.world = Objects.requireNonNull(world);
    }

    @NotNull
    public World getWorld() {
        return world;
    }

    @NotNull
    public String getWorldName() {
        return this.world.getName();
    }
}
