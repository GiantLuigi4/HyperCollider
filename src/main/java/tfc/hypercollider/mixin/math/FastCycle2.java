package tfc.hypercollider.mixin.math;

import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = {"net.minecraft.core.AxisCycle$2"})
public class FastCycle2 {
    @Unique
    private static final Direction.Axis[] AXIS_VALUES = new Direction.Axis[3];

    static {
        Direction.Axis[] ordins = Direction.Axis.values();
        for (int i = 0; i < 3; i++) AXIS_VALUES[i] = ordins[Math.floorMod(i + 1, 3)];
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public Direction.Axis cycle(Direction.Axis axis) {
        return AXIS_VALUES[axis.ordinal()];
    }
}
