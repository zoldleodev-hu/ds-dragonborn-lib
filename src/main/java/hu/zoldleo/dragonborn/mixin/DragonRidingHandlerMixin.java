package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.server.handlers.DragonRidingHandler;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DragonRidingHandler.class)
public abstract class DragonRidingHandlerMixin {
    @ModifyVariable(method = "playerCanRideDragon", at = @At("STORE"))
    private static boolean dragonIsTooSmallToRide(boolean original, @Local(ordinal = 0, argsOnly = true) Player rider, @Local double scaleRatio) {
        if (DragonbornUtils.isDragonborn(rider))
            return scaleRatio >= 0.8;
        return original;
    }

    @ModifyVariable(method = "onRideAttempt", at = @At("STORE"))
    private static float ridingScaleRatio(float original, @Local Player self) {
        if (DragonbornUtils.isDragonborn(self))
            return 0.8f;
        return original;
    }
}
