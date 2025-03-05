package tfc.hypercollider.util.voxel.discrete;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class SingleBitDiscreteShape extends BitSetDiscreteVoxelShape {
    boolean value;

    public SingleBitDiscreteShape() {
        super(1, 1, 1);
    }

    @Override
    protected int getIndex(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) return 0;
        return -1;
    }

    @Override
    public boolean isFull(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) return value;
        return false;
    }

    @Override
    public void fill(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) {
            value = true;
            super.fill(x, y, z);
        }
    }

    @Override
    public boolean isEmpty() {
        return !value;
    }

    @Override
    public int firstFull(Direction.Axis axis) {
        if (value) return 0;
        return 1;
    }

    @Override
    public int lastFull(Direction.Axis axis) {
        if (value) return 1;
        return 0;
    }

    @Override
    public boolean isFullWide(AxisCycle axis, int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) return value;
        return false;
    }

    @Override
    public boolean isFullWide(int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) return value;
        return false;
    }

    @Override
    public boolean isFull(AxisCycle rotation, int x, int y, int z) {
        if (x == 0 && y == 0 && z == 0) return value;
        return false;
    }

    @Override
    public int firstFull(Direction.Axis axis, int y, int z) {
        return firstFull(axis);
    }

    @Override
    public int lastFull(Direction.Axis axis, int y, int z) {
        return lastFull(axis);
    }

    @Override
    public int getSize(Direction.Axis axis) {
        return 1;
    }

    @Override
    public int getXSize() {
        return 1;
    }

    @Override
    public int getYSize() {
        return 1;
    }

    @Override
    public int getZSize() {
        return 1;
    }

    @Override
    public void forAllEdges(IntLineConsumer consumer, boolean combine) {
        if (value)
            super.forAllEdges(consumer, combine);
    }

    @Override
    public void forAllBoxes(IntLineConsumer consumer, boolean combine) {
        if (value)
            super.forAllBoxes(consumer, combine);
    }

    @Override
    public void forAllFaces(IntFaceConsumer faceConsumer) {
        if (value)
            super.forAllFaces(faceConsumer);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SingleBitDiscreteShape that = (SingleBitDiscreteShape) object;
        return value == that.value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
