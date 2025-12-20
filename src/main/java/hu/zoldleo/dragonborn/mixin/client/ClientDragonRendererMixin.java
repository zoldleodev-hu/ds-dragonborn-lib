package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer.setDragonMovementData;

@Mixin(ClientDragonRenderer.class)
public class ClientDragonRendererMixin {
    @Inject(method = "renderDragon", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/event/RenderPlayerEvent$Pre;getPartialTick()F"), cancellable = true)
    private static void cancelDragonRender(RenderPlayerEvent.Pre event, CallbackInfo ci, @Local DragonEntity dragon, @Local AbstractClientPlayer player) {
        if (DragonbornUtils.isDragonborn(player)) {
            dragon.renderingWasCancelled = true;
            event.setCanceled(false);
            if (!dragon.isInInventory && (player != Minecraft.getInstance().player || !Minecraft.getInstance().options.getCameraType().isFirstPerson() || !ServerFlightHandler.isGliding(player))) {
                setDragonMovementData(player, Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());
            }
            ci.cancel();
        }
    }

    @Inject(method = "setDragonMovementData", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/attachments/MovementData;set(DDDLnet/minecraft/world/phys/Vec3;)V"), cancellable = true)
    private static void correctYawForDragonborn(Player player, float realtimeDeltaTick, CallbackInfo ci, @Local MovementData movement, @Local Vec3 deltaMovement) {
        if (DragonbornUtils.isDragonborn(player)) {
            movement.set(player.yBodyRot, player.yHeadRot, player.getViewXRot(realtimeDeltaTick), deltaMovement);
            ci.cancel();
        }
    }
}
