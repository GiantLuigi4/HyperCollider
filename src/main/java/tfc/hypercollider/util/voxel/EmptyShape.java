package tfc.hypercollider.util.voxel;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.Util;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import tfc.hypercollider.util.logic.ColliderUtil;
import tfc.hypercollider.util.voxel.discrete.EmptyDiscreteShape;

import java.util.List;

public class EmptyShape extends VoxelShape {
    public static EmptyShape create() {
//        DiscreteVoxelShape discreteVoxelShape = new BitSetDiscreteVoxelShape(1, 1, 1);
//        discreteVoxelShape.fill(0, 0, 0);
//        return new EmptyShape(discreteVoxelShape);
        return new EmptyShape(EmptyDiscreteShape.INSTANCE);
    }

    public EmptyShape(DiscreteVoxelShape shape) {
        super(shape);
    }

    @Override
    public DoubleList getCoords(Direction.Axis axis) {
        return DoubleList.of(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

    @Override
    public List<AABB> toAabbs() {
        return List.of();
    }

    @Override
    public AABB bounds() {
        throw Util.pauseInIde(new UnsupportedOperationException("No bounds for empty shape."));
    }

    @Override
    public VoxelShape move(double xOffset, double yOffset, double zOffset) {
        return this;
    }

    @Override
    public double min(Direction.Axis axis) {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double max(Direction.Axis axis) {
        return Double.NEGATIVE_INFINITY;
    }

    @Override
    protected double collideX(AxisCycle movementAxis, AABB collisionBox, double desiredOffset) {
        return desiredOffset;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public VoxelShape optimize() {
        return this;
    }

    @Override
    public void forAllEdges(Shapes.DoubleLineConsumer action) {
    }

    @Override
    public void forAllBoxes(Shapes.DoubleLineConsumer action) {
    }

    @Override
    public VoxelShape getFaceShape(Direction side) {
        return this;
    }

    @Nullable
    @Override
    public BlockHitResult clip(Vec3 startVec, Vec3 endVec, BlockPos pos) {
//        return BlockHitResult.miss(
//                startVec, Direction.getNearest(
//                        endVec.x - startVec.x,
//                        endVec.y - startVec.y,
//                        endVec.z - startVec.z
//                ), pos
//        );
        return null;
    }

    @Override
    public double collide(Direction.Axis movementAxis, AABB collisionBox, double desiredOffset) {
        return desiredOffset;
    }

    @Override
    protected double get(Direction.Axis axis, int index) {
        return 0;
    }

    @Override
    protected int findIndex(Direction.Axis axis, double position) {
        return 0;
    }
}
