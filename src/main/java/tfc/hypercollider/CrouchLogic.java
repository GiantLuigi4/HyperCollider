package tfc.hypercollider;

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
import tfc.hypercollider.sweepers.EdgeSweeper;

import java.util.Collections;

public class CrouchLogic {
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
            AABB stepperRegion = stepperBounds.expandTowards(
                    x, 0, z
            );
            VoxelShape stepperShape = Shapes.create(stepperRegion);

            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

            CollisionContext context = CollisionContext.of(entity);

            if (sigX != 0 && sigZ != 0) {
                EdgeSweeper sweeper = new EdgeSweeper(
                        stepperBounds, entity.position().x, entity.position().y, entity.position().z
                );

                double padX = 0.05 * sigX;
                double padZ = 0.05 * sigZ;
                AABB curr = stepperBounds.move(x + padX, 0, z + padZ);
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
                        // TODO: locate exact maximum offset with collision
                        cir.setReturnValue(vec3);
                        return;
                    }

                    x -= sigX;
                    z -= sigZ;
                    if (Math.abs(x) < 1) x = 0;
                    if (Math.abs(z) < 1) z = 0;

                    if (x == 0 && z == 0) break;
                }
            }

            if (sigX != 0 && sigZ == 0) {
                double pad = 0.05 * sigX;
                AABB curr = stepperBounds.move(x + pad, 0, 0);

                boolean noCol = lvl.noCollision(curr);
                if (noCol) {
                    Vec3 dm = Entity.collideBoundingBox(
                            entity,
                            new Vec3(-(x + pad), 0, 0),
                            curr, lvl,
                            Collections.emptyList()
                    );
                    cir.setReturnValue(new Vec3(
                            ((x + pad) + dm.x) - pad,
                            vec3.y,
                            z
                    ));
                    return;
                }

                cir.setReturnValue(new Vec3(x, vec3.y, z));
                return;
            } else if (sigZ != 0 && sigX == 0) {
                double pad = 0.05 * sigZ;
                AABB curr = stepperBounds.move(0, 0, z + pad);

                boolean noCol = lvl.noCollision(curr);
                if (noCol) {
                    Vec3 dm = Entity.collideBoundingBox(
                            entity,
                            new Vec3(0, 0, -(z + pad)),
                            curr, lvl,
                            Collections.emptyList()
                    );
                    cir.setReturnValue(new Vec3(
                            x,
                            vec3.y,
                            ((z + pad) + dm.z) - pad
                    ));
                    return;
                }

                cir.setReturnValue(new Vec3(x, vec3.y, z));
                return;
            }

            cir.setReturnValue(new Vec3(x, vec3.y, z));
        } else {
            cir.setReturnValue(vec3);
        }
    }
}
