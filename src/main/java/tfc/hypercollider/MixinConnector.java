package tfc.hypercollider;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinConnector implements IMixinConfigPlugin {
    private final Cfg cfg;
    private final boolean lithiumInstalled;

    public MixinConnector() {
        cfg = new Cfg();
        {
            boolean present = false;
            try {
                Class.forName("me.jellysquid.mods.lithium.common.config.MixinConfig");
                present = true;
            } catch (Throwable err) {
            }
            lithiumInstalled = present;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!lithiumInstalled && mixinClassName.contains("lithium")) {
            return false; // automatically denied, considering the target isn't present
        }
        return cfg.shouldApply(mixinClassName, targetClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
