package hu.zoldleo.dragonborn.common.datadriven.activation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hu.zoldleo.dragonborn.common.codec.ability.ManaCost;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber
public interface Activation {
    Map<ResourceLocation, MapCodec<? extends Activation>> activationRegistry = new HashMap<>();
    Codec<Activation> CODEC = ResourceLocation.CODEC.dispatch("activation_type", Activation::activationType, x -> activationRegistry.get(x).codec());
    boolean init = init();

    static boolean init() {
        activationRegistry.put(PassiveActivation.ACTIVATION_TYPE, PassiveActivation.CODEC);
        activationRegistry.put(SimpleActivation.ACTIVATION_TYPE, SimpleActivation.CODEC);
        activationRegistry.put(ChanneledActivation.ACTIVATION_TYPE, ChanneledActivation.CODEC);
        return true;
    }

    ResourceLocation activationType();

    // TODO :: move away from enum
    Type type();

    MapCodec<? extends Activation> codec();

    default float getInitialManaCost(int level) {
        return 0;
    }

    default Optional<ManaCost> continuousManaCost() {
        return Optional.empty();
    }

    default int getCastTime(int level) {
        return 0;
    }

    default int getCooldown(int level) {
        return 0;
    }

    default Notification notification() {
        return Notification.NONE;
    }

    default boolean canMoveWhileCasting() {
        return true;
    }

    default Optional<Sound> sound() {
        return Optional.empty();
    }

    default Optional<Animations> animations() {
        return Optional.empty();
    }

    /*/default void playStartAndLoopingSound(final Player dragon, DataDrivenDragonAbility instance) {
        sound().flatMap(Sound::start).ifPresent(start -> {
            if (dragon.level().isClientSide()) {
                DragonSurvivalMod.PROXY.playSoundAtEyeLevel(dragon, start);
            } else {
                dragon.level().playSound(dragon, dragon.blockPosition(), start, SoundSource.PLAYERS, 1, 1);
            }
        });

        sound().flatMap(Sound::looping).ifPresent(looping -> {
            if (dragon.level().isClientSide()) {
                instance.queueTickingSound(looping, SoundSource.PLAYERS, dragon);
            } else {
                PacketDistributor.TRACKING_ENTITY.with(() -> dragon).send(new StartTickingSound(dragon.getId(), looping, instance.location().withSuffix(dragon.getStringUUID())));
            }
        });
    }

    default void playChargingSound(final Player dragon, DragonAbilityInstance instance) {
        sound().flatMap(Sound::charging).ifPresent(charging -> {
            if (dragon.level().isClientSide()) {
                instance.queueTickingSound(charging, SoundSource.PLAYERS, dragon);
            } else {
                PacketDistributor.TRACKING_ENTITY.with(() -> dragon).send(new StartTickingSound(dragon.getId(), charging, instance.location().withSuffix(dragon.getStringUUID())));
            }
        });
    }

    default void playEndSound(final Player dragon) {
        sound().flatMap(Sound::end).ifPresent(end -> {
            if (dragon.level().isClientSide()) {
                DragonSurvivalMod.PROXY.playSoundAtEyeLevel(dragon, end);
            } else {
                dragon.level().playSound(dragon, dragon.blockPosition(), end, SoundSource.PLAYERS, 1, 1);
            }
        });
    }

    default void playStartAndChargingAnimation(final Player dragon) {
        animations().flatMap(Animations::startAndCharging).ifPresent(startAndCharging -> {
            if (dragon.level().isClientSide()) {
                AbilityAnimation abilityAnimation = startAndCharging.map(
                        simple -> simple,
                        compound -> compound
                );
                // If it is simple, we just loop. If it is compound, then we ignore the AnimationType anyway, and go from a single play of start into looping charging.
                DragonSurvivalMod.PROXY.setCurrentAbilityAnimation(dragon, new Pair<>(abilityAnimation, AnimationType.LOOPING));
            } else {
                PacketDistributor.TRACKING_ENTITY.with(() -> dragon).send(new SyncAbilityAnimation(dragon.getId(), AnimationType.LOOPING, startAndCharging));
            }
        });
    }

    default void playLoopingAnimation(final Player dragon) {
        animations().flatMap(Animations::looping).ifPresent(looping -> {
            if (dragon.level().isClientSide()) {
                DragonSurvivalMod.PROXY.setCurrentAbilityAnimation(dragon, new Pair<>(looping, AnimationType.LOOPING));
            } else {
                PacketDistributor.sendToPlayersTrackingEntity(dragon, new SyncAbilityAnimation(dragon.getId(), AnimationType.LOOPING, Either.right(looping)));
            }
        });
    }

    default void playEndAnimation(final Player dragon) {
        animations().flatMap(Animations::end).ifPresent(end -> {
            if (dragon.level().isClientSide()) {
                DragonSurvivalMod.PROXY.setCurrentAbilityAnimation(dragon, new Pair<>(end, AnimationType.PLAY_ONCE));
            } else {
                PacketDistributor.TRACKING_ENTITY.with(() -> dragon).send(new SyncAbilityAnimation(dragon.getId(), AnimationType.PLAY_ONCE, Either.right(end)));
            }
        });
    }*/

    enum Type implements StringRepresentable {
        PASSIVE("passive"),
        SIMPLE("simple"),
        CHANNELED("channeled");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;

        Type(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}