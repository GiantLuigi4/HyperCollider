package tfc.hypercollider.util.itf;

import net.minecraft.world.phys.AABB;

public interface Overlapable {
    default boolean overlaps(AABB box) {
        return overlaps(box, 0, 0, 0);
    }

    boolean overlaps(AABB box, double offX, double offY, double offZ);
}
