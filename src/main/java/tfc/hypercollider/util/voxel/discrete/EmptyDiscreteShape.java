package tfc.hypercollider.util.voxel.discrete;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class EmptyDiscreteShape extends BitSetDiscreteVoxelShape {
    public static final DiscreteVoxelShape INSTANCE = new EmptyDiscreteShape();

    public EmptyDiscreteShape() {
//        super(xSize, ySize, zSize);
        super(0, 0, 0);
    }

    @Override
    protected int getIndex(int x, int y, int z) {
        return 0;
    }

    @Override
    public boolean isFull(int x, int y, int z) {
        return false;
    }

    @Override
    public void fill(int x, int y, int z) {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int firstFull(Direction.Axis axis) {
        return 1;
    }

    @Override
    public int lastFull(Direction.Axis axis) {
        return 0;
    }

    @Override
    public boolean isFullWide(AxisCycle axis, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isFullWide(int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isFull(AxisCycle rotation, int x, int y, int z) {
        return false;
    }

    @Override
    public int firstFull(Direction.Axis axis, int y, int z) {
        return 1;
    }

    @Override
    public int lastFull(Direction.Axis axis, int y, int z) {
        return 0;
    }

    @Override
    public int getSize(Direction.Axis axis) {
        return 0;
    }

    @Override
    public int getXSize() {
        return 0;
    }

    @Override
    public int getYSize() {
        return 0;
    }

    @Override
    public int getZSize() {
        return 0;
    }

    @Override
    public void forAllEdges(IntLineConsumer consumer, boolean combine) {
    }

    @Override
    public void forAllBoxes(IntLineConsumer consumer, boolean combine) {
    }

    @Override
    public void forAllFaces(IntFaceConsumer faceConsumer) {
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
