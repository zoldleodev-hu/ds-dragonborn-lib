package hu.zoldleo.dragonborn.common.datadriven.activation;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.codec.ability.animation.CompoundAbilityAnimation;
import hu.zoldleo.dragonborn.common.codec.ability.animation.SimpleAbilityAnimation;

import java.util.Optional;

public record Animations(
        Optional<Either<CompoundAbilityAnimation, SimpleAbilityAnimation>> startAndCharging,
        Optional<SimpleAbilityAnimation> looping,
        Optional<SimpleAbilityAnimation> end
) {
    public static final Codec<Animations> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.either(CompoundAbilityAnimation.CODEC, SimpleAbilityAnimation.CODEC).optionalFieldOf("start_and_charging").forGetter(Animations::startAndCharging),
            SimpleAbilityAnimation.CODEC.optionalFieldOf("looping").forGetter(Animations::looping),
            SimpleAbilityAnimation.CODEC.optionalFieldOf("end").forGetter(Animations::end)
    ).apply(instance, Animations::new));

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private Either<CompoundAbilityAnimation, SimpleAbilityAnimation> startAndCharging;
        private SimpleAbilityAnimation looping;
        private SimpleAbilityAnimation end;

        public Builder startAndCharging(final CompoundAbilityAnimation startAndCharging) {
            this.startAndCharging = Either.left(startAndCharging);
            return this;
        }

        public Builder startAndCharging(final SimpleAbilityAnimation startAndCharging) {
            this.startAndCharging = Either.right(startAndCharging);
            return this;
        }

        public Builder looping(final SimpleAbilityAnimation looping) {
            this.looping = looping;
            return this;
        }

        public Builder end(final SimpleAbilityAnimation end) {
            this.end = end;
            return this;
        }

        public Animations build() {
            return new Animations(Optional.ofNullable(startAndCharging), Optional.ofNullable(looping), Optional.ofNullable(end));
        }

        public Optional<Animations> optional() {
            return Optional.of(build());
        }
    }
}