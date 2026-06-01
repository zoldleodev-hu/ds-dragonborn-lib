package hu.zoldleo.dragonborn.mixin;

/*/import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerFlightHandler.class)*/
public class ServerFlightHandlerMixin {
    /*/@Inject(method = "handleLanding(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    private static void setLanded(Player player, CallbackInfo ci) {
        ((PlayerAccessor)player).landed(true);
    }

    @Unique
    @SubscribeEvent(receiveCanceled = true) // Unsure if this is needed
    private static void dragonborn$handleLanding(final LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            ((PlayerAccessor)player).landed(true);
        }
    }

    @Unique
    @SubscribeEvent
    private static void dragonborn$handleLanding(final PlayerFlyableFallEvent event) {
        ((PlayerAccessor)event.getEntity()).landed(true);
    }*/
}