package tfc.hypercollider.mixin.voxel.special;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.hypercollider.util.voxel.BoundingShape;
import tfc.hypercollider.util.voxel.discrete.SingleBitDiscreteShape;

@Mixin(value = Shapes.class, priority = 999)
public abstract class SpecializeCube {
    @Shadow
    public static VoxelShape block() {
        return null;
    }

    @Shadow
    @Final
    private static VoxelShape EMPTY;

    @Mutable
    @Shadow
    @Final
    private static VoxelShape BLOCK;

    @Inject(at = @At("RETURN"), method = "create(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
    private static void specializeToBlock(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, CallbackInfoReturnable<VoxelShape> cir) {
//        System.out.println(block());
//        if (!(BLOCK instanceof BoundingShape)) {
//            if (minX > maxX)
//                throw new RuntimeException("Lithium...");
//        }
        if (cir.getReturnValue() == BLOCK) {
            return;
        }

        //noinspection ConstantValue
//        if (cir.getReturnValue().shape == null) {
//            return;
//        }

        if (cir.getReturnValue().isEmpty()) {
            cir.setReturnValue(EMPTY);
            return;
        }

        cir.setReturnValue(
                new BoundingShape(
                        BLOCK.shape,
                        cir.getReturnValue().bounds()
                )
        );
    }

    static {
        DiscreteVoxelShape discreteVoxelShape = new SingleBitDiscreteShape();
        discreteVoxelShape.fill(0, 0, 0);
        BLOCK = new BoundingShape(discreteVoxelShape, new AABB(
                0, 0, 0,
                1, 1, 1
        ));
    }
}
