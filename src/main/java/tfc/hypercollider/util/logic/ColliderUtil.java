package tfc.hypercollider.util.logic;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class ColliderUtil {
    public static double swivelOffset(
            AxisCycle axiscycle, AABB pCollisionBox, double offsetX,
            double bMinX, double bMinY, double bMinZ,
            double bMaxX, double bMaxY, double bMaxZ
    ) {
        Direction.Axis ySwivel = axiscycle.cycle(Direction.Axis.Y);

        if (
                pCollisionBox.max(ySwivel) <= ySwivel.choose(bMinX, bMinY, bMinZ) ||
                        pCollisionBox.min(ySwivel) >= ySwivel.choose(bMaxX, bMaxY, bMaxZ)
        ) {
            return offsetX;
        }

        Direction.Axis zSwivel = axiscycle.cycle(Direction.Axis.Z);

        if (
                pCollisionBox.max(zSwivel) <= zSwivel.choose(bMinX, bMinY, bMinZ) ||
                        pCollisionBox.min(zSwivel) >= zSwivel.choose(bMaxX, bMaxY, bMaxZ)
        ) {
            return offsetX;
        }

        Direction.Axis xSwivel = axiscycle.cycle(Direction.Axis.X);

        if (offsetX > 0.0D) {
            double tMinX = xSwivel.choose(bMinX, bMinY, bMinZ);
            double oMaxX = pCollisionBox.max(xSwivel);

            if (oMaxX <= tMinX) {
                double deltaX = tMinX - oMaxX;

                if (deltaX < offsetX) return deltaX;
            }
        } else if (offsetX < 0.0D) {
            double tMaxX = xSwivel.choose(bMaxX, bMaxY, bMaxZ);
            double oMinX = pCollisionBox.min(xSwivel);

            if (oMinX >= tMaxX) {
                double deltaX = tMaxX - oMinX;

                if (deltaX > offsetX) return deltaX;
            }
        }

        return offsetX;
    }

    public static double swivelOffset(AxisCycle axiscycle, AABB pCollisionBox, AABB box, double offsetX) {
        Direction.Axis ySwivel = axiscycle.cycle(Direction.Axis.Y);

        if (
                pCollisionBox.max(ySwivel) <= box.min(ySwivel) ||
                        pCollisionBox.min(ySwivel) >= box.max(ySwivel)
        ) {
            return offsetX;
        }

        Direction.Axis zSwivel = axiscycle.cycle(Direction.Axis.Z);

        if (
                pCollisionBox.max(zSwivel) <= box.min(zSwivel) ||
                        pCollisionBox.min(zSwivel) >= box.max(zSwivel)
        ) {
            return offsetX;
        }

        Direction.Axis xSwivel = axiscycle.cycle(Direction.Axis.X);

        if (offsetX > 0.0D) {
            double tMinX = box.min(xSwivel);
            double oMaxX = pCollisionBox.max(xSwivel);

            if (oMaxX <= tMinX) {
                double deltaX = tMinX - oMaxX;

                if (deltaX < offsetX) return deltaX;
            }
        } else if (offsetX < 0.0D) {
            double tMaxX = box.max(xSwivel);
            double oMinX = pCollisionBox.min(xSwivel);

            if (oMinX >= tMaxX) {
                double deltaX = tMaxX - oMinX;

                if (deltaX > offsetX) return deltaX;
            }
        }

        return offsetX;
    }
}
