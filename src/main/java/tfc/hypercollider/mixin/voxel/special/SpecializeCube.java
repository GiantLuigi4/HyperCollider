package tfc.hypercollider.mixin.voxel.special;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.hypercollider.util.voxel.BlockShape;
import tfc.hypercollider.util.voxel.UnitBlockShape;

@Mixin(Shapes.class)
public abstract class SpecializeCube {
    @Shadow
    public static VoxelShape block() {
        return null;
    }

    @Shadow @Final private static VoxelShape EMPTY;

    @Inject(at = @At("RETURN"), method = "create(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
    private static void specializeToBlock(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, CallbackInfoReturnable<VoxelShape> cir) {
        if (cir.getReturnValue() == block()) {
            return;
        }

        //noinspection ConstantValue
        if (cir.getReturnValue().shape == null) {
            return;
        }

        if (cir.getReturnValue().isEmpty()) {
            cir.setReturnValue(EMPTY);
            return;
        }

        cir.setReturnValue(
                new BlockShape(
                        cir.getReturnValue().shape,
                        cir.getReturnValue().bounds(),
                        cir.getReturnValue().getCoords(Direction.Axis.X),
                        cir.getReturnValue().getCoords(Direction.Axis.Y),
                        cir.getReturnValue().getCoords(Direction.Axis.Z)
                )
        );
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static CubeVoxelShape method_1087() { /* compiled code */
        DiscreteVoxelShape discreteVoxelShape = new BitSetDiscreteVoxelShape(1, 1, 1);
        discreteVoxelShape.fill(0, 0, 0);
        return new UnitBlockShape(discreteVoxelShape, new AABB(
                0, 0, 0,
                1, 1, 1
        ));
    }
}
