package tfc.hypercollider.mixin.voxel.overlap;

import me.jellysquid.mods.lithium.common.shapes.VoxelShapeCaster;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfc.hypercollider.util.itf.Overlapable;
import tfc.hypercollider.util.voxel.BoundingShape;

@Mixin(BoundingShape.class)
public class BlockShapeMixin implements Overlapable {
    @Shadow private AABB box;

    @Override
    public boolean overlaps(AABB box, double blockX, double blockY, double blockZ) {
        return
                box.minX + 1.0E-7 < this.box.maxX + blockX &&
                        box.maxX - 1.0E-7 > this.box.minX + blockX &&
                        box.minY + 1.0E-7 < this.box.maxY + blockY &&
                        box.maxY - 1.0E-7 > this.box.minY + blockY &&
                        box.minZ + 1.0E-7 < this.box.maxZ + blockZ &&
                        box.maxZ - 1.0E-7 > this.box.minZ + blockZ;
    }
}
