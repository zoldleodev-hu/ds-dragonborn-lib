package hu.zoldleo.dragonborn.common.datadriven.upgrade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.LevelBasedValue;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import hu.zoldleo.dragonborn.util.ExperienceUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.text.NumberFormat;

public record ExperiencePointsUpgrade(int maxLevel, LevelBasedValue experienceCost) implements UpgradeType<ExperiencePointsUpgrade.Type> {
    private static final String EXPERIENCE_POINTS_UPGRADE = "dragonsurvival.gui.ability_upgrade.experience_points_upgrade";

    public static final MapCodec<ExperiencePointsUpgrade> CODEC = RecordCodecBuilder.mapCodec(instance -> UpgradeType.codecStart(instance)
            .and(LevelBasedValue.CODEC.fieldOf("experience_cost").forGetter(ExperiencePointsUpgrade::experienceCost)).apply(instance, ExperiencePointsUpgrade::new)
    );

    private static final NumberFormat FORMAT = DragonbornUtils.numberFormat(2);

    @Override
    public boolean apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability, final ExperiencePointsUpgrade.Type type) {
        if (type == Type.UPGRADE && !canUpgrade(dragon, ability))
            return false;
        if (type == Type.DOWNGRADE && !canDowngrade(dragon, ability))
            return false;

        if (!dragon.isCreative()) {
            int experiencePoints = getExperience(ability, type);
            if (experiencePoints != 0)
                dragon.giveExperiencePoints(experiencePoints);
        }

        ability.setLevel(ability.level + type.step());
        return true;
    }

    @Override
    public MutableComponent getDescription(final int abilityLevel) {
        int experiencePoints = (int) experienceCost.calculate(abilityLevel);
        return Component.translatable(EXPERIENCE_POINTS_UPGRADE, experiencePoints, FORMAT.format(ExperienceUtils.getLevelAndProgress(experiencePoints)));
    }

    @Override
    public ResourceLocation type() {
        return null;
    }

    @Override
    public boolean canUpgrade(final ServerPlayer dragon, final DataDrivenDragonAbility ability) {
        return canModifyLevel(dragon, ability, Type.UPGRADE);
    }

    @Override
    public boolean canDowngrade(final ServerPlayer dragon, final DataDrivenDragonAbility ability) {
        return canModifyLevel(dragon, ability, Type.DOWNGRADE);
    }

    public boolean canModifyLevel(final Player player, final DataDrivenDragonAbility ability, final Type type) {
        return switch (type) {
            case UPGRADE -> ability.level < maxLevel() && (player.isCreative() || ExperienceUtils.getTotalExperience(player) >= Math.abs(getExperience(ability, type)));
            case DOWNGRADE -> ability.level > minLevel() && getExperience(ability, type) != 0;
        };
    }

    public int getExperience(final DataDrivenDragonAbility ability, final Type type) {
        int newLevel = ability.level + type.step();
        int experience = (int) experienceCost.calculate(type == Type.UPGRADE ? newLevel : ability.level);
        return type == Type.UPGRADE ? -experience : experience;
    }

    @Override
    public MapCodec<? extends UpgradeType<?>> codec() {
        return CODEC;
    }

    public enum Type {
        UPGRADE(1), DOWNGRADE(-1);

        private final int step;

        Type(int step) {
            this.step = step;
        }

        public int step() {
            return step;
        }
    }
}