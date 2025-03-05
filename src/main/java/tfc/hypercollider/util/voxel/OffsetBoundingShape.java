package tfc.hypercollider.util.voxel;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tfc.hypercollider.util.logic.ColliderUtil;
import tfc.hypercollider.util.voxel.cache.CubeShapeQuery;

import java.util.ArrayList;
import java.util.List;

public class OffsetBoundingShape extends VoxelShape implements CubeShapeQuery {
    double minX, minY, minZ, maxX, maxY, maxZ;
    List<AABB> bounds;

    public OffsetBoundingShape(DiscreteVoxelShape shape, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        super(shape);
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    @Override
    public DoubleList getCoords(Direction.Axis axis) {
        return switch (axis) {
            case X -> DoubleList.of(minX, maxX);
            case Y -> DoubleList.of(minY, maxY);
            case Z -> DoubleList.of(minZ, maxZ);
        };
    }

    @Override
    public List<AABB> toAabbs() {
        if (bounds == null) {
            bounds = new ArrayList<>();
            bounds.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
        }
        return bounds;
    }

    @Override
    public AABB bounds() {
        return toAabbs().get(0);
    }

    @Override
    public double min(Direction.Axis axis) {
        return switch (axis) {
            case X -> minX;
            case Y -> minY;
            case Z -> minZ;
        };
    }

    @Override
    public double max(Direction.Axis axis) {
        return switch (axis) {
            case X -> maxX;
            case Y -> maxY;
            case Z -> maxZ;
        };
    }

    @Override
    public void forAllBoxes(Shapes.DoubleLineConsumer action) {
        action.consume(
                minX, minY, minZ, maxX, maxY, maxZ
        );
    }

    @Override
    public void forAllEdges(Shapes.DoubleLineConsumer action) {
        action.consume(minX, minY, minZ, maxX, minY, minZ);
        action.consume(maxX, minY, minZ, maxX, minY, maxZ);
        action.consume(maxX, minY, maxZ, minX, minY, maxZ);
        action.consume(minX, minY, maxZ, minX, minY, minZ);

        action.consume(minX, maxY, minZ, maxX, maxY, minZ);
        action.consume(maxX, maxY, minZ, maxX, maxY, maxZ);
        action.consume(maxX, maxY, maxZ, minX, maxY, maxZ);
        action.consume(minX, maxY, maxZ, minX, maxY, minZ);

        action.consume(minX, minY, minZ, minX, maxY, minZ);
        action.consume(maxX, minY, minZ, maxX, maxY, minZ);
        action.consume(maxX, minY, maxZ, maxX, maxY, maxZ);
        action.consume(minX, minY, maxZ, minX, maxY, maxZ);
    }

    @Override
    protected double collideX(AxisCycle movementAxis, AABB collisionBox, double desiredOffset) {
        if (Math.abs(desiredOffset) < 1.0E-7) {
            return 0;
        }

        movementAxis = movementAxis.inverse();
        return ColliderUtil.swivelOffset(
                movementAxis, collisionBox, desiredOffset,
                minX, minY, minZ, maxX, maxY, maxZ
        );
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
    public VoxelShape move(double xOffset, double yOffset, double zOffset) {
        return new OffsetBoundingShape(
                shape,
                minX + xOffset,
                minY + yOffset,
                minZ + zOffset,
                maxX + xOffset,
                maxY + yOffset,
                maxZ + zOffset
        );
    }

    // fast impls
    @Override
    protected double get(Direction.Axis axis, int index) {
        return switch (axis) {
            case X -> index == 0 ? minX : maxX;
            case Y -> index == 0 ? minY : maxY;
            case Z -> index == 0 ? minZ : maxZ;
        };
    }

    @Override
    protected int findIndex(Direction.Axis axis, double position) {
        return Math.min(Math.max((int) position, -1), 1);
    }

    @Override
    public boolean isCube() {
        return true;
    }
}
