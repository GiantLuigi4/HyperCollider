package tfc.hypercollider.util.voxel.cache;

import net.minecraft.world.phys.AABB;

import java.util.List;

public interface BounderCachedShape {
    void setCache(List<AABB> boxes);
}
