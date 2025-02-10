package tfc.hypercollider.mixin.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfc.hypercollider.util.game.CrouchLogic;

@Mixin(Player.class)
public abstract class QuickCrouch {
    @Shadow
    @Final
    private Abilities abilities;

    @Shadow
    protected abstract boolean isStayingOnGroundSurface();

    @Shadow
    protected abstract boolean isAboveGround();

    @Inject(at = @At("HEAD"), method = "maybeBackOffFromEdge", cancellable = true)
    public void maybeBackOffFromEdge(Vec3 vec3, MoverType moverType, CallbackInfoReturnable<Vec3> cir) {
        CrouchLogic.handle(
                abilities,
                ((Entity) (Object) this),
                vec3, moverType, cir,
                isStayingOnGroundSurface(),
                isAboveGround()
        );
    }
}
