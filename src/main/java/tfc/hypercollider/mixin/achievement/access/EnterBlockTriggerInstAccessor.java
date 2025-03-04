package tfc.hypercollider.mixin.achievement.access;

import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EnterBlockTrigger.TriggerInstance.class)
public interface EnterBlockTriggerInstAccessor {
    @Accessor
    Block getBlock();
}
