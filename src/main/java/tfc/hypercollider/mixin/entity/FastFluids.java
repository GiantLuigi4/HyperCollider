package tfc.hypercollider.mixin.entity;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class FastFluids {
    @Shadow
    public abstract boolean touchingUnloadedChunk();

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract boolean isPushedByFluid();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract void setDeltaMovement(Vec3 vec3);

    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tagKey, double d) {
        if (this.touchingUnloadedChunk()) {
            return false;
        } else {
            AABB aABB = this.getBoundingBox().deflate(0.001);
            int i = Mth.floor(aABB.minX);
            int j = Mth.ceil(aABB.maxX);
            int k = Mth.floor(aABB.minY);
            int l = Mth.ceil(aABB.maxY);
            int m = Mth.floor(aABB.minZ);
            int n = Mth.ceil(aABB.maxZ);
            double e = 0.0;
            boolean bl = this.isPushedByFluid();
            boolean bl2 = false;
            Vec3 vec3 = Vec3.ZERO;
            int o = 0;
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int p = i; p < j; ++p) {
                for (int r = m; r < n; ++r) {
                    ChunkAccess chunk = this.level().getChunk(
                            SectionPos.blockToSectionCoord(p),
                            SectionPos.blockToSectionCoord(r),
                            ChunkStatus.FULL, false
                    );
                    if (chunk == null) continue;

                    for (int q = k; q < l; ++q) {
                        mutableBlockPos.set(p, q, r);
                        FluidState fluidState = chunk.getFluidState(mutableBlockPos);
                        if (fluidState.isEmpty()) continue; // nothing to do

                        if (fluidState.is(tagKey)) {
                            double f = (float) q + fluidState.getHeight(this.level(), mutableBlockPos);
                            if (f >= aABB.minY) {
                                bl2 = true;
                                e = Math.max(f - aABB.minY, e);
                                if (bl) {
                                    Vec3 vec32 = fluidState.getFlow(this.level(), mutableBlockPos);
                                    if (e < 0.4) {
                                        vec32 = vec32.scale(e);
                                    }

                                    vec3 = vec3.add(vec32);
                                    ++o;
                                }
                            }
                        }
                    }
                }
            }

            if (vec3.length() > 0.0) {
                if (o > 0) {
                    vec3 = vec3.scale(1.0 / (double) o);
                }

                //noinspection ConstantValue
                if (!(((Entity) (Object) this) instanceof Player)) {
                    vec3 = vec3.normalize();
                }

                Vec3 vec33 = this.getDeltaMovement();
                vec3 = vec3.scale(d);
                if (Math.abs(vec33.x) < 0.003 && Math.abs(vec33.z) < 0.003 && vec3.length() < 0.0045000000000000005) {
                    vec3 = vec3.normalize().scale(0.0045000000000000005);
                }

                this.setDeltaMovement(this.getDeltaMovement().add(vec3));
            }

            this.fluidHeight.put(tagKey, e);
            return bl2;
        }
    }
}
