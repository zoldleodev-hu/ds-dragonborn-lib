package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerFlightHandler.class)
public class ServerFlightHandlerMixin {
    @Inject(method = "handleLanding(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    private static void setLanded(Player player, CallbackInfo ci) {
        ((PlayerAccessor)player).landed(true);
    }
}