package tfc.hypercollider.mixin.voxel.overlap;

import me.jellysquid.mods.lithium.common.shapes.VoxelShapeCaster;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import tfc.hypercollider.util.itf.Overlapable;
import tfc.hypercollider.util.voxel.EmptyShape;

@Mixin(EmptyShape.class)
public class EmptyShapeMixin implements Overlapable {
    @Override
    public boolean overlaps(AABB aabb, double v, double v1, double v2) {
        return false;
    }
}
