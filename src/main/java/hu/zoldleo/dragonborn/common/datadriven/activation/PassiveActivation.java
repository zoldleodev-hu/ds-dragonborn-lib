package hu.zoldleo.dragonborn.common.datadriven.activation;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.LevelBasedValue;
import hu.zoldleo.dragonborn.common.codec.ability.ManaCost;
import hu.zoldleo.dragonborn.common.datadriven.activation.trigger.ActivationTrigger;
import hu.zoldleo.dragonborn.common.datadriven.activation.trigger.ConstantTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record PassiveActivation(Optional<ManaCost> continuousManaCost, Optional<LevelBasedValue> cooldown, ActivationTrigger<?> trigger) implements Activation {
    public static final PassiveActivation DEFAULT = new PassiveActivation(Optional.empty(), Optional.empty(), ConstantTrigger.INSTANCE);

    public static final MapCodec<PassiveActivation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ManaCost.CODEC.optionalFieldOf("continuous_mana_cost").forGetter(PassiveActivation::continuousManaCost),
            LevelBasedValue.CODEC.optionalFieldOf("cooldown").forGetter(PassiveActivation::cooldown),
            ActivationTrigger.CODEC.optionalFieldOf("trigger", ConstantTrigger.INSTANCE).forGetter(PassiveActivation::trigger)
    ).apply(instance, PassiveActivation::new));

    public static final ResourceLocation ACTIVATION_TYPE = DragonSurvivalMod.res("passive");

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
        return Type.PASSIVE;
    }

    @Override
    public MapCodec<? extends Activation> codec() {
        return CODEC;
    }
}