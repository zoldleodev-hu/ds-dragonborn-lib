package hu.zoldleo.dragonborn.common.datadriven.block_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public interface AbilityBlockEffect {
    Map<ResourceLocation, MapCodec<? extends AbilityBlockEffect>> abilityBlockEffectRegistry = new HashMap<>();
    Codec<AbilityBlockEffect> CODEC = ResourceLocation.CODEC.dispatch("effect_type", AbilityBlockEffect::type, x -> abilityBlockEffectRegistry.get(x).codec());
    boolean init = init();

    static boolean init() {
        return true;
    }

    default List<MutableComponent> getDescription(final Player dragon, final DataDrivenDragonAbility ability) {
        return List.of();
    }

    void apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability, final BlockPos position, @Nullable final Direction direction);

    MapCodec<? extends AbilityBlockEffect> blockCodec();

    ResourceLocation type();
}