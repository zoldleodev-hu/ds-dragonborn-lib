package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonSizeHandler.class)
public abstract class DragonSizeHandlerMixin {
    @Inject(method = "overridePose", at = @At("HEAD"), cancellable = true)
    private static void excludeDragonborn(Player player, CallbackInfoReturnable<Pose> cir) {
        if (DragonbornUtils.isDragonborn(player)) {
            player.setForcedPose(null);
            cir.setReturnValue(player.getPose());
            cir.cancel();
        }
    }

    @Inject(method = "getDragonSize", at = @At("HEAD"), cancellable = true)
    private static void dragonbornSize(EntityEvent.Size event, CallbackInfo ci) {
        if (DragonbornUtils.isDragonborn(event.getEntity())) {
            ci.cancel();
        }
    }

    @Inject(method = "calculateDimensions", at = @At("HEAD"), cancellable = true)
    private static void injectPlayerDim(DragonStateHandler handler, Player player, Pose overridePose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (DragonbornUtils.isDragonborn(player)) {
            cir.setReturnValue(player.getDimensions(overridePose));
            cir.cancel();
        }
    }
}
