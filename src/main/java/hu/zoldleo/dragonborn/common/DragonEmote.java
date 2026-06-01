package hu.zoldleo.dragonborn.common;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record DragonEmote(
        String animationKey,
        Optional<String> translationOverride,
        double speed,
        int duration,
        boolean loops,
        boolean blend,
        boolean locksHead,
        boolean locksTail,
        boolean thirdPerson,
        boolean canMove,
        Optional<Sound> sound
) {
    public static final double DEFAULT_SPEED = 1;
    public static final int NO_DURATION = -1;

    public static final Codec<DragonEmote> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("animation_key").forGetter(DragonEmote::animationKey),
            Codec.STRING.optionalFieldOf("translation_override").forGetter(DragonEmote::translationOverride),
            Codec.DOUBLE.optionalFieldOf("speed", DEFAULT_SPEED).forGetter(DragonEmote::speed),
            Codec.INT.optionalFieldOf("duration", NO_DURATION).forGetter(DragonEmote::duration),
            Codec.BOOL.optionalFieldOf("loops", false).forGetter(DragonEmote::loops),
            Codec.BOOL.optionalFieldOf("blend", false).forGetter(DragonEmote::blend),
            Codec.BOOL.optionalFieldOf("locks_head", false).forGetter(DragonEmote::locksHead),
            Codec.BOOL.optionalFieldOf("locks_tail", false).forGetter(DragonEmote::locksTail),
            Codec.BOOL.optionalFieldOf("third_person", false).forGetter(DragonEmote::thirdPerson),
            Codec.BOOL.optionalFieldOf("can_move", false).forGetter(DragonEmote::canMove),
            Sound.CODEC.optionalFieldOf("sound").forGetter(DragonEmote::sound)
    ).apply(instance, DragonEmote::new));

    public record Sound(SoundEvent soundEvent, float volume, float pitch, int interval) {
        public static final float DEFAULT_VOLUME = 1;
        public static final float DEFAULT_PITCH = 1;

        public static final Codec<Sound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("sound_event").forGetter(Sound::soundEvent),
                Codec.FLOAT.optionalFieldOf("volume", DEFAULT_VOLUME).forGetter(Sound::volume),
                Codec.FLOAT.optionalFieldOf("pitch", DEFAULT_PITCH).forGetter(Sound::pitch),
                Codec.INT.fieldOf("interval").forGetter(Sound::interval)
        ).apply(instance, Sound::new));

        public void playSound(Player player) {
            player.level().playLocalSound(player.position().x, player.position().y, player.position().z, soundEvent, SoundSource.PLAYERS, volume, pitch, false);
        }
    }

    public Component name() {
        return Component.translatable("dragon_emote." + DragonSurvivalMod.MODID + "." + translationOverride.orElse(animationKey));
    }

    public String key() {
        return translationOverride.orElse(animationKey);
    }

    public static class Builder {
        private String animationKey;
        private String translationOverride;
        private double speed = DEFAULT_SPEED;
        private int duration = NO_DURATION;
        private Sound sound;

        private boolean loops;
        private boolean blend;
        private boolean locksHead;
        private boolean locksTail;
        private boolean thirdPerson;
        private boolean canMove;

        public static Builder of(final String animationKey) {
            return of(animationKey, null);
        }

        public static Builder of(final String animationKey, final String translationOverride) {
            Builder builder = new Builder();
            builder.animationKey = animationKey;
            builder.translationOverride = translationOverride;
            return builder;
        }

        public Builder speed(double speed) {
            this.speed = speed;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder sound(final SoundEvent sound, int interval) {
            return sound(sound, interval, Sound.DEFAULT_VOLUME, Sound.DEFAULT_PITCH);
        }

        public Builder sound(final SoundEvent sound, int interval, float volume, float pitch) {
            this.sound = new Sound(sound, volume, pitch, interval);
            return this;
        }

        public Builder loops() {
            this.loops = true;
            return this;
        }

        public Builder blend() {
            this.blend = true;
            return this;
        }

        public Builder locksHead() {
            this.locksHead = true;
            return this;
        }

        public Builder locksTail() {
            this.locksTail = true;
            return this;
        }

        public Builder thirdPerson() {
            this.thirdPerson = true;
            return this;
        }

        public Builder canMove() {
            this.canMove = true;
            return this;
        }

        public DragonEmote build() {
            return new DragonEmote(animationKey, Optional.ofNullable(translationOverride), speed, duration, loops, blend, locksHead, locksTail, thirdPerson, canMove, Optional.ofNullable(sound));
        }
    }
}