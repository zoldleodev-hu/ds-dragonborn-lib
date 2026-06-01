package hu.zoldleo.dragonborn.common.datadriven.upgrade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.LevelBasedValue;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record DragonGrowthUpgrade(int maxLevel, LevelBasedValue growthRequirement) implements UpgradeType<InputData> {
    private static final String DRAGON_SIZE_UPGRADE = "dragonsurvival.gui.ability_upgrade.dragon_growth_upgrade";

    public static final MapCodec<DragonGrowthUpgrade> CODEC = RecordCodecBuilder.mapCodec(instance -> UpgradeType.codecStart(instance)
            .and(LevelBasedValue.CODEC.fieldOf("growth_requirement").forGetter(DragonGrowthUpgrade::growthRequirement)).apply(instance, DragonGrowthUpgrade::new)
    );

    @Override
    public ResourceLocation type() {
        return null;
    }

    @Override
    public boolean apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability, final InputData data) {
        if (data.type() != InputData.Type.GROWTH)
            return false;

        int newLevel = 0;

        for (int level = DataDrivenDragonAbility.MIN_LEVEL_FOR_CALCULATIONS; level <= maxLevel(); level++) {
            if (data.input() < growthRequirement.calculate(level))
                break;
            newLevel++;
        }

        if (newLevel != ability.level) {
            ability.setLevel(newLevel);
            return true;
        }

        return false;
    }

    @Override
    public MutableComponent getDescription(final int abilityLevel) {
        return Component.translatable(DRAGON_SIZE_UPGRADE, (int) growthRequirement.calculate(abilityLevel));
    }

    @Override
    public MapCodec<? extends UpgradeType<?>> codec() {
        return CODEC;
    }
}