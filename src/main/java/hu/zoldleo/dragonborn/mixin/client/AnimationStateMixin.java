package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.util.DragonAnimations;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;
import java.util.Queue;

@Mixin(AnimationController.class)
public abstract class AnimationStateMixin<T extends GeoAnimatable> {
    @Shadow
    protected Queue<AnimationProcessor.QueuedAnimation> animationQueue;

    @Shadow
    protected GeoModel<T> lastModel;

    @Shadow
    protected boolean needsAnimationReload;

    @Shadow
    protected RawAnimation currentRawAnimation;

    @Shadow
    protected AnimationProcessor.QueuedAnimation currentAnimation;

    @Shadow
    protected boolean shouldResetTick;

    @Shadow
    public abstract @Nullable AnimationProcessor.QueuedAnimation getCurrentAnimation();

    @Unique
    boolean wasFall;

    @Inject(method = "process", at = @At(value = "HEAD"))
    void asd(GeoModel<T> model, AnimationState<T> state, Map<String, GeoBone> bones, Map<String, BoneSnapshot> snapshots, double seekTime, boolean crashWhenCantFindBone, CallbackInfo ci) {
        if (wasFall && currentAnimation == null)
            System.out.println("Hiba");
    }

    @Inject(method = "process", at = @At(value = "TAIL"))
    void sad(GeoModel<T> model, AnimationState<T> state, Map<String, GeoBone> bones, Map<String, BoneSnapshot> snapshots, double seekTime, boolean crashWhenCantFindBone, CallbackInfo ci) {
        if (currentRawAnimation == DragonAnimations.FALL_LOOP.getAnimation() && currentAnimation != null)
            wasFall = true;
    }
}