package hu.zoldleo.dragonborn.common;

import by.dragonsurvivalteam.dragonsurvival.client.render.util.AnimationTickTimer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.animation.AbilityAnimation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.animation.AnimationLayer;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.animation.AnimationType;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.compat.create.SkyhookRendererHelper;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.TreasureRestData;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.AnimationUtils;
import by.dragonsurvivalteam.dragonsurvival.util.DragonAnimations;
import com.mojang.datafixers.util.Pair;
import hu.zoldleo.dragonborn.client.DragonbornEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

public class DragonbornEntity extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public volatile Integer playerId;
    public AnimationController<DragonbornEntity> mainAnimController;
    private Pair<AbilityAnimation, AnimationType> currentAbilityAnimation;
    private boolean begunPlayingAbilityAnimation;
    private final AnimationTickTimer animationTickTimer = new AnimationTickTimer();
    private final GeoModel<DragonbornEntity> model = ((DragonbornEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel();

    public DragonbornEntity(EntityType<? extends Entity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        mainAnimController = new AnimationController<>(this, "main", 2, this::predicate);
        controllers.add(mainAnimController);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public @Nullable Player getPlayer() {
        if (this.playerId == null) {
            return null;
        } else {
            Entity entity = this.level().getEntity(this.playerId);
            if (entity instanceof Player) {
                return (Player)entity;
            } else {
                return null;
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }

    private PlayState predicate(final AnimationState<DragonbornEntity> state) {
        Player player = this.getPlayer();
        if (player == null) {
            return PlayState.STOP;
        }

        AnimationController<DragonbornEntity> animationController = state.getController();
        DragonStateHandler handler = DragonStateProvider.getData(player);
        TreasureRestData treasureRest = TreasureRestData.getData(player);
        if (handler.refreshBody) {
            animationController.forceAnimationReset();
        }

        boolean useDynamicScaling = false;
        double animationSpeed = 1.0;
        double speedFactor = ClientConfig.movementAnimationSpeedFactor;
        double baseSpeed = 0.1;
        double smallSizeFactor = ClientConfig.smallSizeAnimationSpeedFactor;
        double bigSizeFactor = ClientConfig.largeSizeAnimationSpeedFactor;
        double distanceFromGround = ServerFlightHandler.distanceFromGround(player);

        Vec3 deltaMovement = player.getDeltaMovement();
        if (this.checkAndPlayAbilityAnimation(state, AnimationLayer.BASE))
            return PlayState.CONTINUE;

        MovementData movement = MovementData.getData(player);
        boolean isSwimming = isConsideredSwimmingForAnimation(player);
        boolean animationWasNullBeforePredicate = animationController.getCurrentAnimation() == null;

        if (!movement.isMovingHorizontally() && handler.isOnMagicSource)
            return state.setAndContinue(DragonAnimations.SIT_ON_MAGIC_SOURCE.getAnimation());

        if (player.isSleeping() || treasureRest.isResting())
            return state.setAndContinue(DragonAnimations.SLEEP.getAnimation());

        if (SkyhookRendererHelper.isPlayerRidingSkyhook(player.getUUID()) && AnimationUtils.doesAnimationExist(model, this, DragonAnimations.CREATE_SKYHOOK_RIDING.getAnimation()))
            return state.setAndContinue(DragonAnimations.CREATE_SKYHOOK_RIDING.getAnimation());

        if (player.isPassenger())
            return state.setAndContinue(DragonAnimations.SIT.getAnimation());

        if (!player.getAbilities().flying && !ServerFlightHandler.isFlying(player)) {
            if (player.getPose() == Pose.SWIMMING) {
                if (ServerFlightHandler.isSpin(player)) {
                    state.setAnimation(DragonAnimations.FLY_SPIN.getAnimation());
                    animationController.transitionLength(2);
                } else {
                    useDynamicScaling = true;
                    baseSpeed = 0.13;
                    state.setAnimation(DragonAnimations.SWIM_FAST.getAnimation());
                    animationController.transitionLength(4);
                }
            } else if (isSwimming) {
                if (ServerFlightHandler.isSpin(player)) {
                    animationSpeed = 2.0;
                    state.setAnimation(DragonAnimations.FLY_SPIN.getAnimation());
                    animationController.transitionLength(2);
                } else {
                    useDynamicScaling = true;
                    baseSpeed = 0.051;
                    state.setAnimation(DragonAnimations.SWIM.getAnimation());
                    animationController.transitionLength(2);
                }
            } else if (AnimationUtils.isAnimationPlaying(animationController, DragonAnimations.FLY_LAND.getAnimation())) {
                state.setAnimation(DragonAnimations.FLY_LAND_END.getAnimation());
                if (!DragonAnimations.FLY_LAND_END.getAnimation().getAnimationStages().isEmpty()) {
                    this.animationTickTimer.putAnimation(((DragonbornEntityRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel(), this, DragonAnimations.FLY_LAND_END.getAnimation());
                }

                animationController.transitionLength(2);
            } else if (!(this.animationTickTimer.getDuration(DragonAnimations.FLY_LAND_END.getAnimation()) > (double) 0.0F)) {
                if (player.onClimbable()) {
                    if (movement.deltaMovement.y() < (double) 0.0F) {
                        state.setAnimation(DragonAnimations.CLIMBING_DOWN.getAnimation());
                    } else {
                        state.setAnimation(DragonAnimations.CLIMBING_UP.getAnimation());
                    }

                    useDynamicScaling = true;
                    baseSpeed = 1.0E-4;
                    animationController.transitionLength(2);
                } else if (DragonEntity.DRAGONS_JUMPING.getOrDefault(this.playerId, false)) {
                    state.resetCurrentAnimation();
                    state.setAnimation(DragonAnimations.JUMP.getAnimation());
                    animationController.transitionLength(2);
                    this.animationTickTimer.putAnimation(((DragonbornEntityRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel(), this, DragonAnimations.JUMP.getAnimation());
                    DragonEntity.DRAGONS_JUMPING.remove(this.playerId);
                } else if (!this.animationTickTimer.isPresent(DragonAnimations.JUMP.getAnimation()) || !(Boolean) DragonEntity.DRAGONS_JUMPING.getOrDefault(this.playerId, true)) {
                    if (!player.onGround()) {
                        state.setAnimation(DragonAnimations.FALL_LOOP.getAnimation());
                        animationController.transitionLength(2);
                    } else if (player.isShiftKeyDown() || !DragonSizeHandler.canPoseFit(player, Pose.STANDING) && DragonSizeHandler.canPoseFit(player, Pose.CROUCHING)) {
                        if (movement.isMovingHorizontally()) {
                            useDynamicScaling = true;
                            baseSpeed = 0.03;
                            state.setAnimation(DragonAnimations.SNEAK_WALK.getAnimation());
                            animationController.transitionLength(5);
                        } else if (movement.dig) {
                            state.setAnimation(DragonAnimations.DIG_SNEAK.getAnimation());
                            animationController.transitionLength(5);
                        } else {
                            state.setAnimation(DragonAnimations.SNEAK.getAnimation());
                            animationController.transitionLength(5);
                        }
                    } else if (player.isSprinting()) {
                        useDynamicScaling = true;
                        baseSpeed = 0.165;
                        state.setAnimation(DragonAnimations.RUN.getAnimation());
                        animationController.transitionLength(4);
                    } else if (movement.isMovingHorizontally()) {
                        useDynamicScaling = true;
                        state.setAnimation(DragonAnimations.WALK.getAnimation());
                        animationController.transitionLength(2);
                    } else if (movement.dig) {
                        state.setAnimation(DragonAnimations.DIG.getAnimation());
                        animationController.transitionLength(6);
                    } else {
                        state.setAnimation(DragonAnimations.IDLE.getAnimation());
                        animationController.transitionLength(2);
                    }
                }
            }
        } else if (ServerFlightHandler.isGliding(player)) {
            if (ServerFlightHandler.isSpin(player)) {
                animationSpeed = 2.0;
                state.setAnimation(DragonAnimations.FLY_SPIN.getAnimation());
                animationController.transitionLength(5);
            } else if (deltaMovement.y < -1.0) {
                state.setAnimation(DragonAnimations.FLY_DIVE_ALT.getAnimation());
                animationController.transitionLength(4);
            } else if (deltaMovement.y < -0.25) {
                state.setAnimation(DragonAnimations.FLY_DIVE.getAnimation());
                animationController.transitionLength(4);
            } else if (deltaMovement.y > 0.5) {
                animationSpeed = 1.5;
                state.setAnimation(DragonAnimations.FLY.getAnimation());
                animationController.transitionLength(2);
            } else {
                state.setAnimation(DragonAnimations.FLY_SOARING.getAnimation());
                animationController.transitionLength(4);
            }
        } else if (movement.desiredMoveVec.y < 0.0 && deltaMovement.y < 0.0 && distanceFromGround < 10.0 && deltaMovement.length() < 4.0) {
            state.setAnimation(DragonAnimations.FLY_LAND.getAnimation());
            animationController.transitionLength(2);
        } else if (ServerFlightHandler.isSpin(player)) {
            state.setAnimation(DragonAnimations.FLY_SPIN.getAnimation());
            animationController.transitionLength(2);
        } else {
            if (movement.desiredMoveVec.y > (double) 0.0F)
                animationSpeed = 2.0;

            state.setAnimation(DragonAnimations.FLY.getAnimation());
            animationController.transitionLength(2);
        }

        if (animationWasNullBeforePredicate)
            animationController.transitionLength(0);

        double finalAnimationSpeed = animationSpeed;
        if (useDynamicScaling) {
            double horizontalDistance = deltaMovement.horizontalDistance();
            double speedComponent = Math.min(ClientConfig.maxAnimationSpeedFactor, (horizontalDistance - baseSpeed) / baseSpeed * speedFactor);
            double sizeDistance = handler.getVisualScale(player, state.getPartialTick()) - 1.0;
            double sizeFactor = sizeDistance >= 0.0 ? bigSizeFactor : smallSizeFactor;
            double sizeComponent = 1.0 / (1.0 + sizeDistance * sizeFactor);
            finalAnimationSpeed = Math.min(ClientConfig.maxAnimationSpeed, Math.max(ClientConfig.minAnimationSpeed, (animationSpeed + speedComponent) * sizeComponent));
        }

        AnimationUtils.setAnimationSpeed(finalAnimationSpeed, state.getAnimationTick(), animationController);

        return PlayState.CONTINUE;
    }

    @Override
    public double getTick(Object obj) {
        return RenderUtil.getCurrentTick();
    }

    private boolean checkAndPlayAbilityAnimation(AnimationState<DragonbornEntity> state, AnimationLayer layer) {
        AnimationLayer currentAbilityLayer = this.currentAbilityAnimation != null ? (this.currentAbilityAnimation.getFirst()).getLayer() : null;
        boolean isNotPlayingCurrentAbilityAnimation = this.currentAbilityAnimation != null && currentAbilityLayer == layer && this.animationTickTimer.getDuration((this.currentAbilityAnimation.getFirst()).getName()) <= 0.0;
        if (!this.begunPlayingAbilityAnimation && isNotPlayingCurrentAbilityAnimation) {
            this.begunPlayingAbilityAnimation = true;
            state.getController().setAnimationSpeed(1.0);
            (this.currentAbilityAnimation.getFirst()).play(state, this.currentAbilityAnimation.getSecond());
            if (this.currentAbilityAnimation.getSecond() == AnimationType.PLAY_ONCE) {
                this.animationTickTimer.putAnimation(((DragonbornEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel(), this, (this.currentAbilityAnimation.getFirst()).getName());
            }
        } else if (this.begunPlayingAbilityAnimation && isNotPlayingCurrentAbilityAnimation && this.currentAbilityAnimation.getSecond() == AnimationType.PLAY_ONCE) {
            this.begunPlayingAbilityAnimation = false;
            this.currentAbilityAnimation = null;
        } else if (this.begunPlayingAbilityAnimation && currentAbilityLayer == layer) {
            state.getController().setAnimationSpeed(1.0);
            return true;
        }

        return this.begunPlayingAbilityAnimation && currentAbilityLayer == layer;
    }

    public static boolean isConsideredSwimmingForAnimation(Player player) {
        boolean isInFluid = player.canSwimInFluidType(player.getInBlockState().getFluidState().getFluidType());
        return isInFluid && !player.isPassenger() && (!player.onGround() || !player.getEyeInFluidType().isAir());
    }
}