package hu.zoldleo.dragonborn.client;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.client.DragonSurvivalClient;
import by.dragonsurvivalteam.dragonsurvival.client.models.DragonModel;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.util.AnimationUtils;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.common.DragonbornEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.loading.math.MathParser;
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

    @Override
    public void applyMolangQueries(AnimationState<DragonbornEntity> animationState, double currentTick) {
        super.applyMolangQueries(animationState, currentTick);
        DragonbornEntity dragonborn = animationState.getAnimatable();
        Player player = dragonborn.getPlayer();
        if (player != null) {
            DragonEntity dragon = ClientDragonRenderer.getOrCreateDragon(player);
            applyDSQueries(dragon, player);
            System.out.println(dragon.currentBodyYawChange);
        }
    }

    private void applyDSQueries(DragonEntity dragon, Player player) {
        MovementData movement = MovementData.getData(player);
        float deltaTick = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
        float partialDeltaTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        if (dragon.neckLocked) {
            MathParser.setVariable("query.head_yaw", () -> (double)0.0F);
            MathParser.setVariable("query.head_pitch", () -> (double)0.0F);
        } else {
            MathParser.setVariable("query.head_yaw", () -> movement.headYaw);
            MathParser.setVariable("query.head_pitch", () -> movement.headPitch);
        }

        double gravity = player.getAttributeValue(Attributes.GRAVITY);
        MathParser.setVariable("query.gravity", () -> gravity);
        double bodyYawAvg;
        double headYawAvg;
        double headPitchAvg;
        double verticalVelocityAvg;
        if (!dragon.isInInventory) {
            double bodyYawChange = Functions.angleDifference(movement.bodyYaw, movement.bodyYawLastFrame) / (double)deltaTick * 0.2;
            double headYawChange = Functions.angleDifference(movement.headYaw, movement.headYawLastFrame) / (double)deltaTick * 0.2;
            double headPitchChange = Functions.angleDifference(movement.headPitch, movement.headPitchLastFrame) / (double)deltaTick * 0.2;
            double verticalVelocity = Mth.lerp(partialDeltaTick, movement.deltaMovementLastFrame.y, movement.deltaMovement.y) * 10.0;
            verticalVelocity *= 1.0F - Mth.abs(Mth.clampedMap(movement.prevXRot, -90.0F, 90.0F, -1.0F, 1.0F));
            float deltaTickFor60FPS = AnimationUtils.getDeltaTickFor60FPS();
            int removeSize = (int)(10.0F / deltaTickFor60FPS);
            if (dragon.clearVerticalVelocity) {
                dragon.verticalVelocityHistory.clear();

                while(dragon.verticalVelocityHistory.size() < removeSize) {
                    dragon.verticalVelocityHistory.add(0.0);
                }
            }

            boolean removedElement;
            do {
                removedElement = false;
                if (dragon.bodyYawHistory.size() > removeSize) {
                    dragon.bodyYawHistory.removeFirst();
                    removedElement = true;
                }

                if (dragon.headYawHistory.size() > removeSize) {
                    dragon.headYawHistory.removeFirst();
                    removedElement = true;
                }

                if (dragon.headPitchHistory.size() > removeSize) {
                    dragon.headPitchHistory.removeFirst();
                    removedElement = true;
                }

                if (dragon.verticalVelocityHistory.size() > removeSize) {
                    dragon.verticalVelocityHistory.removeFirst();
                    removedElement = true;
                }
            } while(removedElement);

            dragon.bodyYawHistory.add(bodyYawChange);
            dragon.headYawHistory.add(headYawChange);
            dragon.headPitchHistory.add(headPitchChange);
            dragon.verticalVelocityHistory.add(verticalVelocity);
            bodyYawAvg = dragon.bodyYawHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            headYawAvg = dragon.headYawHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            headPitchAvg = dragon.headPitchHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            verticalVelocityAvg = dragon.verticalVelocityHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        } else {
            bodyYawAvg = 0.0;
            headYawAvg = 0.0;
            headPitchAvg = 0.0;
            verticalVelocityAvg = 0.0;
        }

        bodyYawAvg = Double.isNaN(bodyYawAvg) ? 0.0 : bodyYawAvg;
        headYawAvg = Double.isNaN(headYawAvg) ? 0.0 : headYawAvg;
        headPitchAvg = Double.isNaN(headPitchAvg) ? 0.0 : headPitchAvg;
        verticalVelocityAvg = Double.isNaN(verticalVelocityAvg) ? 0.0 : verticalVelocityAvg;
        double lerpRate = Math.min(1.0F, deltaTick);
        dragon.currentBodyYawChange = Mth.lerp(lerpRate, dragon.currentBodyYawChange, bodyYawAvg);
        dragon.currentHeadYawChange = Mth.lerp(lerpRate, dragon.currentHeadYawChange, headYawAvg);
        dragon.currentHeadPitchChange = Mth.lerp(lerpRate, dragon.currentHeadPitchChange, headPitchAvg);
        if (dragon.clearVerticalVelocity) {
            dragon.currentTailMotionUp = 0.0;
            dragon.clearVerticalVelocity = false;
        } else {
            dragon.currentTailMotionUp = Mth.lerp(lerpRate, dragon.currentTailMotionUp, -verticalVelocityAvg);
        }

        if (dragon.tailLocked) {
            MathParser.setVariable("query.tail_motion_up", () -> 0.0);
            MathParser.setVariable("query.body_yaw_change", () -> 0.0);
        } else {
            MathParser.setVariable("query.body_yaw_change", () -> -dragon.currentBodyYawChange);
            MathParser.setVariable("query.tail_motion_up", () -> dragon.currentTailMotionUp);
        }

        MathParser.setVariable("query.head_yaw_change", () -> dragon.currentHeadYawChange);
        MathParser.setVariable("query.head_pitch_change", () -> dragon.currentHeadPitchChange);
    }
}