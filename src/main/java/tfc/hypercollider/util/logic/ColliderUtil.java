package tfc.hypercollider.util.logic;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class ColliderUtil {
    public static double swivelOffset(AxisCycle axiscycle, AABB pCollisionBox, AABB box, double offsetX) {
        Direction.Axis xSwivel = axiscycle.cycle(Direction.Axis.X);
        Direction.Axis ySwivel = axiscycle.cycle(Direction.Axis.Y);
        Direction.Axis zSwivel = axiscycle.cycle(Direction.Axis.Z);

        double tMinX = box.min(xSwivel);
        double tMaxX = box.max(xSwivel);
        double oMaxX = pCollisionBox.max(xSwivel);
        double oMinX = pCollisionBox.min(xSwivel);

        double tMinZ = box.min(ySwivel);
        double tMaxZ = box.max(ySwivel);
        double oMaxZ = pCollisionBox.max(ySwivel);
        double oMinZ = pCollisionBox.min(ySwivel);

        double tMaxY = box.max(zSwivel);
        double tMinY = box.min(zSwivel);
        double oMaxY = pCollisionBox.max(zSwivel);
        double oMinY = pCollisionBox.min(zSwivel);

        if (oMaxY > tMinY && oMinY < tMaxY && oMaxZ > tMinZ && oMinZ < tMaxZ) {
            if (offsetX > 0.0D && oMaxX <= tMinX) {
                double deltaX = tMinX - oMaxX;

                if (deltaX < offsetX) return deltaX;
            } else if (offsetX < 0.0D && oMinX >= tMaxX) {
                double deltaX = tMaxX - oMinX;

                if (deltaX > offsetX) return deltaX;
            }
        }

        return offsetX;
    }
}
