package tfc.hypercollider.util.game;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.hypercollider.util.logic.ShapeChecker;
import tfc.hypercollider.util.sweepers.EdgeSweeper;

import java.util.Collections;

public class CrouchLogic {
    //    public static final double PAD_SIZE = 0.01;
//    public static final double PAD_SIZE = 0.0;
    public static final double PAD_SIZE = 0.00001;

    protected static Vec3 checkX(Entity entity, Vec3 vec3, double sigX, Level lvl, double x, double z, AABB stepperBounds) {
        double pad = PAD_SIZE * sigX;
        AABB curr = stepperBounds.move(x + pad, 0, 0);

        boolean noCol = lvl.noCollision(curr);
        if (noCol) {
            Vec3 dm = Entity.collideBoundingBox(
                    entity,
                    new Vec3(-(x + pad), 0, 0),
                    curr, lvl,
                    Collections.emptyList()
            );
            return new Vec3(
                    ((x + pad) + dm.x) - pad,
                    vec3.y,
                    z
            );
        }

        return new Vec3(x, vec3.y, z);
    }

    protected static Vec3 checkZ(Entity entity, Vec3 vec3, double sigZ, Level lvl, double x, double z, AABB stepperBounds) {
        double pad = PAD_SIZE * sigZ;
        AABB curr = stepperBounds.move(0, 0, z + pad);

        boolean noCol = lvl.noCollision(curr);
        if (noCol) {
            Vec3 dm = Entity.collideBoundingBox(
                    entity,
                    new Vec3(0, 0, -(z + pad)),
                    curr, lvl,
                    Collections.emptyList()
            );
            return new Vec3(
                    x,
                    vec3.y,
                    ((z + pad) + dm.z) - pad
            );
        }

        return new Vec3(x, vec3.y, z);
    }

    public static void handle(Abilities abilities, Entity entity, Vec3 vec3, MoverType moverType, CallbackInfoReturnable<Vec3> cir, boolean stayingOnGroundSurface, boolean aboveGround) {
        if (!abilities.flying && vec3.y <= 0.0 && (moverType == MoverType.SELF || moverType == MoverType.PLAYER) && stayingOnGroundSurface && aboveGround) {
            double x = vec3.x;
            double z = vec3.z;

            int sigX = (int) Math.signum(x);
            int sigZ = (int) Math.signum(z);

            if (sigX == 0 && sigZ == 0) {
                cir.setReturnValue(vec3);
                return;
            }

            Level lvl = entity.level();

            float sUp = entity.maxUpStep();

            AABB eBounds = entity.getBoundingBox();

            AABB stepperBounds = new AABB(
                    eBounds.minX,
                    eBounds.minY - sUp,
                    eBounds.minZ,

                    eBounds.maxX,
                    eBounds.minY,
                    eBounds.maxZ
            );

            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

            CollisionContext context = CollisionContext.of(entity);

            if (sigX != 0) {
                vec3 = checkX(entity, vec3, sigX, lvl, x, z, stepperBounds);
                x = vec3.x;
            }

            if (sigZ != 0) {
                vec3 = checkZ(entity, vec3, sigZ, lvl, x, z, stepperBounds);
                z = vec3.z;
                if (sigX == 0) {
                    cir.setReturnValue(new Vec3(
                            x, vec3.y, z
                    ));
                    return;
                }
            } else {
                cir.setReturnValue(new Vec3(
                        x, vec3.y, z
                ));
                return;
            }

            AABB stepperRegion = stepperBounds.expandTowards(
                    x, 0, z
            );
            VoxelShape stepperShape = Shapes.create(stepperRegion);

            sigX = (int) Math.signum(x);
            sigZ = (int) Math.signum(z);

            // TODO: I wonder if there's a better way I can do this?
            // TODO: BUG: if too close to -z edge, +z motion gets canceled out when close to -x edge
            // TODO: BUG: if too close to -z edge, -z motion gets canceled out when close to +x edge
            if (sigX != 0 && sigZ != 0) {
                EdgeSweeper sweeper = new EdgeSweeper(
                        stepperBounds, entity.position().x, entity.position().y, entity.position().z
                );

                double padX = 0.01 * sigX;
                double padZ = 0.01 * sigZ;
                AABB curr = stepperBounds.move(x, 0, z);
                boolean noCol = lvl.noCollision(curr);
                if (!noCol) {
                    cir.setReturnValue(vec3);
                    return;
                }

                while (true) {
                    sigX = (int) Math.signum(x);
                    sigZ = (int) Math.signum(z);
                    sweeper.set(
                            (int) (stepperBounds.minX + x),
                            (int) (stepperBounds.minY),
                            (int) (stepperBounds.minZ + z),
                            sigX, sigZ
                    );

                    boolean noCollide = true;
                    while (sweeper.hasNext()) {
                        sweeper.next(mutable);

                        BlockState state = lvl.getBlockState(mutable);
                        VoxelShape shape = state.getCollisionShape(lvl, mutable, context);
                        if (ShapeChecker.checkCollision(
                                stepperRegion, shape,
                                mutable.getX(), mutable.getY(), mutable.getZ(),
                                stepperShape
                        )) {
                            noCollide = false;
                            break;
                        }
                    }
                    if (!noCollide) {
                        double pad = PAD_SIZE * sigX;
                        curr = stepperBounds.move(x + pad, 0, 0);
                        if (lvl.noCollision(curr)) {
                            Vec3 dm = Entity.collideBoundingBox(
                                    entity,
                                    new Vec3(-(x + pad), 0, 0),
                                    curr, lvl,
                                    Collections.emptyList()
                            );
                            x = ((x + pad) + dm.x) - pad;
                        }

                        pad = PAD_SIZE * sigZ;
                        curr = stepperBounds.move(0, 0, z + pad);
                        if (lvl.noCollision(curr)) {
                            Vec3 dm = Entity.collideBoundingBox(
                                    entity,
                                    new Vec3(0, 0, -(z + pad)),
                                    curr, lvl,
                                    Collections.emptyList()
                            );
                            z = ((z + pad) + dm.z) - pad;
                        }

                        cir.setReturnValue(new Vec3(x, vec3.y, z));
                        return;
                    }

                    if (Math.abs(x) < 1)
                        x -= sigX * 0.05;
                    else
                        x -= sigX * 0.5;
                    if (Math.abs(z) < 1)
                        z -= sigZ * 0.05;
                    else
                        z -= sigZ * 0.5;
                    if (Math.abs(x) <= 0.05) x = 0;
                    if (Math.abs(z) <= 0.05) z = 0;

                    if (x == 0 && z == 0)
                        break;
                }
            }

            // TODO: perform a proper check
            // basically, move the bounding box ahead of time
            // check collide back on whatever axis is non-zero
//            sigX = (int) Math.signum(x);
//            sigZ = (int) Math.signum(z);
//
//            if (sigX != 0) {
//                vec3 = checkX(entity, vec3, sigX, lvl, x, z, stepperBounds);
//                x = vec3.x;
//            }
//
//            if (sigZ != 0) {
//                vec3 = checkZ(entity, vec3, sigZ, lvl, x, z, stepperBounds);
//                z = vec3.z;
//            }

            cir.setReturnValue(new Vec3(x, vec3.y, z));
        } else {
            cir.setReturnValue(vec3);
        }
    }
}
