package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientMagicHUDHandler.class, remap = false)
public class MagicHUDMixin {
    @ModifyExpressionValue(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;isDragon()Z"))
    private static boolean dontRenderOnHorse(boolean original, @Local(name = "handler") DragonStateHandler handler, @Local(name = "localPlayer") Player localPlayer) {
        return original && !(DragonbornUtils.isDragonborn(handler) && localPlayer.getControlledVehicle() instanceof PlayerRideableJumping playerrideablejumping && playerrideablejumping.canJump());
    }
}