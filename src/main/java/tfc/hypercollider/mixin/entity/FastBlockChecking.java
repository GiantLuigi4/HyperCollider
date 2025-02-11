package tfc.hypercollider.mixin.entity;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;
import java.util.stream.Stream;

@Mixin(Entity.class)
public abstract class FastBlockChecking {
    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract Level level();

    @Shadow
    protected abstract void onInsideBlock(BlockState blockState);

    @Shadow
    public boolean wasOnFire;

    @Shadow
    protected abstract void playEntityOnFireExtinguishedSound();

    @Shadow
    public boolean isInPowderSnow;

    @Shadow
    public abstract boolean isInWaterRainOrBubble();

    @Shadow
    protected abstract int getFireImmuneTicks();

    @Shadow
    public abstract void setRemainingFireTicks(int i);

    @Shadow
    private int remainingFireTicks;

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;noneMatch(Ljava/util/function/Predicate;)Z"))
    public boolean doNotMatch(Stream instance, Predicate predicate) {
        return false;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void checkInsideBlocks() {
        AABB aABB = this.getBoundingBox();
        BlockPos blockPos = BlockPos.containing(aABB.minX + 1.0E-7, aABB.minY + 1.0E-7, aABB.minZ + 1.0E-7);
        BlockPos blockPos2 = BlockPos.containing(aABB.maxX - 1.0E-7, aABB.maxY - 1.0E-7, aABB.maxZ - 1.0E-7);

        if (this.level().hasChunksAt(blockPos, blockPos2)) {
            AABB deflate = this.getBoundingBox().deflate(1E-6);
            boolean touchingFire = false;

            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

            for (int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    ChunkAccess chunk = this.level().getChunk(
                            SectionPos.blockToSectionCoord(i),
                            SectionPos.blockToSectionCoord(k),
                            ChunkStatus.FULL, false
                    );
                    if (chunk == null) continue;

                    for (int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                        mutableBlockPos.set(i, j, k);
                        BlockState blockState = chunk.getBlockState(mutableBlockPos);

                        if (blockState.isAir()) {
                            try {
                                blockState.entityInside(this.level(), mutableBlockPos, (Entity) (Object) this);
                                this.onInsideBlock(blockState);
                            } catch (Throwable var12) {
                                CrashReport crashReport = CrashReport.forThrowable(var12, "Colliding entity with block");
                                CrashReportCategory crashReportCategory = crashReport.addCategory("Block being collided with");
                                CrashReportCategory.populateBlockDetails(crashReportCategory, this.level(), mutableBlockPos, blockState);
                                throw new ReportedException(crashReport);
                            }
                            continue;
                        }

                        if (!touchingFire) { // only need one match
                            if (blockState.is(BlockTags.FIRE) || blockState.is(Blocks.LAVA)) {
                                // match vanilla logic
                                if (deflate.intersects(
                                        mutableBlockPos.getX(), mutableBlockPos.getY(), mutableBlockPos.getZ(),
                                        mutableBlockPos.getX() + 1, mutableBlockPos.getY() + 1, mutableBlockPos.getZ() + 1
                                )) {
                                    touchingFire = true;
                                }
                            }
                        }

                        try {
                            blockState.entityInside(this.level(), mutableBlockPos, (Entity) (Object) this);
                            this.onInsideBlock(blockState);
                        } catch (Throwable var12) {
                            CrashReport crashReport = CrashReport.forThrowable(var12, "Colliding entity with block");
                            CrashReportCategory crashReportCategory = crashReport.addCategory("Block being collided with");
                            CrashReportCategory.populateBlockDetails(crashReportCategory, this.level(), mutableBlockPos, blockState);
                            throw new ReportedException(crashReport);
                        }
                    }
                }
            }

            // if not in fire, do this
            if (!touchingFire) {
                if (this.remainingFireTicks <= 0) {
                    this.setRemainingFireTicks(-this.getFireImmuneTicks());
                }

                if (this.wasOnFire && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
                    this.playEntityOnFireExtinguishedSound();
                }
            }
        }
    }
}
