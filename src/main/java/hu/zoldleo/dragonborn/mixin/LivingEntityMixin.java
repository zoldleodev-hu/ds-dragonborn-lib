package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyReturnValue(method = "isFallFlying", at = @At("RETURN"))
    private boolean spinOrGlide(boolean original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        return original || (entity instanceof Player player && DragonbornUtils.isDragonborn(player) && (ServerFlightHandler.isSpin(player) || ServerFlightHandler.isGliding(player)));
    }
}
