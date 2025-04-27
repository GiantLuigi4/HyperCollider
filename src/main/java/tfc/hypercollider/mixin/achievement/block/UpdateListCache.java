package tfc.hypercollider.mixin.achievement.block;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.hypercollider.mixin.achievement.access.CriterionAccessor;
import tfc.hypercollider.util.itf.CacheList;

import java.util.Set;

@Mixin(SimpleCriterionTrigger.class)
public class UpdateListCache {
    @Unique
    boolean isBlockEnter;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void postInit(CallbackInfo ci) {
        isBlockEnter = ((Object) this) instanceof EnterBlockTrigger;
    }

    @Inject(at = @At("TAIL"), method = "addPlayerListener")
    public <T extends AbstractCriterionTriggerInstance> void postInject(PlayerAdvancements playerAdvancements, CriterionTrigger.Listener<T> listener, CallbackInfo ci) {
        if (isBlockEnter) {
            Set<CriterionTrigger.Listener<?>> set = (Set) ((CriterionAccessor) this).getPlayers().get(playerAdvancements);
            ((CacheList) playerAdvancements).setCache(set);
        }
    }
}
