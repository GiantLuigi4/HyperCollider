package tfc.hypercollider.mixin.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import tfc.hypercollider.ShapeChecker;

import java.util.Iterator;
import java.util.List;

@Mixin(CollisionGetter.class)
public interface FastNoCollisionCheck {
    @Shadow
    Iterable<VoxelShape> getBlockCollisions(@Nullable Entity entity, AABB aABB);

    @Shadow
    List<VoxelShape> getEntityCollisions(@Nullable Entity entity, AABB aABB);

    @Shadow
    @Nullable VoxelShape borderCollision(Entity entity, AABB aABB);

    /**
     * @author
     * @reason
     */
    @Overwrite
    default boolean noCollision(@Nullable Entity entity, AABB aABB) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        CollisionGetter getter = (CollisionGetter) this;
        CollisionContext context;
        if (entity != null) {
            context = CollisionContext.of(entity);
        } else {
            context = CollisionContext.empty();
        }

        VoxelShape entShape = Shapes.create(aABB);

        for (int i = (int) Math.floor(aABB.minX); i <= (int) Math.ceil(aABB.maxX); ++i) {
            for (int k = (int) Math.floor(aABB.minZ); k <= (int) Math.ceil(aABB.maxZ); ++k) {
                BlockGetter chunk = getter.getChunkForCollisions(
                        SectionPos.blockToSectionCoord(i),
                        SectionPos.blockToSectionCoord(k)
                );
                if (chunk == null) continue;

                for (int j = (int) Math.floor(aABB.minY); j <= (int) Math.ceil(aABB.maxY); ++j) {
                    mutableBlockPos.set(i, j, k);
                    BlockState state = chunk.getBlockState(mutableBlockPos);
                    if (state.isAir()) continue;

                    VoxelShape voxelShape = state.getCollisionShape(
                            (BlockGetter) this,
                            mutableBlockPos,
                            context
                    );

                    if (!voxelShape.isEmpty()) {
                        if (ShapeChecker.checkCollision(
                                aABB,
                                voxelShape,
                                i, j, k,
                                entShape
                        )) {
                            return false;
                        }
                    }
                }
            }
        }

        if (!this.getEntityCollisions(entity, aABB).isEmpty()) {
            return false;
        } else if (entity == null) {
            return true;
        } else {
            VoxelShape voxelShape2 = this.borderCollision(entity, aABB);
            return voxelShape2 == null || !Shapes.joinIsNotEmpty(voxelShape2, Shapes.create(aABB), BooleanOp.AND);
        }
    }
}
