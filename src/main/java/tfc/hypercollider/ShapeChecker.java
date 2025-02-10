package tfc.hypercollider;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;

public class ShapeChecker {
    public static boolean checkCollision(
            AABB box,
            VoxelShape shape,
            int x, int y, int z,
            VoxelShape entityShape
    ) {
        if (shape == Shapes.block()) {
            return box.intersects(
                    x, y, z,
                    x + 1, y + 1, z + 1
            );
        }

        if (shape.getClass().equals(CubeVoxelShape.class)) {
            AABB bounds = shape.bounds();
            return box.intersects(
                    bounds.minX + x,
                    bounds.minY + y,
                    bounds.minZ + z,
                    bounds.maxX + x,
                    bounds.maxY + y,
                    bounds.maxZ + z
            );
        }

        if (shape.getClass().equals(ArrayVoxelShape.class)) {
            for (AABB bounds : shape.toAabbs()) {
                if (box.intersects(
                        bounds.minX + x,
                        bounds.minY + y,
                        bounds.minZ + z,
                        bounds.maxX + x,
                        bounds.maxY + y,
                        bounds.maxZ + z
                )) {
                    return false;
                }
            }
            return true;
        }

        VoxelShape voxelShape2 = shape.move(x, y, z);
        return Shapes.joinIsNotEmpty(
                voxelShape2,
                entityShape,
                BooleanOp.AND
        );
    }
}
