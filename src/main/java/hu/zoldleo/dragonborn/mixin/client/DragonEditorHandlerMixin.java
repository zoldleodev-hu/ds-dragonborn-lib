package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.SkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonPart;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.LayerSettings;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import hu.zoldleo.dragonborn.mixin.DragonStateHandlerAccessor;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(DragonEditorHandler.class)
public class DragonEditorHandlerMixin {
    @Shadow
    private static ShaderInstance skinGenerationShader;
    @Unique
    private static ShaderInstance dragonborn$skinGenerationMaskShader;

    @Inject(method = "generateSkinTextures", at = @At(value = "INVOKE_ASSIGN", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;getCurrentStageCustomization()Lby/dragonsurvivalteam/dragonsurvival/client/skin_editor_system/objects/DragonStageCustomization;"))
    private static void addPlayerTexture(DragonEntity dragon, CallbackInfo ci, @Local(ordinal = 0) RenderTarget normalTarget, @Local(ordinal = 1) int viewportX, @Local(ordinal = 2) int viewportY, @Local(ordinal = 3) int viewportW, @Local(ordinal = 4) int viewportH) {
        if (dragon.getPlayer() instanceof AbstractClientPlayer player) {
            DragonStateHandler handler = player.getData(DSDataAttachments.DRAGON_HANDLER);
            if (dragon.getPlayer() instanceof FakeClientPlayer fake) {
                handler = fake.handler;
            }
            if (DragonbornUtils.isDragonborn(handler)) {
                PlayerSkin fakeSkin = ((DragonStateHandlerAccessor)(handler)).dragonborn$getFakeSkin();
                PlayerSkin skin = (fakeSkin == null) ? player.getSkin() : fakeSkin;
                AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(skin.texture());

                if (handler == DragonEditorScreen.HANDLER && Minecraft.getInstance().player instanceof LocalPlayer local)
                    texture = Minecraft.getInstance().getTextureManager().getTexture(local.getSkin().texture());

                normalTarget.bindWrite(true);

                RenderSystem.enableBlend();
                RenderSystem.colorMask(true, true, true, true);
                RenderSystem.blendEquation(32774);
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                skinGenerationShader.setSampler("SkinTexture", texture);
                skinGenerationShader.getUniform("HueVal").set(0f);
                skinGenerationShader.getUniform("SatVal").set(0f);
                skinGenerationShader.getUniform("BrightVal").set(0f);
                skinGenerationShader.getUniform("Colorable").set(0f);
                skinGenerationShader.getUniform("Glowing").set(0f);
                skinGenerationShader.apply();
                BufferBuilder bufferbuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
                bufferbuilder.addVertex(0.0F, 0.0F, 0.0F);
                bufferbuilder.addVertex(1.0F, 0.0F, 0.0F);
                bufferbuilder.addVertex(1.0F, 1.0F, 0.0F);
                bufferbuilder.addVertex(0.0F, 1.0F, 0.0F);
                GlStateManager._viewport(0, 448, 64, 64);
                BufferUploader.draw(bufferbuilder.buildOrThrow());
                GlStateManager._viewport(viewportX, viewportY, viewportW, viewportH);

                skinGenerationShader.clear();
                normalTarget.unbindWrite();
            }
        }
    }

    @Inject(method = "generateSkinTextures", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;renderThreadTesselator()Lcom/mojang/blaze3d/vertex/Tesselator;", ordinal = 0))
    private static void applyMask(DragonEntity dragon, CallbackInfo ci, @Local AbstractTexture texture, @Local(ordinal = 0) float hueVal, @Local(ordinal = 1) float satVal, @Local(ordinal = 2) float brightVal, @Local(ordinal = 0) DragonPart skinTexture, @Local LayerSettings settings, @Local SkinLayer layer, @Local DragonStateHandler handler) {
        ResourceLocation maskLocation = DragonSurvival.res("textures/dragon/custom/masks/" + handler.body().value().model().getPath() + '/' + layer.getNameLowerCase() + "_mask.png");
        if (Minecraft.getInstance().getResourceManager().getResource(maskLocation).isEmpty())
            return;
        AbstractTexture mask = Minecraft.getInstance().getTextureManager().getTexture(maskLocation);
        dragonborn$skinGenerationMaskShader.setSampler("SkinTexture", texture);
        dragonborn$skinGenerationMaskShader.setSampler("SkinTextureMask", mask);
        dragonborn$skinGenerationMaskShader.getUniform("HueVal").set(hueVal);
        dragonborn$skinGenerationMaskShader.getUniform("SatVal").set(satVal);
        dragonborn$skinGenerationMaskShader.getUniform("BrightVal").set(brightVal);
        dragonborn$skinGenerationMaskShader.getUniform("Colorable").set(skinTexture.isColorable() ? 1.0F : 0.0F);
        dragonborn$skinGenerationMaskShader.getUniform("Glowing").set(settings.isGlowing ? 1.0F : 0.0F);
        dragonborn$skinGenerationMaskShader.apply();
    }

    @Inject(method = "registerShaders", at = @At("TAIL"))
    private static void registerMaskShader(RegisterShadersEvent event, CallbackInfo ci) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), DragonSurvival.res("skin_generation_mask"), DefaultVertexFormat.BLIT_SCREEN), (instance) -> dragonborn$skinGenerationMaskShader = instance);
    }
}
