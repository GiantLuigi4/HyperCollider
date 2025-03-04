package tfc.hypercollider.mixin.achievement.access;

import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractCriterionTriggerInstance.class)
public interface AbstractCriterionAccessor {
    @Invoker
    ContextAwarePredicate callGetPlayerPredicate();
}
