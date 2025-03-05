package tfc.hypercollider.mixin.voxel.fast;

import net.minecraft.core.AxisCycle;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import tfc.hypercollider.util.logic.ColliderUtil;

@Mixin(CubeVoxelShape.class)
public class CubeMixin {
    public double collideX(AxisCycle axisCycle, AABB aABB, double d) {
        if (((VoxelShape) (Object) this).isEmpty()) {
            return d;
        } else if (Math.abs(d) < 1.0E-7) {
            return 0;
        }

        axisCycle = axisCycle.inverse();
        AABB other = ((VoxelShape) (Object) this).bounds();
        return ColliderUtil.swivelOffset(axisCycle, aABB, other, d);
    }

//    public VoxelShape optimize() {
//        return new BlockShape(
//                ((VoxelShape) (Object) this).shape,
//                ((VoxelShape) (Object) this).bounds()
//        );
//    }

//    public VoxelShape move(double xOffset, double yOffset, double zOffset) {
//        return new BlockShape(
//                ((VoxelShape) (Object) this).shape,
//                ((VoxelShape) (Object) this).bounds().move(xOffset, yOffset, zOffset)
//        );
//    }
}
