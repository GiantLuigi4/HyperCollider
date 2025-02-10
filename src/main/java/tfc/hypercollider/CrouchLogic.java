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

import java.util.ArrayList;
import java.util.Collections;

public class CrouchLogic {
    public static void handle(Abilities abilities, Entity entity, Vec3 vec3, MoverType moverType, CallbackInfoReturnable<Vec3> cir, boolean stayingOnGroundSurface, boolean aboveGround) {
        if (!abilities.flying && vec3.y <= 0.0 && (moverType == MoverType.SELF || moverType == MoverType.PLAYER) && stayingOnGroundSurface && aboveGround) {
            double x = vec3.x;
            double z = vec3.z;
//            z = 0;
            x = 0;

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
            } else if (sigZ != 0) {
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
