package hu.zoldleo.dragonborn.common.datadriven;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.MiscResources;
import hu.zoldleo.dragonborn.common.UnlockableBehavior;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;

public class DataDrivenDragonType extends AbstractDragonType {
    public static final ResourceKey<Registry<DataDrivenDragonType>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvivalMod.res("dragon_species"));

    public static final Codec<DataDrivenDragonType> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("is_dragonborn", false).forGetter(type -> type.isDragonborn),
            Codec.BOOL.optionalFieldOf("can_eat_human_food", false).forGetter(type -> type.canEatHumanFood),
            Codec.doubleRange(1, Double.MAX_VALUE).optionalFieldOf("starting_growth").forGetter(type -> type.startingGrowth),
            UnlockableBehavior.CODEC.optionalFieldOf("unlockable_behavior").forGetter(type -> type.unlockableBehavior),
            RegistryCodecs.homogeneousList(DataDrivenDragonBody.REGISTRY).optionalFieldOf("bodies", HolderSet.direct()).forGetter(type -> type.bodies),
            RegistryCodecs.homogeneousList(DataDrivenDragonAbility.REGISTRY).optionalFieldOf("abilities", HolderSet.direct()).forGetter(type -> type.abilities),
            MiscResources.CODEC.fieldOf("misc_resources").forGetter(type -> type.miscResources)
    ).apply(instance, instance.stable(DataDrivenDragonType::new)));

    public static final Codec<Holder<DataDrivenDragonType>> CODEC = RegistryFixedCodec.create(REGISTRY);

    public final boolean isDragonborn;
    public final boolean canEatHumanFood;
    public final Optional<Double> startingGrowth;
    public final Optional<UnlockableBehavior> unlockableBehavior;
    public final HolderSet<DataDrivenDragonBody> bodies;
    public final HolderSet<DataDrivenDragonAbility> abilities;
    public final MiscResources miscResources;

    private String name = "ERROR";

    public DataDrivenDragonType(boolean isDragonborn, boolean canEatHumanFood, Optional<Double> startingGrowth, Optional<UnlockableBehavior> unlockableBehavior, HolderSet<DataDrivenDragonBody> bodies, HolderSet<DataDrivenDragonAbility> abilities, MiscResources miscResources) {
        this.isDragonborn = isDragonborn;
        this.canEatHumanFood = canEatHumanFood;
        this.startingGrowth = startingGrowth;
        this.unlockableBehavior = unlockableBehavior;
        this.bodies = bodies;
        this.abilities = abilities;
        this.miscResources = miscResources;
    }

    public DataDrivenDragonType copy() {
        DataDrivenDragonType copied = new DataDrivenDragonType(isDragonborn, canEatHumanFood, startingGrowth, unlockableBehavior, bodies, abilities, miscResources);
        copied.name = name;
        return copied;
    }

    @Override
    public String getTypeName() {
        return name;
    }

    @Override
    public void onPlayerUpdate(Player player, DragonStateHandler dragonStateHandler) {

    }

    @Override
    public boolean isInManaCondition(Player player, DragonStateHandler dragonStateHandler) {
        return false;
    }

    @Override
    public void onPlayerDeath() {

    }

    @Override
    public List<Pair<ItemStack, FoodData>> validFoods(Player player, DragonStateHandler dragonStateHandler) {
        return List.of();
    }

    @Override
    public List<TagKey<Block>> mineableBlocks() {
        return List.of();
    }

    @Override
    public CompoundTag writeNBT() {
        return new CompoundTag(); // TODO save dragon supply
    }

    @Override
    public void readNBT(CompoundTag compoundTag) {

    }

    public static List<DataDrivenDragonType> getRegisteredDragonTypes() {
        HolderLookup.RegistryLookup<DataDrivenDragonType> lookup = DragonbornUtils.resolveLookup(REGISTRY);
        if (lookup == null)
            return List.of();
        return lookup.listElements().map(DataDrivenDragonType::setName).toList();
    }

    public static List<String> getRegisteredDragonTypeNames() {
        HolderLookup.RegistryLookup<DataDrivenDragonType> lookup = DragonbornUtils.resolveLookup(REGISTRY);
        if (lookup == null)
            return List.of();
        return lookup.listElementIds().map(x -> x.location().toString()).toList();
    }

    public static DataDrivenDragonType getRegisteredDragonTypeByName(String name) {
        HolderLookup.RegistryLookup<DataDrivenDragonType> lookup = DragonbornUtils.resolveLookup(REGISTRY);
        if (lookup == null)
            return null;
        return lookup.listElements().filter(x -> x.key().location().toString().equals(name)).findAny().map(DataDrivenDragonType::setName).orElse(null);
    }

    private static DataDrivenDragonType setName(Holder.Reference<DataDrivenDragonType> reference) {
        reference.value().name = reference.key().location().toString();
        return reference.value();
    }
}