package hu.zoldleo.dragonborn.common.datadriven.upgrade;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public interface UpgradeType<T> {
    /*Map<ResourceLocation, MapCodec<? extends UpgradeType<?>>> abilityUpgradeRegistry = new HashMap<>();
    Codec<UpgradeType<?>> CODEC = ResourceLocation.CODEC.dispatch("target_type", UpgradeType::type, x -> abilityUpgradeRegistry.get(x).codec());
    boolean init = init();

    static boolean init() {
        abilityUpgradeRegistry.put(ExperiencePointsUpgrade.UPGRADE_TYPE, ExperiencePointsUpgrade.CODEC);
        abilityUpgradeRegistry.put(ExperienceLevelUpgrade.UPGRADE_TYPE, ExperienceLevelUpgrade.CODEC);
        abilityUpgradeRegistry.put(DragonGrowthUpgrade.UPGRADE_TYPE, DragonGrowthUpgrade.CODEC);
        abilityUpgradeRegistry.put(ItemUpgrade.UPGRADE_TYPE, ItemUpgrade.CODEC);
        abilityUpgradeRegistry.put(ConditionUpgrade.UPGRADE_TYPE, ConditionUpgrade.CODEC);
        return true;
    }*/

    ResourceLocation type();

    static <V extends UpgradeType<?>> Products.P1<RecordCodecBuilder.Mu<V>, Integer> codecStart(final RecordCodecBuilder.Instance<V> instance) {
        return instance.group(ExtraCodecs.intRange(DataDrivenDragonAbility.MIN_LEVEL, DataDrivenDragonAbility.MAX_LEVEL).fieldOf("maximum_level").forGetter(UpgradeType::maxLevel));
    }

    @SuppressWarnings("unchecked") // ignore
    default boolean attempt(final ServerPlayer dragon, final DataDrivenDragonAbility ability, @Nullable final Object input) {
        // Need to find the 'UpgradeType' interface to check the parameter type
        Type[] interfaces = getClass().getGenericInterfaces();

        for (Type type : interfaces) {
            if (!(type instanceof ParameterizedType parameterized))
                continue;

            if (parameterized.getRawType() != UpgradeType.class)
                continue;

            Class<?> parameterClass = (Class<?>) parameterized.getActualTypeArguments()[0];

            // 'Void' as type parameter means the upgrade logic is not dependent on any input
            if (input == null && parameterClass == Void.class || parameterClass.isInstance(input)) {
                if (apply(dragon, ability, (T) input)) {
                    //PacketDistributor.PLAYER.with(() -> dragon).send(new SyncAbilityLevel(ability.key(), ability.level));
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean canUpgrade(final ServerPlayer dragon, final DataDrivenDragonAbility ability) {
        return ability.level < maxLevel();
    }

    default boolean canDowngrade(final ServerPlayer dragon, final DataDrivenDragonAbility ability) {
        return ability.level > minLevel();
    }

    default int minLevel() {
        return 0;
    }

    boolean apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability, final T input);

    MutableComponent getDescription(int abilityLevel);

    int maxLevel();

    MapCodec<? extends UpgradeType<?>> codec();
}