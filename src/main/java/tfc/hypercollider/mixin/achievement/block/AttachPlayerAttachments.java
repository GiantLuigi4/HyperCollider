package tfc.hypercollider.mixin.achievement.block;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfc.hypercollider.mixin.achievement.access.EnterBlockTriggerInstAccessor;
import tfc.hypercollider.util.itf.CacheList;

import java.util.*;

@Mixin(PlayerAdvancements.class)
public class AttachPlayerAttachments implements CacheList {
    @Unique
    private Set<CriterionTrigger.Listener<?>> li;
    @Unique
    private List<CriterionTrigger.Listener<?>> liList;
    @Unique
    private Map<Block, List<CriterionTrigger.Listener<?>>> listeners = new HashMap<>();

    @Override
    public void setCache(Set<CriterionTrigger.Listener<?>> li) {
        this.li = li;
        updateList();
    }

    @Override
    public Set<CriterionTrigger.Listener<?>> getCache() {
        return li;
    }

    @Override
    public List<CriterionTrigger.Listener<?>> getCacheList() {
        return liList;
    }

    @Override
    public List<CriterionTrigger.Listener<?>> updateList() {
        liList = new ArrayList<>(li);
        listeners.clear();
        for (CriterionTrigger.Listener<?> listener : li) {
            EnterBlockTrigger.TriggerInstance inst = ((EnterBlockTrigger.TriggerInstance) listener.getTriggerInstance());
            Block blk = ((EnterBlockTriggerInstAccessor) inst).getBlock();

            List<CriterionTrigger.Listener<?>> instances = listeners.get(blk);
            //noinspection Java8MapApi
            if (instances == null) listeners.put(blk, instances = new ArrayList<>());
            instances.add(listener);
        }
        return liList;
    }

    @Override
    public Map<Block, List<CriterionTrigger.Listener<?>>> getMap() {
        return listeners;
    }
}
