package hu.zoldleo.dragonborn.common.datadriven.activation.trigger;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record ConstantTrigger() implements ActivationTrigger<Void> {
    private static final String TRANSLATION = "trigger_type.dragonsurvival.constant";
    public static final ResourceLocation TYPE = DragonSurvivalMod.res("constant");

    public static final ConstantTrigger INSTANCE = new ConstantTrigger();
    public static final MapCodec<ConstantTrigger> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public Component translation() {
        return Component.translatable(TRANSLATION);
    }

    @Override
    public MapCodec<? extends ActivationTrigger<?>> codec() {
        return CODEC;
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }
}