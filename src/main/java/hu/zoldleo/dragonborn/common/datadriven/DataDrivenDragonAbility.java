package hu.zoldleo.dragonborn.common.datadriven;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.magic.common.DragonAbility;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.codec.ActionContainer;
import hu.zoldleo.dragonborn.common.codec.DragonbornCodecs;
import hu.zoldleo.dragonborn.common.codec.LevelBasedResource;
import hu.zoldleo.dragonborn.common.datadriven.activation.Activation;
import hu.zoldleo.dragonborn.common.datadriven.upgrade.UpgradeType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

public class DataDrivenDragonAbility extends DragonAbility {
    public static final ResourceKey<Registry<DataDrivenDragonAbility>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvivalMod.res("dragon_ability"));
    public static final Codec<DataDrivenDragonAbility> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Activation.CODEC.fieldOf("activation").forGetter(ability -> ability.activation),
            //UpgradeType.CODEC.optionalFieldOf("upgrade").forGetter(ability -> ability.upgrade),
            DragonbornCodecs.LOOT_CONDITION_CODEC.optionalFieldOf("usage_blocked").forGetter(ability -> ability.usageBlocked),
            ActionContainer.CODEC.listOf().optionalFieldOf("actions", List.of()).forGetter(ability -> ability.actions),
            Codec.BOOL.optionalFieldOf("can_be_manually_disabled", true).forGetter(ability -> ability.canBeManuallyDisabled),
            LevelBasedResource.CODEC.fieldOf("icon").forGetter(ability -> ability.icon)
    ).apply(instance, DataDrivenDragonAbility::new));

    public static final int MIN_LEVEL = 0;
    public static final int MIN_LEVEL_FOR_CALCULATIONS = 1;
    public static final int MAX_LEVEL = 255;
    public static final int NO_COOLDOWN = 0;


    public final Activation activation;
    //public final Optional<UpgradeType<?>> upgrade;
    public final Optional<LootItemCondition> usageBlocked;
    public final List<ActionContainer> actions;
    public final boolean canBeManuallyDisabled;
    public final LevelBasedResource icon;

    private boolean isManuallyDisabled;
    private boolean isAutomaticallyDisabled;

    public DataDrivenDragonAbility(Activation activation, /*Optional<UpgradeType<?>> upgrade,*/ Optional<LootItemCondition> usageBlocked, List<ActionContainer> actions, boolean canBeManuallyDisabled, LevelBasedResource icon) {
        this.activation = activation;
        //this.upgrade = upgrade;
        this.usageBlocked = usageBlocked;
        this.actions = actions;
        this.canBeManuallyDisabled = canBeManuallyDisabled;
        this.icon = icon;
    }

    public void tick(ServerPlayer dragon) {
        if (isDisabled())
            return;
        for (ActionContainer action : actions)
            action.tick(dragon, this, dragon.server.getTickCount());
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return null;
    }

    @Override
    public ResourceLocation[] getSkillTextures() {
        return new ResourceLocation[0];
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public int getMinLevel() {
        return 0;
    }

    public boolean isUsable() {
        return !isDisabled() && level > 0;
    }

    public boolean isDisabled() {
        return !ServerConfig.dragonAbilities || isManuallyDisabled || isAutomaticallyDisabled;
    }
}