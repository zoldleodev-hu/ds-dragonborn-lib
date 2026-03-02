package hu.zoldleo.dragonborn.common;

import by.dragonsurvivalteam.dragonsurvival.client.DragonSurvivalClient;
import by.dragonsurvivalteam.dragonsurvival.client.models.DragonModel;
import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.util.AnimationTickTimer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.animation.AbilityAnimation;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.animation.AnimationLayer;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.animation.AnimationType;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.compat.create.SkyhookRendererHelper;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.TreasureRestData;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.AnimationUtils;
import by.dragonsurvivalteam.dragonsurvival.util.DragonAnimations;
import com.mojang.datafixers.util.Pair;
import hu.zoldleo.dragonborn.client.DragonbornClient;
import hu.zoldleo.dragonborn.client.DragonbornEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DragonbornEntity extends LivingEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public volatile Integer playerId;
    public AnimationController<DragonbornEntity> mainAnimController;
    private Pair<AbilityAnimation, AnimationType> currentAbilityAnimation;
    private boolean begunPlayingAbilityAnimation;
    private final AnimationTickTimer animationTickTimer = new AnimationTickTimer();
    private final GeoModel<DragonbornEntity> model = ((DragonbornEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel();
    public static final ConcurrentHashMap<Integer, Boolean> DRAGONBORN_JUMPING = new ConcurrentHashMap<>();

    public DragonbornEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        mainAnimController = new AnimationController<>(this, "main", 2, this::predicate).triggerableAnim("jump", DragonAnimations.JUMP.getAnimation());
        controllers.add(mainAnimController);
        /*controllers.add(new AnimationController<>(this, "bite", 2, this::bitePredicate));
        controllers.add(new AnimationController<>(this, "breath", 2, this::breathPredicate));
        for(int slot = 0; slot < 4; ++slot) {
            int finalSlot = slot;
            controllers.add(new AnimationController<>(this, "continuous_" + slot, (state) -> this.continousPredicate(state, finalSlot)));
        }*/
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public boolean shouldPlayAnimsWhileGamePaused() {
        return true;
    }

    public @Nullable Player getPlayer() {
        if (this.playerId == null)
            return null;
        Entity entity = level().getEntity(playerId);
        if (entity instanceof Player player)
            return player;
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        Player player = getPlayer();
        return player != null ? player.getArmorSlots() : List.of();
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slotIn) {
        Player player = getPlayer();
        return player != null ? player.getItemBySlot(slotIn) : ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {
        Player player = getPlayer();
        if (player != null)
            player.setItemSlot(slotIn, stack);
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        Player player = getPlayer();
        return player != null ? player.getMainArm() : HumanoidArm.LEFT;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
    }

    private PlayState predicate(final AnimationState<DragonbornEntity> state) {
        Player player = getPlayer();
        if (player == null)
            return PlayState.STOP;
        AnimationController<DragonbornEntity> animationController = state.getController();
        if (player.onGround()) {
            state.setAnimation(DragonAnimations.IDLE.getAnimation());
            animationController.transitionLength(3);
        } else {
            state.setAnimation(RawAnimation.begin().thenLoop("fall_loop"));
            animationController.transitionLength(5);
            System.out.println("fall_loop" + Minecraft.getInstance().level.getGameTime() % 20);
        }



        /*Player player = getPlayer();
        if (player == null)
            return PlayState.STOP;

        AnimationController<DragonbornEntity> animationController = state.getController();
        DragonStateHandler handler = DragonStateProvider.getData(player);
        TreasureRestData treasureRest = TreasureRestData.getData(player);
        if (handler.refreshBody)
            animationController.forceAnimationReset();

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
                if (!DragonAnimations.FLY_LAND_END.getAnimation().getAnimationStages().isEmpty())
                    animationTickTimer.putAnimation(((DragonbornEntityRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel(), this, DragonAnimations.FLY_LAND_END.getAnimation());

                animationController.transitionLength(2);
            } else if (animationTickTimer.getDuration(DragonAnimations.FLY_LAND_END.getAnimation()) <= 0.0) {
                if (player.onClimbable()) {
                    if (movement.deltaMovement.y() < 0.0)
                        state.setAnimation(DragonAnimations.CLIMBING_DOWN.getAnimation());
                    else
                        state.setAnimation(DragonAnimations.CLIMBING_UP.getAnimation());

                    useDynamicScaling = true;
                    baseSpeed = 1.0E-4;
                    animationController.transitionLength(2);
                } else if (DragonEntity.DRAGONS_JUMPING.getOrDefault(playerId, false)) {
                    state.resetCurrentAnimation();
                    state.setAnimation(DragonAnimations.JUMP.getAnimation());
                    animationController.transitionLength(2);
                    animationTickTimer.putAnimation(((DragonbornEntityRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel(), this, DragonAnimations.JUMP.getAnimation());
                    DragonEntity.DRAGONS_JUMPING.remove(playerId);
                } else if (!animationTickTimer.isPresent(DragonAnimations.JUMP.getAnimation()) || !(Boolean) DragonEntity.DRAGONS_JUMPING.getOrDefault(playerId, true)) {
                    if (!player.onGround()) {
                        state.setAnimation(DragonAnimations.FALL_LOOP.getAnimation());
                        animationController.transitionLength(5);
                    } else if (player.isShiftKeyDown() || !DragonSizeHandler.canPoseFit(player, Pose.STANDING) && DragonSizeHandler.canPoseFit(player, Pose.CROUCHING)) {
                        if (movement.isMovingHorizontally()) {
                            useDynamicScaling = true;
                            baseSpeed = 0.03;
                            state.setAnimation(DragonAnimations.SNEAK_WALK.getAnimation());
                            animationController.transitionLength(5);
                        } else if (movement.dig) {
                            state.setAnimation(DragonAnimations.DIG_SNEAK.getAnimation());
                            animationController.transitionLength(3);
                        } else {
                            state.setAnimation(DragonAnimations.SNEAK.getAnimation());
                            animationController.transitionLength(3);
                        }
                    } else if (player.isSprinting()) {
                        useDynamicScaling = true;
                        baseSpeed = 0.165;
                        state.setAnimation(DragonAnimations.RUN.getAnimation());
                        animationController.transitionLength(4);
                    } else if (movement.isMovingHorizontally()) {
                        useDynamicScaling = true;
                        state.setAnimation(DragonAnimations.WALK.getAnimation());
                        animationController.transitionLength(3);
                    } else if (movement.dig) {
                        state.setAnimation(DragonAnimations.DIG.getAnimation());
                        animationController.transitionLength(6);
                    } else {
                        state.setAnimation(DragonAnimations.IDLE.getAnimation());
                        animationController.transitionLength(3);
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
            if (movement.desiredMoveVec.y > 0.0)
                animationSpeed = 2.0;

            state.setAnimation(DragonAnimations.FLY.getAnimation());
            animationController.transitionLength(2);
        }

        if (animationWasNullBeforePredicate)
            animationController.transitionLength(10);
        if (!animationWasNullBeforePredicate && animationController.getCurrentAnimation() == null)
            System.out.println("Current animation is NULL");

        double finalAnimationSpeed = animationSpeed;
        if (useDynamicScaling) {
            double horizontalDistance = deltaMovement.horizontalDistance();
            double speedComponent = Math.min(ClientConfig.maxAnimationSpeedFactor, (horizontalDistance - baseSpeed) / baseSpeed * speedFactor);
            double sizeDistance = handler.getVisualScale(player, state.getPartialTick()) - 1.0;
            double sizeFactor = sizeDistance >= 0.0 ? bigSizeFactor : smallSizeFactor;
            double sizeComponent = 1.0 / (1.0 + sizeDistance * sizeFactor);
            finalAnimationSpeed = Math.min(ClientConfig.maxAnimationSpeed, Math.max(ClientConfig.minAnimationSpeed, (animationSpeed + speedComponent) * sizeComponent));
        }

        AnimationUtils.setAnimationSpeed(finalAnimationSpeed, state.getAnimationTick(), animationController);*/

        return PlayState.CONTINUE;
    }

    @Override
    public double getTick(Object obj) {
        return RenderUtil.getCurrentTick();
    }

    private boolean checkAndPlayAbilityAnimation(AnimationState<DragonbornEntity> state, AnimationLayer layer) {
        AnimationLayer currentAbilityLayer = currentAbilityAnimation != null ? currentAbilityAnimation.getFirst().getLayer() : null;
        boolean isNotPlayingCurrentAbilityAnimation = currentAbilityAnimation != null && currentAbilityLayer == layer && animationTickTimer.getDuration(currentAbilityAnimation.getFirst().getName()) <= 0.0;
        if (!begunPlayingAbilityAnimation && isNotPlayingCurrentAbilityAnimation) {
            begunPlayingAbilityAnimation = true;
            state.getController().setAnimationSpeed(1.0);
            currentAbilityAnimation.getFirst().play(state, currentAbilityAnimation.getSecond());
            if (currentAbilityAnimation.getSecond() == AnimationType.PLAY_ONCE)
                animationTickTimer.putAnimation(((DragonbornEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel(), this, currentAbilityAnimation.getFirst().getName());
        } else if (begunPlayingAbilityAnimation && isNotPlayingCurrentAbilityAnimation && currentAbilityAnimation.getSecond() == AnimationType.PLAY_ONCE) {
            begunPlayingAbilityAnimation = false;
            currentAbilityAnimation = null;
        } else if (begunPlayingAbilityAnimation && currentAbilityLayer == layer) {
            state.getController().setAnimationSpeed(1.0);
            return true;
        }

        return begunPlayingAbilityAnimation && currentAbilityLayer == layer;
    }

    public static boolean isConsideredSwimmingForAnimation(Player player) {
        boolean isInFluid = player.canSwimInFluidType(player.getInBlockState().getFluidState().getFluidType());
        return isInFluid && !player.isPassenger() && (!player.onGround() || !player.getEyeInFluidType().isAir());
    }

    // Where is this used?
    public void setCurrentAbilityAnimation(Pair<AbilityAnimation, AnimationType> _currentAbilityAnimation) {
        if (currentAbilityAnimation != null) {
            AbilityAnimation animation = currentAbilityAnimation.getFirst();
            if (animation != null) {
                animationTickTimer.stopAnimation(animation.getName());
            }
        }

        currentAbilityAnimation = _currentAbilityAnimation;
        begunPlayingAbilityAnimation = false;
    }

    private PlayState breathPredicate(AnimationState<DragonbornEntity> state) {
        Player player = getPlayer();
        if (player == null)
            return PlayState.STOP;
        DragonStateHandler handler = DragonStateProvider.getData(player);
        if (handler.refreshBody)
            state.getController().forceAnimationReset();
        return checkAndPlayAbilityAnimation(state, AnimationLayer.BREATH) ? PlayState.CONTINUE : PlayState.STOP;
    }

    private PlayState playOrContinueAnimation(RawAnimation animation, AnimationState<DragonbornEntity> state, MovementData movement) {
        movement.bite = false;
        if (this.animationTickTimer.getDuration(animation) <= 0.0) {
            this.animationTickTimer.putAnimation(((DragonbornEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel(), this, animation);
        }

        return state.setAndContinue(animation);
    }

    private PlayState bitePredicate(AnimationState<DragonbornEntity> state) {
        Player player = getPlayer();
        if (player == null)
            return PlayState.STOP;

        DragonStateHandler handler = DragonStateProvider.getData(player);
        if (handler.refreshBody)
            state.getController().forceAnimationReset();

        if (this.checkAndPlayAbilityAnimation(state, AnimationLayer.BITE))
            return PlayState.CONTINUE;

        MovementData movement = MovementData.getData(player);
        boolean isUsingItem = player.isUsingItem();
        InteractionHand usedItemHand = player.getUsedItemHand();
        boolean isUsingEdibleItem = isUsingItem && DragonFoodHandler.isEdible(player, player.getItemInHand(usedItemHand));
        if (!ClientDragonRenderer.renderItemsInMouth) {
            if (isUsingEdibleItem) {
                if (usedItemHand == InteractionHand.MAIN_HAND || animationTickTimer.getDuration(DragonAnimations.EAT_ITEM_RIGHT.getAnimation()) > 0.0)
                    return playOrContinueAnimation(DragonAnimations.EAT_ITEM_RIGHT.getAnimation(), state, movement);
                if (usedItemHand == InteractionHand.OFF_HAND || animationTickTimer.getDuration(DragonAnimations.EAT_ITEM_LEFT.getAnimation()) > 0.0)
                    return playOrContinueAnimation(DragonAnimations.EAT_ITEM_LEFT.getAnimation(), state, movement);
            } else if (isUsingItem) {
                if (usedItemHand == InteractionHand.MAIN_HAND && player.getTicksUsingItem() == 1 || animationTickTimer.getDuration(DragonAnimations.USE_ITEM_RIGHT.getAnimation()) > 0.0)
                    return playOrContinueAnimation(DragonAnimations.USE_ITEM_RIGHT.getAnimation(), state, movement);
                if (usedItemHand == InteractionHand.OFF_HAND && player.getTicksUsingItem() == 1 || animationTickTimer.getDuration(DragonAnimations.USE_ITEM_LEFT.getAnimation()) > 0.0)
                    return playOrContinueAnimation(DragonAnimations.USE_ITEM_LEFT.getAnimation(), state, movement);
            } else if (!player.getMainHandItem().isEmpty() && (movement.bite || animationTickTimer.getDuration(DragonAnimations.USE_ITEM_RIGHT.getAnimation()) > 0.0))
                return playOrContinueAnimation(DragonAnimations.USE_ITEM_RIGHT.getAnimation(), state, movement);
        }

        if ((!movement.bite || movement.dig) && animationTickTimer.getDuration(DragonAnimations.BITE.getAnimation()) <= 0.0) {
            return PlayState.STOP;
        }
        return playOrContinueAnimation(DragonAnimations.BITE.getAnimation(), state, movement);
    }

    private PlayState continousPredicate(AnimationState<DragonbornEntity> state, int slot) {
        Player player = getPlayer();
        if (player == null) {
            state.getController().forceAnimationReset();
            return PlayState.STOP;
        }

        DragonStateHandler handler = DragonStateProvider.getData(player);
        if (handler.refreshBody)
            state.getController().forceAnimationReset();

        if (AnimationUtils.doesAnimationExist(((DragonbornEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this)).getGeoModel(), this, "continuous_" + slot)) {
            RawAnimation continuousAnimation = RawAnimation.begin().thenPlay("continuous_" + slot);
            state.setAndContinue(continuousAnimation);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }
}