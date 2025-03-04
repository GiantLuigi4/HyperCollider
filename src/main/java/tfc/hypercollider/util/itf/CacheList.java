package tfc.hypercollider.util.itf;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;

import java.util.List;
import java.util.Set;

public interface CacheList {
    void setCache(Set<CriterionTrigger.Listener<?>> li);
    Set<CriterionTrigger.Listener<?>> getCache();
    List<CriterionTrigger.Listener<?>> getCacheList();
    List<CriterionTrigger.Listener<?>> updateList();
}
