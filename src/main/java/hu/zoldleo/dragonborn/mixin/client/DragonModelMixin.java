package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.models.DragonModel;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.mixin.DragonStateHandlerAccessor;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.model.GeoModel;

@Mixin(DragonModel.class)
public abstract class DragonModelMixin extends GeoModel<DragonEntity> {
    @Inject(method = "getModelResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getDragonbornModel(DragonEntity dragon, CallbackInfoReturnable<ResourceLocation> cir) {
        if (dragon.getPlayer() instanceof AbstractClientPlayer player) {
            ResourceLocation model = null;
            if (player instanceof FakeClientPlayer fake && DragonbornUtils.isDragonborn(fake.handler))
                model = dragonborn$getFakeDragonbornModel(fake.handler, player);
            else {
                DragonStateHandler handler = player.getData(DSDataAttachments.DRAGON_HANDLER);
                if (DragonbornUtils.isDragonborn(handler))
                    model = dragonborn$getDragonbornModel(handler);
            }
            if (model != null) {
                try {
                    getBakedModel(model);
                    cir.setReturnValue(model);
                    cir.cancel();
                } catch (Exception var4) {
                    DragonSurvival.LOGGER.error("Model not found for dragon species: {}", Translation.Type.DRAGON_SPECIES.wrap(player.getData(DSDataAttachments.DRAGON_HANDLER).speciesKey().location()));
                }
            }
        }
    }

    @Unique
    private ResourceLocation dragonborn$getFakeDragonbornModel(DragonStateHandler handler, AbstractClientPlayer player) {
        ResourceLocation model = handler.getModel().withSuffix("_wide");
        if (handler == DragonEditorScreen.HANDLER && Minecraft.getInstance().player instanceof LocalPlayer local) {
            player = local;
        }
        PlayerSkin fakeSkin = ((DragonStateHandlerAccessor)handler).dragonborn$getFakeSkin();
        PlayerSkin skin = (fakeSkin == null) ? player.getSkin() : fakeSkin;
        if (skin.model() == PlayerSkin.Model.SLIM)
            model = handler.getModel().withSuffix("_slim");
        return model.withPrefix("geo/").withSuffix(".geo.json");
    }

    @Unique
    private ResourceLocation dragonborn$getDragonbornModel(DragonStateHandler handler) {
        return handler.getModel().withPrefix("geo/").withSuffix("_extras.geo.json");
    }

    @ModifyExpressionValue(method = "getTextureResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;equals(Ljava/lang/Object;)Z"))
    private boolean useCustomSkin(boolean original, @Local DragonStateHandler handler) {
        return original || handler.body().is(Dragonborn.CAN_USE_CUSTOM_SKIN);
    }
}
