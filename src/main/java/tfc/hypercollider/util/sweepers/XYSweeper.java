package tfc.hypercollider.util.sweepers;

import net.minecraft.core.BlockPos;

public class XYSweeper {
    int minX, minY, minZ;
    int sizeX, sizeY, sizeZ;

    int cx, cy, cz;
    int bx, by, bz;

    public XYSweeper(
            int minX, int minY, int minZ,
            int sizeX, int sizeY, int sizeZ
    ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public XYSweeper move(int minX, int minY, int minZ) {
//        this.minX = minX;
//        this.minY = minY;
//        this.minZ = minZ;
        bx = cx = minX + this.minX - 1;
        by = cy = minY + this.minY - 1;
        bz = cz = minZ + this.minZ - 1;
        return this;
    }

    public boolean hasNext() {
        return cx != (bx + sizeX) + 1;
    }

    public BlockPos.MutableBlockPos next(
            BlockPos.MutableBlockPos pos
    ) {
        pos.set(cx, cy, cz);
        cy += 1;
        if (cy > (by + sizeY)) {
            cx += 1;
            cy = by;
            if (cx > (bx + sizeX)) {
                cz += 1;
                cx = bx;
                if (cz > (bz + sizeZ)) {
                    cx = (bx + sizeX) + 1;
                }
            }
        }
        return pos;
    }
}
