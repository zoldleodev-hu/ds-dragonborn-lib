package hu.zoldleo.dragonborn.common.datadriven.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.codec.DragonbornCodecs;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

public record ConditionUpgrade(List<LootItemCondition> conditions, boolean requirePrevious) implements UpgradeType<Void> {
    public static final MapCodec<ConditionUpgrade> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DragonbornCodecs.LOOT_CONDITION_CODEC.listOf().fieldOf("conditions").forGetter(ConditionUpgrade::conditions),
            Codec.BOOL.optionalFieldOf("require_previous", true).forGetter(ConditionUpgrade::requirePrevious)
    ).apply(instance, ConditionUpgrade::new));

    private static final LootContextParamSet CONTEXT = new LootContextParamSet.Builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.TOOL)
            .build();

    @Override
    public ResourceLocation type() {
        return null;
    }

    @Override
    public boolean apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability, final Void ignored) {
        LootContext context = createContext(dragon);
        int newLevel = 0;

        for (int level = DataDrivenDragonAbility.MIN_LEVEL_FOR_CALCULATIONS; level <= maxLevel(); level++) {
            if (!conditions.get(level - 1).test(context)) {
                if (requirePrevious) {
                    break;
                }

                continue;
            }

            if (requirePrevious) {
                newLevel++;
            } else {
                newLevel = level;
            }
        }

        if (newLevel != ability.level) {
            ability.setLevel(newLevel);
            return true;
        }

        return false;
    }

    private LootContext createContext(final ServerPlayer dragon) {
        LootParams parameters = new LootParams.Builder(dragon.serverLevel())
                .withParameter(LootContextParams.THIS_ENTITY, dragon)
                .withParameter(LootContextParams.ORIGIN, dragon.position())
                .withParameter(LootContextParams.TOOL, dragon.getMainHandItem())
                .create(CONTEXT);
        return new LootContext.Builder(parameters).create(null);
    }

    @Override
    public MutableComponent getDescription(final int abilityLevel) {
        return Component.empty();
    }

    @Override
    public int maxLevel() {
        return conditions.size();
    }

    @Override
    public MapCodec<? extends UpgradeType<?>> codec() {
        return CODEC;
    }
}