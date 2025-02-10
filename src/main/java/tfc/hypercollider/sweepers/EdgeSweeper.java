package tfc.hypercollider.sweepers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class EdgeSweeper {
    XYSweeper edgeX;
    XYSweeper edgeZ;
    AABB box;

    double ox, oy, oz;

    public EdgeSweeper(AABB box, double ox, double oy, double oz) {
        this.box = box;
//        this.ox = ox;
//        this.oy = oy;
//        this.oz = oz;
        this.ox = box.minX;
        this.oy = box.minY;
        this.oz = box.minZ;
    }

    public void set(
            int x,
            int y,
            int z,
            int edgeX,
            int edgeZ
    ) {
        if (edgeX == 1) {
            double bndX = box.getXsize() - 1;
            this.edgeX = new XYSweeper(
                    (int) Math.floor(bndX),
                    (int) Math.floor((box.minY - oy)),
                    (int) Math.floor((box.minZ - oz)),
                    (int) Math.ceil(bndX + 1),
                    (int) Math.ceil((box.maxY - oy)),
                    (int) Math.ceil((box.maxZ - oz))
            ).move(x, y, z - 1);
        } else if (edgeX == -1) {
            double bndX = box.getXsize() - 1;
            this.edgeX = new XYSweeper(
                    (int) Math.floor(0),
                    (int) Math.floor((box.minY - oy)),
                    (int) Math.floor((box.minZ - oz)),
                    (int) Math.ceil(1),
                    (int) Math.ceil((box.maxY - oy)),
                    (int) Math.ceil((box.maxZ - oz))
            ).move(x - 1, y, z - 1);
        }
        if (edgeZ == 1) {
            double bndX = box.getZsize() - 1;
            this.edgeZ = new XYSweeper(
                    (int) Math.floor((box.minX - ox)),
                    (int) Math.floor((box.minY - oy)),
                    (int) Math.floor(bndX),
                    (int) Math.ceil((box.maxX - ox)),
                    (int) Math.ceil((box.maxY - oy)),
                    (int) Math.ceil(bndX + 1)
            ).move(x, y, z);
        } else if (edgeZ == -1) {
            double bndX = box.getZsize() - 1;
            this.edgeZ = new XYSweeper(
                    (int) Math.floor((box.minX - ox)),
                    (int) Math.floor((box.minY - oy)),
                    (int) Math.floor(0),
                    (int) Math.ceil((box.maxX - ox)),
                    (int) Math.ceil((box.maxY - oy)),
                    (int) Math.ceil(1)
            ).move(x, y, z);
        }
    }

    public boolean hasNext() {
        return edgeX != null || edgeZ != null;
    }

    public BlockPos.MutableBlockPos next(
            BlockPos.MutableBlockPos pos
    ) {
        if (edgeX != null) {
            edgeX.next(pos);
            if (!edgeX.hasNext())
                edgeX = null;
            return pos;
        }
        if (edgeZ != null) {
            edgeZ.next(pos);
            if (!edgeZ.hasNext())
                edgeZ = null;
            return pos;
        }
        throw new RuntimeException("Next called on finished edge sweeper");
    }
}
