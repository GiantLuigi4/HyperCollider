package tfc.hypercollider.mixin.voxel;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.hypercollider.util.voxel.BlockShape;

@Mixin(VoxelShape.class)
public class OptimToBlock {
    @Inject(at = @At("TAIL"), method = "optimize", cancellable = true)
    public void postOptim(CallbackInfoReturnable<VoxelShape> cir) {
        VoxelShape poptim = cir.getReturnValue();
        if (poptim.toAabbs().size() == 1) {
            cir.setReturnValue(new BlockShape(
                    poptim.shape,
                    poptim.bounds()
            ));
        }
    }
}
