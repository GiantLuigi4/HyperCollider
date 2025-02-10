package tfc.hypercollider.mixin.voxel;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(VoxelShape.class)
public abstract class FastVoxel {
    @Shadow public abstract AABB bounds();

    @Shadow public abstract List<AABB> toAabbs();

    private static double swivelOffset(AxisCycle axiscycle, AABB pCollisionBox, AABB box, double offsetX) {
        Direction.Axis xSwivel = axiscycle.cycle(Direction.Axis.X);
        Direction.Axis ySwivel = axiscycle.cycle(Direction.Axis.Y);
        Direction.Axis zSwivel = axiscycle.cycle(Direction.Axis.Z);

        double tMinX = box.min(xSwivel);
        double tMaxX = box.max(xSwivel);
        double oMaxX = pCollisionBox.max(xSwivel);
        double oMinX = pCollisionBox.min(xSwivel);

        double tMinZ = box.min(ySwivel);
        double tMaxZ = box.max(ySwivel);
        double oMaxZ = pCollisionBox.max(ySwivel);
        double oMinZ = pCollisionBox.min(ySwivel);

        double tMaxY = box.max(zSwivel);
        double tMinY = box.min(zSwivel);
        double oMaxY = pCollisionBox.max(zSwivel);
        double oMinY = pCollisionBox.min(zSwivel);

        if (oMaxY > tMinY && oMinY < tMaxY && oMaxZ > tMinZ && oMinZ < tMaxZ) {
            if (offsetX > 0.0D && oMaxX <= tMinX) {
                double deltaX = tMinX - oMaxX;

                if (deltaX < offsetX) return deltaX;
            } else if (offsetX < 0.0D && oMinX >= tMaxX) {
                double deltaX = tMaxX - oMinX;

                if (deltaX > offsetX) return deltaX;
            }
        }

        return offsetX;
    }

    // doing the collision logic on the AABBs instead of the bitmasks is a good amount faster
    @SuppressWarnings({"ConstantValue", "EqualsBetweenInconvertibleTypes"})
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/core/AxisCycle;inverse()Lnet/minecraft/core/AxisCycle;"), method = "collideX", cancellable = true)
    public void preCollide(AxisCycle axisCycle, AABB aABB, double d, CallbackInfoReturnable<Double> cir) {
        if (getClass().equals(ArrayVoxelShape.class)) {
            axisCycle = axisCycle.inverse();
            for (AABB aabb : toAabbs()) {
                d = swivelOffset(axisCycle, aABB, aabb, d);
                if (Math.abs(d) < 1.0E-7) {
                    cir.setReturnValue(0.0);
                    return;
                }
            }
            cir.setReturnValue(d);
        } else if (getClass().equals(CubeVoxelShape.class)) {
            axisCycle = axisCycle.inverse();
            AABB other = bounds();
            cir.setReturnValue(swivelOffset(axisCycle, aABB, other, d));
        }
    }

    // TODO: dedicated offset cube voxel shape class thingy?
}
