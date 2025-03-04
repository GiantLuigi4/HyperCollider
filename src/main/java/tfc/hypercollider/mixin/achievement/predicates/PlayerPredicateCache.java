package tfc.hypercollider.mixin.achievement.predicates;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfc.hypercollider.util.itf.PredicateCache;

@Mixin(ServerPlayer.class)
public class PlayerPredicateCache implements PredicateCache {
    @Unique
    private LootContext predicate;

    @Override
    public LootContext getSelfContext() {
        return predicate;
    }

    @Override
    public void setSelfContext(LootContext context) {
        this.predicate = context;
    }
}
