package hu.zoldleo.dragonborn.client;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.DragonSurvivalClient;
import by.dragonsurvivalteam.dragonsurvival.client.models.DragonModel;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.common.DragonbornEntity;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import java.util.Objects;

public class DragonbornModel extends GeoModel<DragonbornEntity> {
    public static final ResourceLocation DEFAULT_MODEL = DragonSurvival.res("empty");

    @Override
    public ResourceLocation getModelResource(DragonbornEntity entity) {
        ResourceLocation model = DEFAULT_MODEL;
        if (entity.getPlayer() instanceof AbstractClientPlayer player) {
            DragonStateHandler handler = player.getData(DSDataAttachments.DRAGON_HANDLER);
            model = handler.getModel();
            if (handler.body().is(Dragonborn.DRAGONBORN_BODIES))
                model = model.withSuffix("_extras");
        }

        model = model.withPrefix("geo/").withSuffix(".geo.json");

        try {
            this.getBakedModel(model);
            return model;
        } catch (Exception var4) {
            DragonSurvival.LOGGER.error("Model not found for dragon species: {}", Translation.Type.DRAGON_SPECIES.wrap(DragonStateProvider.getData(Objects.requireNonNull(entity.getPlayer())).speciesKey().location()));
            return DEFAULT_MODEL;
        }
    }

    @Override
    public ResourceLocation getTextureResource(DragonbornEntity entity) {
        if (entity.getPlayer() instanceof AbstractClientPlayer)
            return DragonSurvivalClient.DRAGON_MODEL.getTextureResource(ClientDragonRenderer.getOrCreateDragon(entity.getPlayer()));
        return DragonSurvival.res("textures/dragon/dragonborn/blank_skin.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DragonbornEntity animatable) {
        return DragonModel.getAnimationResource(animatable.getPlayer());
    }
}