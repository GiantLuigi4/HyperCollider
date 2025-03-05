package tfc.hypercollider.mixin.voxel.overlap;

import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import tfc.hypercollider.util.voxel.cache.CubeShapeQuery;

@Mixin(VoxelShape.class)
public class CubeQuery implements CubeShapeQuery {
    @Override
    public boolean isCube() {
        return false;
    }
}
