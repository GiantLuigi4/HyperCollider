package tfc.hypercollider.util.voxel;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CubeVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tfc.hypercollider.util.logic.ColliderUtil;
import tfc.hypercollider.util.voxel.cache.CubeShapeQuery;

import java.util.List;

public class BoundingShape extends CubeVoxelShape implements CubeShapeQuery {
    AABB box;

    public BoundingShape(DiscreteVoxelShape shape, AABB box) {
        super(shape);
        this.box = box;
    }

    @Override
    public DoubleList getCoords(Direction.Axis axis) {
        return DoubleList.of(
                box.min(axis),
                box.max(axis)
        );
    }

    @Override
    public List<AABB> toAabbs() {
        return List.of(box);
    }

    @Override
    public AABB bounds() {
        return box;
    }

    @Override
    public VoxelShape move(double xOffset, double yOffset, double zOffset) {
//        return new BoundingShape(shape, box.move(xOffset, yOffset, zOffset));
        return new OffsetBoundingShape(
                shape,
                box.minX + xOffset,
                box.minY + yOffset,
                box.minZ + zOffset,
                box.maxX + xOffset,
                box.maxY + yOffset,
                box.maxZ + zOffset
        );
    }

    @Override
    public double min(Direction.Axis axis) {
        return box.min(axis);
    }

    @Override
    public double max(Direction.Axis axis) {
        return box.max(axis);
    }

    @Override
    protected double collideX(AxisCycle movementAxis, AABB collisionBox, double desiredOffset) {
        if (Math.abs(desiredOffset) < 1.0E-7) {
            return 0;
        }

        movementAxis = movementAxis.inverse();
        return ColliderUtil.swivelOffset(movementAxis, collisionBox, box, desiredOffset);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public VoxelShape optimize() {
        // already optimal
        return this;
    }

    @Override
    public void forAllBoxes(Shapes.DoubleLineConsumer action) {
        action.consume(
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ
        );
    }

    @Override
    public void forAllEdges(Shapes.DoubleLineConsumer action) {
        action.consume(box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ);
        action.consume(box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ);
        action.consume(box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ);
        action.consume(box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ);

        action.consume(box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ);
        action.consume(box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ);
        action.consume(box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ);
        action.consume(box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ);

        action.consume(box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ);
        action.consume(box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ);
        action.consume(box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ);
        action.consume(box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ);
    }

    // fast impls
    @Override
    protected double get(Direction.Axis axis, int index) {
        return switch (axis) {
            case X -> index == 0 ? box.minX : box.maxX;
            case Y -> index == 0 ? box.minY : box.maxY;
            case Z -> index == 0 ? box.minZ : box.maxZ;
        };
    }

    @Override
    protected int findIndex(Direction.Axis axis, double position) {
        double middle;
        if (position > (middle = max(axis))) return -1;
        if (position < (middle += min(axis))) return -1;
        if (position > middle * 0.5) return 1;
        return 0;
    }

    @Override
    public boolean isCube() {
        return true;
    }
}
