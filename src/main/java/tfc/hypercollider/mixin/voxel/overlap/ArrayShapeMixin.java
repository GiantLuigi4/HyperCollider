package tfc.hypercollider.mixin.voxel.overlap;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfc.hypercollider.util.itf.Overlapable;

@Mixin(ArrayVoxelShape.class)
public class ArrayShapeMixin implements Overlapable {
    @Unique
    private static boolean __intersects(AABB tbox, AABB box, double blockX, double blockY, double blockZ) {
        return
                box.minX + 1.0E-7 < tbox.maxX + blockX &&
                        box.maxX - 1.0E-7 > tbox.minX + blockX &&
                        box.minY + 1.0E-7 < tbox.maxY + blockY &&
                        box.maxY - 1.0E-7 > tbox.minY + blockY &&
                        box.minZ + 1.0E-7 < tbox.maxZ + blockZ &&
                        box.maxZ - 1.0E-7 > tbox.minZ + blockZ;
    }

    @Override
    public boolean overlaps(AABB aabb, double v, double v1, double v2) {
        for (AABB aabb1 : ((VoxelShape) (Object) this).toAabbs())
            if (__intersects(aabb1, aabb, v, v1, v2))
                return true;
        return false;
    }
}
