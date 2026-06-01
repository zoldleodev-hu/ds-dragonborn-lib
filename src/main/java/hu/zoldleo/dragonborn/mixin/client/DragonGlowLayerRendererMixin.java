package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonGlowLayerRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = DragonGlowLayerRenderer.class, remap = false)
public class DragonGlowLayerRendererMixin {
    /*/@ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;FII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;equals(Ljava/lang/Object;)Z"))
    private boolean useCustomSkin(boolean original, @Local(name = "handler") DragonStateHandler handler) {
        return original || handler.getBody().is(Dragonborn.CAN_USE_CUSTOM_SKIN);
    }*/

    @ModifyExpressionValue(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;FII)V", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;getTypeNameLowerCase()Ljava/lang/String;"))
    private String getCustomTypeName(String original) {
        return new ResourceLocation(original).getPath();
    }

    @ModifyConstant(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;FII)V", constant = @Constant(stringValue = "dragonsurvival", ordinal = 0))
    private String getCustomTypeNamespace(String original, @Local(name = "handler") DragonStateHandler handler) {
        ResourceLocation loc = new ResourceLocation(handler.getTypeNameLowerCase());
        return loc.getNamespace().equals("minecraft") ? "dragonsurvival" : loc.getNamespace();
    }
}
