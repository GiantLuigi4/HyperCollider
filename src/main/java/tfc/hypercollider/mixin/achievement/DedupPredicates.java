package tfc.hypercollider.mixin.achievement;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.hypercollider.itf.PredicateCache;

@Mixin(EntityPredicate.class)
public class DedupPredicates {
    @Inject(at = @At("HEAD"), method = "createContext", cancellable = true)
    private static void preGet(ServerPlayer serverPlayer, Entity entity, CallbackInfoReturnable<LootContext> cir) {
        if (serverPlayer == entity) {
            LootContext ctx = ((PredicateCache) serverPlayer).getSelfContext();
            if (ctx != null) {
                cir.setReturnValue(ctx);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "createContext")
    private static void postGet(ServerPlayer serverPlayer, Entity entity, CallbackInfoReturnable<LootContext> cir) {
        if (serverPlayer == entity) {
            ((PredicateCache) serverPlayer).setSelfContext(cir.getReturnValue());
        }
    }
}
