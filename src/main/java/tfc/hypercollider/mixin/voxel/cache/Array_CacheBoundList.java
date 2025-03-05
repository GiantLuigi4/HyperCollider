package tfc.hypercollider.mixin.voxel.cache;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import tfc.hypercollider.util.voxel.cache.BounderCachedShape;

import java.util.List;

// sacrifices some RAM to reduce computations
@Mixin(ArrayVoxelShape.class)
public abstract class Array_CacheBoundList extends VoxelShape implements BounderCachedShape {
    public Array_CacheBoundList(DiscreteVoxelShape shape) {
        super(shape);
    }

    List<AABB> bounds;

    @Override
    public List<AABB> toAabbs() {
        if (bounds == null) {
            this.bounds = super.toAabbs();
        }
        return bounds;
    }

    @Override
    public VoxelShape move(double xOffset, double yOffset, double zOffset) {
        VoxelShape res = super.move(xOffset, yOffset, zOffset);
        if (res instanceof BounderCachedShape bounder) {
            List<AABB> in = toAabbs();
            AABB[] resLi = new AABB[in.size()];
            for (int i = 0; i < resLi.length; i++) {
                resLi[i] = in.get(i).move(xOffset, yOffset, zOffset);
            }
            bounder.setCache(ImmutableList.copyOf(resLi));
        }
        return res;
    }

    @Override
    public void setCache(List<AABB> boxes) {
        this.bounds = boxes;
    }
}
