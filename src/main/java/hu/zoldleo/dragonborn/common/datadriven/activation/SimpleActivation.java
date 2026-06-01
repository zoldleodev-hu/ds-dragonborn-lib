package hu.zoldleo.dragonborn.common.datadriven.activation;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.LevelBasedValue;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Function;

public record SimpleActivation(
        Optional<LevelBasedValue> initialManaCost,
        Optional<LevelBasedValue> castTime,
        Optional<LevelBasedValue> cooldown,
        Notification notification,
        boolean canMoveWhileCasting,
        Optional<Sound> sound,
        Optional<Animations> animations
) implements Activation {
    private static final Function<Sound, DataResult<Sound>> soundValidator = sound -> sound.looping().isPresent() ? DataResult.error(() -> "Simple activation does not support [looping] sounds") : DataResult.success(sound);
    private static final Function<Animations, DataResult<Animations>> animationValidator = animations -> animations.looping().isPresent() ? DataResult.error(() -> "Simple activation does not support [looping] animations") : DataResult.success(animations);
    public static final MapCodec<SimpleActivation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.optionalFieldOf("initial_mana_cost").forGetter(SimpleActivation::initialManaCost),
            LevelBasedValue.CODEC.optionalFieldOf("cast_time").forGetter(SimpleActivation::castTime),
            LevelBasedValue.CODEC.optionalFieldOf("cooldown").forGetter(SimpleActivation::cooldown),
            Notification.CODEC.optionalFieldOf("notification", Notification.DEFAULT).forGetter(SimpleActivation::notification),
            Codec.BOOL.optionalFieldOf("can_move_while_casting", true).forGetter(SimpleActivation::canMoveWhileCasting),
            Sound.CODEC.flatXmap(soundValidator, soundValidator).optionalFieldOf("sound").forGetter(SimpleActivation::sound),
            Animations.CODEC.flatXmap(animationValidator, animationValidator).optionalFieldOf("animations").forGetter(SimpleActivation::animations)
    ).apply(instance, SimpleActivation::new));

    public static final ResourceLocation ACTIVATION_TYPE = DragonSurvivalMod.res("simple");

    @Override
    public float getInitialManaCost(final int level) {
        return initialManaCost.map(cost -> cost.calculate(level))
                .orElseGet(() -> Activation.super.getInitialManaCost(level));
    }

    @Override
    public int getCastTime(final int level) {
        return castTime.map(time -> (int) time.calculate(level))
                .orElseGet(() -> Activation.super.getCastTime(level));
    }

    @Override
    public int getCooldown(final int level) {
        return cooldown.map(cooldown -> (int) cooldown.calculate(level))
                .orElseGet(() -> Activation.super.getCooldown(level));
    }

    @Override
    public ResourceLocation activationType() {
        return ACTIVATION_TYPE;
    }

    @Override
    public Type type() {
        return Type.SIMPLE;
    }

    @Override
    public MapCodec<? extends Activation> codec() {
        return CODEC;
    }
}