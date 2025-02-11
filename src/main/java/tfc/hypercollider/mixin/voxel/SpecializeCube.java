package tfc.hypercollider.mixin.voxel;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
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

    @Inject(at = @At("TAIL"), method = "create(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
    private static void specializeToBlock(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, CallbackInfoReturnable<VoxelShape> cir) {
        if (cir.getReturnValue() == block()) {
            return;
        }

        cir.setReturnValue(
                new BlockShape(
                        cir.getReturnValue().shape,
                        cir.getReturnValue().bounds()
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
