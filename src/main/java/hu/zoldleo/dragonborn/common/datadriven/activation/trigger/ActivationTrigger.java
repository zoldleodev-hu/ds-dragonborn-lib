package hu.zoldleo.dragonborn.common.datadriven.activation.trigger;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hu.zoldleo.dragonborn.common.datadriven.activation.Activation;
import hu.zoldleo.dragonborn.common.datadriven.activation.ChanneledActivation;
import hu.zoldleo.dragonborn.common.datadriven.activation.PassiveActivation;
import hu.zoldleo.dragonborn.common.datadriven.activation.SimpleActivation;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mod.EventBusSubscriber
public interface ActivationTrigger<T> {
    Map<ResourceLocation, MapCodec<? extends ActivationTrigger<?>>> activationTriggerRegistry = new HashMap<>();
    Codec<ActivationTrigger<?>> CODEC = ResourceLocation.CODEC.dispatch("trigger_type", ActivationTrigger::type, x -> activationTriggerRegistry.get(x).codec());
    boolean init = init();

    static boolean init() {
        activationTriggerRegistry.put(ConstantTrigger.TYPE, ConstantTrigger.CODEC);
        return true;
    }

    default boolean test(final T testContext) {
        return true;
    }

    Component translation();

    MapCodec<? extends ActivationTrigger<?>> codec();

    ResourceLocation type();
}