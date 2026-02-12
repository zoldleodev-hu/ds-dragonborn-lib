package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.models.DragonModel;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.client.DragonbornClient;
import hu.zoldleo.dragonborn.mixin.DragonStateHandlerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.model.GeoModel;

@Mixin(DragonModel.class)
public abstract class DragonModelMixin extends GeoModel<DragonEntity> {
    @Shadow
    public abstract ResourceLocation getTextureResource(DragonEntity dragon);

    @Inject(method = "getModelResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    private void getDragonbornModel(DragonEntity dragon, CallbackInfoReturnable<ResourceLocation> cir) {
        if (dragon.getPlayer() instanceof AbstractClientPlayer player) {
            DragonStateHandler handler = player.getData(DSDataAttachments.DRAGON_HANDLER);
            if (dragon.getPlayer() instanceof FakeClientPlayer fake)
                handler = fake.handler;
            if (handler.body().is(Dragonborn.DRAGONBORN_BODIES)) {
                ResourceLocation model = handler.getModel().withSuffix("_wide");
                if (handler == DragonEditorScreen.HANDLER && Minecraft.getInstance().player instanceof LocalPlayer local) {
                    player = local;
                }
                PlayerSkin fakeSkin = ((DragonStateHandlerAccessor)handler).dragonborn$getFakeSkin();
                PlayerSkin skin = (fakeSkin == null) ? player.getSkin() : fakeSkin;
                if (skin.model() == PlayerSkin.Model.SLIM)
                    model = handler.getModel().withSuffix("_slim");
                model = model.withPrefix("geo/").withSuffix(".geo.json");
                try {
                    getBakedModel(model);
                    cir.setReturnValue(model);
                    cir.cancel();
                } catch (Exception var4) {
                    DragonSurvival.LOGGER.error("Model not found for dragon species: {}", Translation.Type.DRAGON_SPECIES.wrap(DragonStateProvider.getData(dragon.getPlayer()).speciesKey().location()));
                }
            }
        }
    }
}
