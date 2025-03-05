package tfc.hypercollider.mixin.voxel.join;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.hypercollider.util.itf.Overlapable;
import tfc.hypercollider.util.voxel.BoundingShape;
import tfc.hypercollider.util.voxel.OffsetBoundingShape;
import tfc.hypercollider.util.voxel.cache.CubeShapeQuery;

@Mixin(Shapes.class)
public class FastJoinNotEmpty {
    @Inject(at =@At("HEAD"), method = "joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z", cancellable = true)
    private static void preJoin(VoxelShape shape1, VoxelShape shape2, BooleanOp resultOperator, CallbackInfoReturnable<Boolean> cir) {
        if (resultOperator == BooleanOp.AND) {
            if (shape1.isEmpty() || shape2.isEmpty()) {
                // AND would be false for either of these
                cir.setReturnValue(false);
                return;
            }

            boolean leftIsBounding = false;
            if (((CubeShapeQuery) shape2).isCube()) {
                VoxelShape tmp = shape1;
                shape1 = shape2;
                shape2 = tmp;
                leftIsBounding = true;
            } else if (((CubeShapeQuery) shape2).isCube()) {
                leftIsBounding = true;
            }

            if (!leftIsBounding) {
                // vanilla must handle
                return;
            }

            if (shape2 instanceof Overlapable) {
                AABB bound = shape1.bounds();
                boolean overlap = ((Overlapable) shape2).overlaps(bound);
                cir.setReturnValue(overlap);
            }
        }
    }
}
