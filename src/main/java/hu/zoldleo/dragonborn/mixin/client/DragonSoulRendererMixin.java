package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.blocks.DragonSoulRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DragonSoulBlockEntity;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import hu.zoldleo.dragonborn.mixin.DragonStateHandlerAccessor;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonSoulRenderer.class)
public abstract class DragonSoulRendererMixin {

    @Inject(method = "render(Lby/dragonsurvivalteam/dragonsurvival/server/tileentity/DragonSoulBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At(value = "INVOKE_ASSIGN", target = "Lby/dragonsurvivalteam/dragonsurvival/client/util/FakeClientPlayerUtils;getNextIndex()I"))
    private void renderDragonborn(DragonSoulBlockEntity soul, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci, @Local DragonStateHandler handler) {
        if (DragonbornUtils.isDragonDragonborn(handler)) {
            ResolvableProfile profile = soul.components().get(DataComponents.PROFILE);
            if (profile != null && profile.isResolved()) {
                Minecraft.getInstance().getSkinManager().getOrLoad(profile.gameProfile()).thenAccept(x -> {
                    ((DragonStateHandlerAccessor)handler).dragonborn$setFakeSkin(x);
                    handler.recompileCurrentSkin();
                });
            }
        }
    }
}