package tfc.hypercollider.util.itf;

import net.minecraft.world.level.storage.loot.LootContext;

public interface PredicateCache {
    LootContext getSelfContext();

    void setSelfContext(LootContext context);
}
