package tfc.hypercollider.mixin.voxel;

import net.minecraft.core.AxisCycle;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import tfc.hypercollider.util.logic.ColliderUtil;

@Mixin(ArrayVoxelShape.class)
public class ArrayMixin {
    public double collideX(AxisCycle axisCycle, AABB aABB, double d) {
        if (((VoxelShape) (Object) this).isEmpty()) {
            return d;
        } else if (Math.abs(d) < 1.0E-7) {
            return 0;
        }

        axisCycle = axisCycle.inverse();
        for (AABB aabb : ((VoxelShape) (Object) this).toAabbs()) {
            d = ColliderUtil.swivelOffset(axisCycle, aABB, aabb, d);
            if (Math.abs(d) < 1.0E-7) {
                return 0;
            }
        }
        return d;
    }
}
