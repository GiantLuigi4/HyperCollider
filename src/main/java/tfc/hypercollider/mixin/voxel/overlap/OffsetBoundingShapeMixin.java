package tfc.hypercollider.mixin.voxel.overlap;

import me.jellysquid.mods.lithium.common.shapes.VoxelShapeCaster;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfc.hypercollider.util.itf.Overlapable;
import tfc.hypercollider.util.voxel.BoundingShape;
import tfc.hypercollider.util.voxel.OffsetBoundingShape;

@Mixin(OffsetBoundingShape.class)
public class OffsetBoundingShapeMixin implements Overlapable {
    @Shadow
    double maxX;
    @Shadow
    double maxY;
    @Shadow
    double maxZ;
    @Shadow
    double minX;
    @Shadow
    double minY;
    @Shadow
    double minZ;

    @Override
    public boolean overlaps(AABB box, double blockX, double blockY, double blockZ) {
        return
                box.minX + 1.0E-7 < this.maxX + blockX &&
                        box.maxX - 1.0E-7 > this.minX + blockX &&
                        box.minY + 1.0E-7 < this.maxY + blockY &&
                        box.maxY - 1.0E-7 > this.minY + blockY &&
                        box.minZ + 1.0E-7 < this.maxZ + blockZ &&
                        box.maxZ - 1.0E-7 > this.minZ + blockZ;
    }
}
