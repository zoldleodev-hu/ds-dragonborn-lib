package hu.zoldleo.dragonborn.common.datadriven.targeting;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.codec.Condition;
import hu.zoldleo.dragonborn.common.codec.DragonbornCodecs;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import hu.zoldleo.dragonborn.common.datadriven.block_effects.AbilityBlockEffect;
import hu.zoldleo.dragonborn.common.datadriven.entity_effects.AbilityEntityEffect;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber
public interface AbilityTargeting {
    String EFFECT_HEADER = "#HEADER#";
    NumberFormat FORMAT = DragonbornUtils.numberFormat(2);

    Map<ResourceLocation, MapCodec<? extends AbilityTargeting>> abilityTargetingRegistry = new HashMap<>();
    Codec<AbilityTargeting> CODEC = ResourceLocation.CODEC.dispatch("target_type", AbilityTargeting::type, x -> abilityTargetingRegistry.get(x).codec());
    boolean init = init();

    static boolean init() {
        abilityTargetingRegistry.put(SelfTarget.TYPE, SelfTarget.CODEC);
        abilityTargetingRegistry.put(LookingAtTarget.TYPE, LookingAtTarget.CODEC);
        return true;
    }

    ResourceLocation type();

    static Either<BlockTargeting, EntityTargeting> block(final List<AbilityBlockEffect> effects) {
        return block(null, effects);
    }

    static Either<BlockTargeting, EntityTargeting> block(final LootItemCondition targetConditions, final List<AbilityBlockEffect> effects) {
        return Either.left(new BlockTargeting(Optional.ofNullable(targetConditions), effects));
    }

    static Either<BlockTargeting, EntityTargeting> entity(final List<AbilityEntityEffect> effects, final TargetingMode targetingMode) {
        return entity(null, effects, targetingMode);
    }

    static Either<BlockTargeting, EntityTargeting> entity(final LootItemCondition targetConditions, final List<AbilityEntityEffect> effects, final TargetingMode targetingMode) {
        return Either.right(new EntityTargeting(Optional.ofNullable(targetConditions), effects, targetingMode));
    }

    default float getDistance(final Player dragon, final DataDrivenDragonAbility instance) {
        return 0;
    }

    record BlockTargeting(Optional<LootItemCondition> targetConditions, List<AbilityBlockEffect> effects) {
        public static final Codec<BlockTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DragonbornCodecs.LOOT_CONDITION_CODEC.optionalFieldOf("target_conditions").forGetter(BlockTargeting::targetConditions),
                AbilityBlockEffect.CODEC.listOf().fieldOf("block_effect").forGetter(BlockTargeting::effects)
        ).apply(instance, BlockTargeting::new));

        public boolean matches(final ServerPlayer dragon, final BlockPos position) {
            return targetConditions.map(condition -> condition.test(Condition.blockContext(dragon, position))).orElse(true);
        }
    }

    record EntityTargeting(Optional<LootItemCondition> targetConditions, List<AbilityEntityEffect> effects, TargetingMode targetingMode) {
        public static final Codec<EntityTargeting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DragonbornCodecs.LOOT_CONDITION_CODEC.optionalFieldOf("target_conditions").forGetter(EntityTargeting::targetConditions),
                AbilityEntityEffect.CODEC.listOf().fieldOf("entity_effect").forGetter(EntityTargeting::effects),
                TargetingMode.CODEC.fieldOf("targeting_mode").forGetter(EntityTargeting::targetingMode)
        ).apply(instance, EntityTargeting::new));

        public boolean matches(final ServerPlayer dragon, final Entity entity, final Vec3 position) {
            return targetConditions.map(condition -> condition.test(Condition.abilityContext(dragon, entity, position))).orElse(true);
        }

        public List<ResourceLocation> getEffectIDs() {
            List<ResourceLocation> ids = new ArrayList<>();
            for (AbilityEntityEffect effect : effects)
                ids.addAll(effect.getEffectIDs());
            return ids;
        }
    }

    static <T extends AbilityTargeting> Products.P1<RecordCodecBuilder.Mu<T>, Either<BlockTargeting, EntityTargeting>> codecStart(final RecordCodecBuilder.Instance<T> instance) {
        return instance.group(Codec.either(BlockTargeting.CODEC, EntityTargeting.CODEC).fieldOf("applied_effects").forGetter(AbilityTargeting::target));
    }

    default List<MutableComponent> getAllEffectDescriptions(final Player dragon, final DataDrivenDragonAbility ability) {
        if (!ability.isUsable()) {
            return List.of();
        }

        List<MutableComponent> descriptions = new ArrayList<>();
        MutableComponent targetDescription = getDescription(dragon, ability);

        target().ifLeft(blockTargeting -> blockTargeting.effects().forEach(effect -> {
            List<MutableComponent> abilityEffectDescriptions = effect.getDescription(dragon, ability);

            if (!effect.getDescription(dragon, ability).isEmpty()) {
                descriptions.addAll(abilityEffectDescriptions.stream().map(description -> format(description, targetDescription)).toList());
            }
        })).ifRight(entityTargeting -> entityTargeting.effects().forEach(effect -> {
            List<MutableComponent> abilityEffectDescriptions = effect.getDescription(dragon, ability);

            if (!effect.getDescription(dragon, ability).isEmpty()) {
                if (this instanceof SelfTarget) {
                    descriptions.addAll(effect.getDescription(dragon, ability).stream().map(this::format).toList());
                } else {
                    descriptions.addAll(abilityEffectDescriptions.stream().map(description -> format(description, targetDescription)).toList());
                }
            }
        }));
        return descriptions;
    }

    private MutableComponent format(final MutableComponent description) {
        return Component.literal(EFFECT_HEADER).append(Component.literal("\n")).append(description);
    }

    private MutableComponent format(final MutableComponent description, final MutableComponent targetDescription) {
        return format(description).append(Component.literal("\n\n")).append(targetDescription);
    }

    default void remove(final ServerPlayer dragon, final DataDrivenDragonAbility ability) { /* Nothing to do */ }

    MutableComponent getDescription(final Player dragon, final DataDrivenDragonAbility ability);

    void apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability);

    MapCodec<? extends AbilityTargeting> codec();

    Either<BlockTargeting, EntityTargeting> target();
}