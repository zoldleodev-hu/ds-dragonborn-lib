package hu.zoldleo.dragonborn.common.datadriven.activation;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.LevelBasedValue;
import hu.zoldleo.dragonborn.common.codec.ability.ManaCost;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Function;

public record ChanneledActivation(
        Optional<LevelBasedValue> initialManaCost,
        Optional<ManaCost> continuousManaCost,
        Optional<LevelBasedValue> castTime,
        Optional<LevelBasedValue> cooldown,
        Optional<LevelBasedValue> maxDuration,
        Notification notification,
        boolean canMoveWhileCasting,
        Optional<Sound> sound,
        Optional<Animations> animations
) implements Activation {
    private static final Function<ManaCost, DataResult<ManaCost>> manacostValidator = cost -> cost.manaCostType() == ManaCost.ManaCostType.TICKING ? DataResult.success(cost) : DataResult.error(() -> "Channeled activation only supports [ticking] continuous mana cost");
    public static final MapCodec<ChanneledActivation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.optionalFieldOf("initial_mana_cost").forGetter(ChanneledActivation::initialManaCost),
            ManaCost.CODEC.flatXmap(manacostValidator, manacostValidator).optionalFieldOf("continuous_mana_cost").forGetter(ChanneledActivation::continuousManaCost),
            LevelBasedValue.CODEC.optionalFieldOf("cast_time").forGetter(ChanneledActivation::castTime),
            LevelBasedValue.CODEC.optionalFieldOf("cooldown").forGetter(ChanneledActivation::cooldown),
            LevelBasedValue.CODEC.optionalFieldOf("max_duration").forGetter(ChanneledActivation::maxDuration),
            Notification.CODEC.optionalFieldOf("notification", Notification.DEFAULT).forGetter(ChanneledActivation::notification),
            Codec.BOOL.optionalFieldOf("can_move_while_casting", true).forGetter(ChanneledActivation::canMoveWhileCasting),
            Sound.CODEC.optionalFieldOf("sound").forGetter(ChanneledActivation::sound),
            Animations.CODEC.optionalFieldOf("animations").forGetter(ChanneledActivation::animations)
    ).apply(instance, ChanneledActivation::new));

    public static final ResourceLocation ACTIVATION_TYPE = DragonSurvivalMod.res("channeled");

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

    public boolean hasReachedMaxDuration(final int level, final int currentTick) {
        return maxDuration.map(duration -> currentTick > (int) duration.calculate(level)).orElse(false);
    }

    @Override
    public ResourceLocation activationType() {
        return ACTIVATION_TYPE;
    }

    @Override
    public Type type() {
        return Type.CHANNELED;
    }

    @Override
    public MapCodec<? extends Activation> codec() {
        return CODEC;
    }
}