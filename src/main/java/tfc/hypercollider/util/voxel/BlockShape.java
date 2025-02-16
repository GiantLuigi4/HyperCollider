package tfc.hypercollider.util.voxel;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import tfc.hypercollider.util.logic.ColliderUtil;

import java.util.List;

public class BlockShape extends ArrayVoxelShape {
    AABB box;

    public BlockShape(
            DiscreteVoxelShape shape, AABB box,
            DoubleList coordsX,
            DoubleList coordsY,
            DoubleList coordsZ
    ) {
        super(shape, coordsX, coordsY, coordsZ);
        this.box = box;
    }

    @Override
    public DoubleList getCoords(Direction.Axis axis) {
        return super.getCoords(axis);
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
        return new BlockShape(
                shape,
                box.move(xOffset, yOffset, zOffset),
                // TODO: validate (should offset be added to the list?)
                getCoords(Direction.Axis.X),
                getCoords(Direction.Axis.Y),
                getCoords(Direction.Axis.Z)
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
        AABB other = this.bounds();
        return ColliderUtil.swivelOffset(movementAxis, collisionBox, other, desiredOffset);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public VoxelShape optimize() {
        return this;
    }

    @Override
    public void forAllBoxes(Shapes.DoubleLineConsumer action) {
        action.consume(
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ
        );
    }
}
