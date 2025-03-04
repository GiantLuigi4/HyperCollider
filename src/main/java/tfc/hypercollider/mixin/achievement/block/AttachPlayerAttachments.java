package tfc.hypercollider.mixin.achievement.block;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.server.PlayerAdvancements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tfc.hypercollider.util.itf.CacheList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(PlayerAdvancements.class)
public class AttachPlayerAttachments implements CacheList {
    @Unique
    private Set<CriterionTrigger.Listener<?>> li;
    @Unique
    private List<CriterionTrigger.Listener<?>> liList;

    @Override
    public void setCache(Set<CriterionTrigger.Listener<?>> li) {
        this.li = li;
        liList = new ArrayList<>(li);
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
        return liList = new ArrayList<>(li);
    }
}
