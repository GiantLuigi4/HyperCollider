package tfc.hypercollider.mixin.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import tfc.hypercollider.util.logic.SignedStepper;

import java.util.Collections;
import java.util.List;

@Mixin(Entity.class)
public abstract class FastCollision {
    private static final int MAX_PENETRATION = 1;
    private static final int COLLISION_PADDING = 1;

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract boolean onGround();

    @Shadow
    public abstract float maxUpStep();

    @Shadow
    private Level level;
    private static final ThreadLocal<Entity> colliding = new ThreadLocal<>();

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Vec3 collide(Vec3 vec3) {
        AABB aABB = this.getBoundingBox();
        List<VoxelShape> list = this.level().getEntityCollisions((Entity) (Object) this, aABB.expandTowards(vec3));
        Vec3 vec32 = vec3.lengthSqr() == 0.0 ? vec3 : collideBoundingBox((Entity) (Object) this, vec3, aABB, this.level(), list);
        boolean bl = vec3.x != vec32.x;
        boolean bl2 = vec3.y != vec32.y;
        boolean bl3 = vec3.z != vec32.z;
        boolean bl4 = this.onGround() || bl2 && vec3.y < 0.0;
        double stepUp = this.maxUpStep();
        if (stepUp > 0.0F && bl4 && (bl || bl3)) {
            Vec3 vec33 = collideBoundingBox((Entity) (Object) this, new Vec3(vec3.x, (double) stepUp, vec3.z), aABB, this.level(), list);
            Vec3 vec34 = collideBoundingBox((Entity) (Object) this, new Vec3(0.0, (double) stepUp, 0.0), aABB.expandTowards(vec3.x, 0.0, vec3.z), this.level(), list);
            if (vec34.y < (double) stepUp) {
                Vec3 vec35 = collideBoundingBox((Entity) (Object) this, new Vec3(vec3.x, 0.0, vec3.z), aABB.move(vec34), this.level(), list).add(vec34);
                if (vec35.horizontalDistanceSqr() > vec33.horizontalDistanceSqr()) {
                    vec33 = vec35;
                }
            }

            if (vec33.horizontalDistanceSqr() > vec32.horizontalDistanceSqr()) {
                return vec33.add(collideBoundingBox((Entity) (Object) this, new Vec3(0.0, -vec33.y + vec3.y, 0.0), aABB.move(vec33), this.level(), list));
            }
        }

        return vec32;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static Vec3 collideBoundingBox(@Nullable Entity entity, Vec3 vec3, AABB aABB, Level level, List<VoxelShape> list) {
        colliding.set(entity);
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(list.size() + 1);
        if (!list.isEmpty()) {
            builder.addAll(list);
        }

        WorldBorder worldBorder = level.getWorldBorder();
        boolean bl = entity != null && worldBorder.isInsideCloseToBorder(entity, aABB.expandTowards(vec3));
        if (bl) {
            builder.add(worldBorder.getCollisionShape());
        }

        if (entity == null) {
            builder.addAll(level.getBlockCollisions(entity, aABB.expandTowards(vec3)));
        }
        Vec3 res = collideWithShapes(vec3, aABB, builder.build());
        colliding.remove();
        return res;
    }

    private static Vec3 vanillaCollide(Vec3 vec3, AABB aABB, List<VoxelShape> list) {
        if (list.isEmpty()) {
            return vec3;
        } else {
            double d = vec3.x;
            double e = vec3.y;
            double f = vec3.z;
            if (e != 0.0) {
                e = Shapes.collide(Direction.Axis.Y, aABB, list, e);
                if (e != 0.0) {
                    aABB = aABB.move(0.0, e, 0.0);
                }
            }

            boolean bl = Math.abs(d) < Math.abs(f);
            if (bl && f != 0.0) {
                f = Shapes.collide(Direction.Axis.Z, aABB, list, f);
                if (f != 0.0) {
                    aABB = aABB.move(0.0, 0.0, f);
                }
            }

            if (d != 0.0) {
                d = Shapes.collide(Direction.Axis.X, aABB, list, d);
                if (!bl && d != 0.0) {
                    aABB = aABB.move(d, 0.0, 0.0);
                }
            }

            if (!bl && f != 0.0) {
                f = Shapes.collide(Direction.Axis.Z, aABB, list, f);
            }

            return new Vec3(d, e, f);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static Vec3 collideWithShapes(Vec3 vec3, AABB aABB, List<VoxelShape> list) {
        Entity e = colliding.get();
        if (e == null) {
            return vanillaCollide(vec3, aABB, list);
        }

        double x = vec3.x;
        double y = vec3.y;
        double z = vec3.z;

        boolean noX = x == 0;
        boolean noZ = z == 0;
        boolean noXZ = noX && noZ;
        boolean noY = y == 0;
        if (noXZ && noY) return vec3;

        Level lvl = e.level();

        CollisionContext ctx = CollisionContext.of(e);

        if (!noY) {
            y = doYCollision(aABB, y, lvl, ctx, list);
            aABB = aABB.move(0.0, y, 0.0);
        }

        if (noXZ) return new Vec3(x, y, z);

        boolean xFirst = Math.abs(x) > Math.abs(z);

        if (xFirst) {
            x = doXCollision(aABB, x, lvl, ctx, list);
            aABB = aABB.move(x, 0.0, 0.0);

            if (!noZ)
                z = doZCollision(aABB, z, lvl, ctx, list);
        } else {
            z = doZCollision(aABB, z, lvl, ctx, list);
            aABB = aABB.move(0.0, 0.0, z);

            if (!noX)
                x = doXCollision(aABB, x, lvl, ctx, list);
        }

        return new Vec3(
                x, y, z
        );

//        return vec3;
    }

    private static double doZCollision(AABB aABB, double x, Level lvl, CollisionContext ctx, Iterable<VoxelShape> preCheck) {
        Direction.Axis axis = Direction.Axis.Z;
        x = Shapes.collide(
                axis, aABB,
                preCheck, x
        );
        if (x == 0) return x;

        int zSign = (int) Math.signum(x);
        int start = SignedStepper.getStartValue(zSign, aABB.minZ, aABB.maxZ, x, COLLISION_PADDING);
        int end = SignedStepper.getEndValue(zSign, aABB.minZ, aABB.maxZ, x, COLLISION_PADDING);
        int xEnd = (int) Math.ceil(aABB.maxX) + COLLISION_PADDING;
        int yEnd = (int) Math.ceil(aABB.maxY) + COLLISION_PADDING;
        int xStart = (int) Math.floor(aABB.minX) - COLLISION_PADDING;
        int yStart = (int) Math.floor(aABB.minY) - COLLISION_PADDING;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        double delta = x;

        int penn = 0;

        loop:
        for (int i = start; SignedStepper.checkDone(zSign, i, start, end); i += SignedStepper.step(zSign, i)) {
            boolean dPen = false;
            for (int zi = xStart; zi < xEnd; zi++) {
                ChunkAccess chunk = lvl.getChunk(
                        SectionPos.blockToSectionCoord(zi),
                        SectionPos.blockToSectionCoord(i),
                        ChunkStatus.FULL, false
                );
                if (chunk == null) continue;

                for (int yi = yStart; yi < yEnd; yi++) {
                    mutable.set(zi, yi, i);

                    BlockState state = chunk.getBlockState(mutable);
                    if (state.isAir()) continue;

                    VoxelShape sp = state.getCollisionShape(
                            lvl, mutable,
                            ctx
                    );
                    double odelt = delta;
                    delta = Shapes.collide(
                            axis, aABB,
                            Collections.singletonList(
                                    sp.move(
                                            mutable.getX(),
                                            mutable.getY(),
                                            mutable.getZ()
                                    )
                            ), delta
                    );
                    if (!dPen && delta != odelt) {
                        penn += 1;
                        dPen = true;
                    }
                    if (delta == 0) break loop;
                }
            }
            if (!dPen && penn != 0) {
                penn++;
                if (penn > MAX_PENETRATION) break loop;
            }
        }

        return delta;
    }

    private static double doXCollision(AABB aABB, double x, Level lvl, CollisionContext ctx, Iterable<VoxelShape> preCheck) {
        Direction.Axis axis = Direction.Axis.X;
        x = Shapes.collide(
                axis, aABB,
                preCheck, x
        );
        if (x == 0) return x;

        int xSign = (int) Math.signum(x);
        int start = SignedStepper.getStartValue(xSign, aABB.minX, aABB.maxX, x, COLLISION_PADDING);
        int end = SignedStepper.getEndValue(xSign, aABB.minX, aABB.maxX, x, COLLISION_PADDING);
        int zEnd = (int) Math.ceil(aABB.maxZ) + COLLISION_PADDING;
        int yEnd = (int) Math.ceil(aABB.maxY) + COLLISION_PADDING;
        int zStart = (int) Math.floor(aABB.minZ) - COLLISION_PADDING;
        int yStart = (int) Math.floor(aABB.minY) - COLLISION_PADDING;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        double delta = x;

        int penn = 0;

        loop:
        for (int i = start; SignedStepper.checkDone(xSign, i, start, end); i += SignedStepper.step(xSign, i)) {
            boolean dPen = false;
            for (int zi = zStart; zi < zEnd; zi++) {
                ChunkAccess chunk = lvl.getChunk(
                        SectionPos.blockToSectionCoord(i),
                        SectionPos.blockToSectionCoord(zi),
                        ChunkStatus.FULL, false
                );
                if (chunk == null) continue;

                for (int yi = yStart; yi < yEnd; yi++) {
                    mutable.set(i, yi, zi);

                    BlockState state = chunk.getBlockState(mutable);
                    if (state.isAir()) continue;

                    VoxelShape sp = state.getCollisionShape(
                            lvl, mutable,
                            ctx
                    );
                    double odelt = delta;
                    delta = Shapes.collide(
                            axis, aABB,
                            Collections.singletonList(
                                    sp.move(
                                            mutable.getX(),
                                            mutable.getY(),
                                            mutable.getZ()
                                    )
                            ), delta
                    );
                    if (!dPen && delta != odelt) {
                        penn += 1;
                        dPen = true;
                    }
                    if (delta == 0) break loop;
                }
            }
            if (!dPen && penn != 0) {
                penn++;
                if (penn > MAX_PENETRATION) break loop;
            }
        }

        return delta;
    }

    private static double doYCollision(AABB aABB, double x, Level lvl, CollisionContext ctx, Iterable<VoxelShape> preCheck) {
        Direction.Axis axis = Direction.Axis.Y;
        x = Shapes.collide(
                axis, aABB,
                preCheck, x
        );
        if (x == 0) return x;

        int ySign = (int) Math.signum(x);
        int start = SignedStepper.getStartValue(ySign, aABB.minY, aABB.maxY, x, COLLISION_PADDING);
        int end = SignedStepper.getEndValue(ySign, aABB.minY, aABB.maxY, x, COLLISION_PADDING);
        int zEnd = (int) Math.ceil(aABB.maxZ) + COLLISION_PADDING;
        int xEnd = (int) Math.ceil(aABB.maxX) + COLLISION_PADDING;
        int zStart = (int) Math.floor(aABB.minZ) - COLLISION_PADDING;
        int xStart = (int) Math.floor(aABB.minX) - COLLISION_PADDING;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        double delta = x;

        int penn = 0;

        ChunkAccess prev = null;
        long prevIdx = 0;
        boolean pfirst = true;
        loop:
        for (int i = start; SignedStepper.checkDone(ySign, i, start, end); i += SignedStepper.step(ySign, i)) {
            boolean dPen = false;
            for (int zi = zStart; zi < zEnd; zi++) {
                int cz = SectionPos.blockToSectionCoord(zi);
                long czsl = ((long) cz) << 32L;
                for (int xi = xStart; xi < xEnd; xi++) {
                    int cx = SectionPos.blockToSectionCoord(xi);
                    if (prevIdx != (czsl | cx) || pfirst) {
                        prev = lvl.getChunk(
                                cx, cz,
                                ChunkStatus.FULL, false
                        );
                        pfirst = false;
                        if (prev == null) continue;
                    }

                    mutable.set(xi, i, zi);

                    BlockState state = prev.getBlockState(mutable);
                    if (state.isAir()) continue;

                    VoxelShape sp = state.getCollisionShape(
                            lvl, mutable,
                            ctx
                    );
                    double odelt = delta;
                    delta = Shapes.collide(
                            axis, aABB,
                            Collections.singletonList(
                                    sp.move(
                                            mutable.getX(),
                                            mutable.getY(),
                                            mutable.getZ()
                                    )
                            ), delta
                    );
                    if (delta < odelt) continue;
                    if (!dPen && delta != odelt) {
                        penn += 1;
                        dPen = true;
                    }
                    if (delta == 0) break loop;
                }
            }
            if (!dPen && penn != 0) {
                penn++;
                if (penn > MAX_PENETRATION) break loop;
            }
        }

        return delta;
    }
}
