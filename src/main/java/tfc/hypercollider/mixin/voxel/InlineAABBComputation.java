package tfc.hypercollider.mixin.voxel;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(VoxelShape.class)
public abstract class InlineAABBComputation {
    @Shadow
    protected abstract DoubleList getCoords(Direction.Axis axis);

    @Shadow
    @Final
    public DiscreteVoxelShape shape;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public List<AABB> toAabbs() {
        DoubleList dlx = getCoords(Direction.Axis.X);
        DoubleList dly = getCoords(Direction.Axis.Y);
        DoubleList dlz = getCoords(Direction.Axis.Z);

        List<AABB> res = new ArrayList<>();
        this.shape.forAllBoxes(
                (i, j, k, l, m, n) -> {
                    res.add(new AABB(
                            dlx.get(i),
                            dly.get(j),
                            dlz.get(k),
                            dlx.get(l),
                            dly.get(m),
                            dlz.get(n)
                    ));
                }, true
        );

        return res;
    }
}
