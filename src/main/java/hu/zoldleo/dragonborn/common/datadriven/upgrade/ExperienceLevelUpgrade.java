package hu.zoldleo.dragonborn.common.datadriven.upgrade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.LevelBasedValue;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record ExperienceLevelUpgrade(int maxLevel, LevelBasedValue levelRequirement) implements UpgradeType<InputData> {
    private static final String EXPERIENCE_LEVEL_UPGRADE = "dragonsurvival.gui.ability_upgrade.experience_level_upgrade";

    public static final MapCodec<ExperienceLevelUpgrade> CODEC = RecordCodecBuilder.mapCodec(instance -> UpgradeType.codecStart(instance)
            .and(LevelBasedValue.CODEC.fieldOf("level_requirement").forGetter(ExperienceLevelUpgrade::levelRequirement)).apply(instance, ExperienceLevelUpgrade::new)
    );

    @Override
    public ResourceLocation type() {
        return null;
    }

    @Override
    public boolean apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability, final InputData data) {
        if (data.type() != InputData.Type.EXPERIENCE_LEVELS)
            return false;

        int newLevel = 0;

        for (int level = DataDrivenDragonAbility.MIN_LEVEL_FOR_CALCULATIONS; level <= maxLevel(); level++) {
            if (data.input() < levelRequirement.calculate(level))
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
        return Component.translatable(EXPERIENCE_LEVEL_UPGRADE, (int) levelRequirement.calculate(abilityLevel));
    }

    @Override
    public MapCodec<? extends UpgradeType<?>> codec() {
        return CODEC;
    }
}