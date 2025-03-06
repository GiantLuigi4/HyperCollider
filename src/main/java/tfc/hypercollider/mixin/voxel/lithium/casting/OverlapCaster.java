package tfc.hypercollider.mixin.voxel.lithium.casting;

import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import tfc.hypercollider.util.itf.Overlapable;
import me.jellysquid.mods.lithium.common.shapes.VoxelShapeCaster;

@Mixin(Overlapable.class)
public interface OverlapCaster
 extends VoxelShapeCaster
{
    @Override
    default boolean intersects(AABB aabb, double v, double v1, double v2) {
        return ((Overlapable) this).overlaps(aabb, v, v1, v2);
    }
}
