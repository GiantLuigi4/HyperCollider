package tfc.hypercollider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class CrouchLogic {
    public static void handle(Abilities abilities, Entity entity, Vec3 vec3, MoverType moverType, CallbackInfoReturnable<Vec3> cir, boolean stayingOnGroundSurface, boolean aboveGround) {
        if (!abilities.flying && vec3.y <= 0.0 && (moverType == MoverType.SELF || moverType == MoverType.PLAYER) && stayingOnGroundSurface && aboveGround) {
            double x = vec3.x;
            double z = vec3.z;
            z = 0;

            int sigX = (int) Math.signum(x);
            int sigZ = (int) Math.signum(z);

            Level lvl = entity.level();

            float sUp = entity.maxUpStep();
            int stepRegion = (int) Math.ceil(sUp);

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

            int minY = (int) Math.floor(stepperRegion.minY) - 1;
            int maxY = (int) Math.ceil(stepperRegion.maxY) + 1;

//            System.out.println(x);

            if (sigX != 0 && sigZ == 0) {
                int minZ = (int) Math.floor(stepperBounds.minZ) - 1;
                int maxZ = (int) Math.ceil(stepperBounds.maxZ) + 1;

                double boundX = sigX > 0 ?
                        eBounds.minX :
                        eBounds.maxX;
                double oBoundX = -Math.ceil((sigX > 0 ?
                        eBounds.maxX :
                        eBounds.minX) - boundX) - sigX;

                double px = x + sigX;
                double xm = -x;
                boolean foundEdge = false;

                while (true) {
                    for (int zi = minZ; zi <= maxZ; zi++) {
                        for (int yi = minY; yi <= maxY; yi++) {
                            int xi = (int) (boundX + px);
                            mutable.set(
                                    xi, yi, zi
                            );

                            BlockState state = lvl.getBlockState(mutable);
                            if (!state.isAir()) {
                                VoxelShape sp = state.getCollisionShape(
                                        lvl, mutable, context
                                );

                                double mv = sp.max(Direction.Axis.X);
                                if (sigX > 0) {
                                    double crd = (xi - 1) + mv;
                                    double oset = 0.05;
                                    crd -= oset; // ensure overlap
                                    if (Math.ceil(px) + boundX > crd) {
                                        double dv = crd - (boundX - (1.0 - oset));
                                        if (dv > xm) {
                                            xm = dv;
                                        }
                                        if (xm > x) xm = x;
                                        foundEdge = true;
                                    }
                                }
                            }
                        }
                    }

                    px -= sigX;

                    if (foundEdge) {
                        x = xm;
                    }

                    if (sigX > 0) {
                        if (px < oBoundX) {
                            break;
                        }
                    } else {
                        if (px > oBoundX) {
                            break;
                        }
                    }
                }
            }

            cir.setReturnValue(new Vec3(x, vec3.y, z));
        } else {
            cir.setReturnValue(vec3);
        }
    }
}
