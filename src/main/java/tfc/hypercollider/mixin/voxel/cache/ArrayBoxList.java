package tfc.hypercollider.mixin.voxel.cache;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfc.hypercollider.util.collection.ReadOnlyList;
import tfc.hypercollider.util.voxel.cache.BounderCachedShape;

import java.util.List;

// sacrifices some RAM to reduce computations
@Mixin(ArrayVoxelShape.class)
public abstract class ArrayBoxList extends VoxelShape implements BounderCachedShape {
    @Shadow @Final private DoubleList xs;

    @Shadow @Final private DoubleList ys;

    @Shadow @Final private DoubleList zs;

    public ArrayBoxList(DiscreteVoxelShape shape) {
        super(shape);
    }

    List<AABB> bounds;

    @Override
    public List<AABB> toAabbs() {
        if (bounds == null) {
            this.bounds = super.toAabbs();
            this.bounds = new ReadOnlyList<>(this.bounds.toArray(new AABB[0]));
        }
        return bounds;
    }

    @Override
    public VoxelShape move(double xOffset, double yOffset, double zOffset) {
        VoxelShape res = new ArrayVoxelShape(
                shape,
                new OffsetDoubleList(xs, xOffset),
                new OffsetDoubleList(ys, yOffset),
                new OffsetDoubleList(zs, zOffset)
        );
        if (res instanceof BounderCachedShape bounder) {
            List<AABB> in = toAabbs();
            AABB[] resLi = new AABB[in.size()];
            for (int i = 0; i < resLi.length; i++) {
                resLi[i] = in.get(i).move(xOffset, yOffset, zOffset);
            }
            bounder.setCache(new ReadOnlyList<>(resLi));
        }
        return res;
    }

    @Override
    public void setCache(List<AABB> boxes) {
        this.bounds = boxes;
    }
}
