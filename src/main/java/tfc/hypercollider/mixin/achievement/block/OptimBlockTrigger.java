package tfc.hypercollider.mixin.achievement.block;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import tfc.hypercollider.mixin.achievement.access.AbstractCriterionAccessor;
import tfc.hypercollider.mixin.achievement.access.CriterionAccessor;
import tfc.hypercollider.util.itf.CacheList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(EnterBlockTrigger.class)
public abstract class OptimBlockTrigger extends SimpleCriterionTrigger<EnterBlockTrigger.TriggerInstance> {
    @Override
    protected void trigger(ServerPlayer player, Predicate<EnterBlockTrigger.TriggerInstance> testTrigger) {
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        List<Listener<?>> lis = ((CacheList) playerAdvancements).getCacheList();
        if (lis == null) {
            Set<CriterionTrigger.Listener<?>> set = (Set) ((CriterionAccessor) this).getPlayers().get(playerAdvancements);
            ((CacheList) playerAdvancements).setCache(set);
            lis = ((CacheList) playerAdvancements).getCacheList();
        } else {
            Set<Listener<?>> slis = ((CacheList) playerAdvancements).getCache();
            if (slis.size() != lis.size()) {
                lis = ((CacheList) playerAdvancements).updateList();
            }
        }

        LootContext lootContext = null;

        for (Listener<?> li : lis) {
            EnterBlockTrigger.TriggerInstance instance = (EnterBlockTrigger.TriggerInstance) li.getTriggerInstance();
            if (testTrigger.test(instance)
                    && ((AbstractCriterionAccessor) instance).callGetPlayerPredicate()
                    .matches(
                            lootContext == null ?
                                    (lootContext = EntityPredicate.createContext(player, player)) :
                                    lootContext
                    )
            ) {
                li.run(playerAdvancements);
            }
        }
    }

    /**
     * @author GiantLuigi4
     * @reason rewrite this logic to actually be efficient
     */
    @Overwrite
    public void trigger(ServerPlayer player, BlockState state) {
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        List<Listener<?>> lis = ((CacheList) playerAdvancements).getCacheList();
        if (lis == null) {
            Set<CriterionTrigger.Listener<?>> set = (Set) ((CriterionAccessor) this).getPlayers().get(playerAdvancements);
            ((CacheList) playerAdvancements).setCache(set);
            ((CacheList) playerAdvancements).getCacheList();
        } else {
            Set<Listener<?>> slis = ((CacheList) playerAdvancements).getCache();
            if (slis.size() != lis.size()) {
                ((CacheList) playerAdvancements).updateList();
            }
        }

        LootContext lootContext = null;

        Map<Block, List<Listener<?>>> mp = ((CacheList) playerAdvancements).getMap();
        if (mp.isEmpty()) return;

        List<Listener<?>> blkListeners = mp.get(state.getBlock());
        if (blkListeners == null) return;
        for (Listener<?> li : blkListeners) {
            EnterBlockTrigger.TriggerInstance instance = (EnterBlockTrigger.TriggerInstance) li.getTriggerInstance();
            if (instance.matches(state)
                    && ((AbstractCriterionAccessor) instance).callGetPlayerPredicate()
                    .matches(
                            lootContext == null ?
                                    (lootContext = EntityPredicate.createContext(player, player)) :
                                    lootContext
                    )
            ) {
                li.run(playerAdvancements);
            }
        }
    }
}
