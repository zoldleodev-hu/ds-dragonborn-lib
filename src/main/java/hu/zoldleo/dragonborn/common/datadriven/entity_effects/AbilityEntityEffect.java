package hu.zoldleo.dragonborn.common.datadriven.entity_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public interface AbilityEntityEffect {
    Map<ResourceLocation, MapCodec<? extends AbilityEntityEffect>> abilityEntityEffectRegistry = new HashMap<>();
    Codec<AbilityEntityEffect> CODEC = ResourceLocation.CODEC.dispatch("effect_type", AbilityEntityEffect::type, x -> abilityEntityEffectRegistry.get(x).codec());
    boolean init = init();

    static boolean init() {
        return true;
    }

    void apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability, final Entity target);

    MapCodec<? extends AbilityEntityEffect> entityCodec();

    ResourceLocation type();

    default List<MutableComponent> getDescription(final Player dragon, final DataDrivenDragonAbility ability) {
        return List.of();
    }

    default void remove(final ServerPlayer dragon, final DataDrivenDragonAbility ability, final Entity entity, boolean isAutoRemoval) { /* Nothing to do */ }

    default boolean shouldRemoveAutomatically() {
        return false;
    }

    default List<ResourceLocation> getEffectIDs() { return List.of(); }
}