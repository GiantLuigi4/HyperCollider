package tfc.hypercollider.mixin.voxel;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.hypercollider.util.voxel.BlockShape;

import java.util.List;

@Mixin(VoxelShape.class)
public class OptimToBlock {
    @Inject(at = @At("TAIL"), method = "optimize", cancellable = true)
    public void postOptim(CallbackInfoReturnable<VoxelShape> cir) {
        VoxelShape poptim = cir.getReturnValue();
        List<AABB> poptimabs = poptim.toAabbs();
        if (poptimabs.size() == 1) {
            cir.setReturnValue(new BlockShape(
                    poptim.shape,
                    poptim.bounds()
            ));
        } else if (poptimabs.isEmpty()) {
            // singleton empty
            cir.setReturnValue(Shapes.empty());
        }
    }
}
