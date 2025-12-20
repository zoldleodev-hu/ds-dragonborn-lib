package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Unique
    private boolean dragonborn$tempRenderInFirstPerson;

    @Inject(
        method = "renderLevel",
        at = @At("HEAD")
    )
    public void disableDragon(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightTexture light, Matrix4f frustum, Matrix4f projection, CallbackInfo callback) {
        Entity entity = camera.getEntity();
        if (entity instanceof Player player) {
            DragonStateHandler handler = player.getData(DSDataAttachments.DRAGON_HANDLER);
            if (DragonbornUtils.isDragonborn(handler)) {
                dragonborn$tempRenderInFirstPerson = ClientDragonRenderer.renderInFirstPerson;
                ClientDragonRenderer.renderInFirstPerson = false;
            }
        }
    }

    @Inject(
        method = "renderLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    public void enableDragon(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer renderer, LightTexture light, Matrix4f frustum, Matrix4f projection, CallbackInfo callback) {
        Entity entity = camera.getEntity();
        if (entity instanceof Player player) {
            DragonStateHandler handler = player.getData(DSDataAttachments.DRAGON_HANDLER);
            if (DragonbornUtils.isDragonborn(handler)) {
                ClientDragonRenderer.renderInFirstPerson = dragonborn$tempRenderInFirstPerson;
            }
        }
    }
}
