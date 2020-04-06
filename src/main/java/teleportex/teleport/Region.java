package teleportex.teleport;

import org.bukkit.util.BoundingBox;

public class Region extends BoundingBox {

    public Region(double startX, double startY, double startZ, double endX, double endY, double endZ) {
        if ((startX <= endX)) {
            startX++;
        } else {
            endX++;
        }
        if ((startY <= endY)) {
            startY++;
        } else {
            endY++;
        }
        if ((startZ <= endZ)) {
            startZ++;
        } else {
            endZ++;
        }
        this.resize(startX,  startY,  startZ,  endX,  endY,  endZ);
    }
}
