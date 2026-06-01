package hu.zoldleo.dragonborn.common.datadriven.activation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public record Sound(Optional<SoundEvent> start, Optional<SoundEvent> charging, Optional<SoundEvent> looping,
                    Optional<SoundEvent> end) {
    public static final Codec<Sound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeRegistries.SOUND_EVENTS.getCodec().optionalFieldOf("start").forGetter(Sound::start),
            ForgeRegistries.SOUND_EVENTS.getCodec().optionalFieldOf("charging").forGetter(Sound::charging),
            ForgeRegistries.SOUND_EVENTS.getCodec().optionalFieldOf("looping").forGetter(Sound::looping),
            ForgeRegistries.SOUND_EVENTS.getCodec().optionalFieldOf("end").forGetter(Sound::end)
    ).apply(instance, Sound::new));

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private SoundEvent start;
        private SoundEvent charging;
        private SoundEvent looping;
        private SoundEvent end;

        public Builder start(final SoundEvent start) {
            this.start = start;
            return this;
        }

        public Builder charging(final SoundEvent charging) {
            this.charging = charging;
            return this;
        }

        public Builder looping(final SoundEvent looping) {
            this.looping = looping;
            return this;
        }

        public Builder end(final SoundEvent end) {
            this.end = end;
            return this;
        }

        public Sound build() {
            return new Sound(Optional.ofNullable(start), Optional.ofNullable(charging), Optional.ofNullable(looping), Optional.ofNullable(end));
        }

        public Optional<Sound> optional() {
            return Optional.of(build());
        }
    }
}