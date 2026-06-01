package hu.zoldleo.dragonborn.common.codec;

import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifierManager;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class DragonbornCodecs {
    public static final Codec<LootItemCondition> LOOT_CONDITION_CODEC = Codec.PASSTHROUGH.flatXmap(
            d -> {
                try {
                    LootItemCondition conditions = LootModifierManager.GSON_INSTANCE.fromJson(getJson(d), LootItemCondition.class);
                    return DataResult.success(conditions);
                }
                catch (JsonSyntaxException e) {
                    LootModifierManager.LOGGER.warn("Unable to decode loot condition", e);
                    return DataResult.error(e::getMessage);
                }
            },
            condition -> {
                try {
                    JsonElement element = LootModifierManager.GSON_INSTANCE.toJsonTree(condition);
                    return DataResult.success(new Dynamic<>(JsonOps.INSTANCE, element));
                }
                catch (JsonSyntaxException e) {
                    LootModifierManager.LOGGER.warn("Unable to encode loot condition", e);
                    return DataResult.error(e::getMessage);
                }
            }
    );

    @SuppressWarnings("unchecked")
    static <U> JsonElement getJson(Dynamic<?> dynamic) {
        Dynamic<U> typed = (Dynamic<U>) dynamic;
        return typed.getValue() instanceof JsonElement ?
                (JsonElement) typed.getValue() :
                typed.getOps().convertTo(JsonOps.INSTANCE, typed.getValue());
    }

    public static final Codec<ClawInventory.Slot> CLAW_INVENTORY_SLOT_CODEC = fromEnum(ClawInventory.Slot::values);

    public static final Codec<AttributeModifier.Operation> ATTRIBUTE_MODIFIER_OPERATION_CODEC = fromEnum(AttributeModifier.Operation::values);

    // for enums of length <= 16
    protected static <E extends Enum<E>> Codec<E> fromEnum(Supplier<E[]> valueSupplier) {
        E[] valueArray = valueSupplier.get();
        return ExtraCodecs.orCompressed(ExtraCodecs.stringResolverCodec(value -> value.name().toLowerCase(Locale.ENGLISH),
                name -> {
                    for(E value : valueArray)
                        if (value.name().toLowerCase(Locale.ENGLISH).equals(name))
                            return value;
                    return null;
                }), ExtraCodecs.idResolverCodec(Enum::ordinal, i -> i >= 0 && i < valueArray.length ? valueArray[i] : null, -1));
    }

    public static final Codec<MobEffectInstance> MOB_EFFECT_INSTANCE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("id").forGetter(MobEffectInstance::getEffect),
            Codec.INT.optionalFieldOf("duration", 0).forGetter(MobEffectInstance::getDuration),
            ExtraCodecs.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(MobEffectInstance::getAmplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance::isAmbient),
            Codec.BOOL.optionalFieldOf("show_particles", true).forGetter(MobEffectInstance::isVisible),
            Codec.BOOL.optionalFieldOf("show_icon", true).forGetter(MobEffectInstance::showIcon)
    ).apply(instance, MobEffectInstance::new));

    protected static final Codec<Pair<MobEffectInstance, Float>> FOOD_EFFECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MOB_EFFECT_INSTANCE_CODEC.fieldOf("effect").forGetter(Pair<MobEffectInstance, Float>::getFirst),
            Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(Pair<MobEffectInstance, Float>::getSecond)
    ).apply(instance, Pair::of));

    public static final Codec<FoodProperties> FOOD_PROPERTIES_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodProperties::getNutrition),
            Codec.FLOAT.fieldOf("saturation").forGetter(FoodProperties::getSaturationModifier),
            Codec.BOOL.optionalFieldOf("is_meat", true).forGetter(FoodProperties::isMeat),
            Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(FoodProperties::canAlwaysEat),
            Codec.BOOL.optionalFieldOf("fast_food", false).forGetter(FoodProperties::isFastFood),
            FOOD_EFFECT_CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(FoodProperties::getEffects)
    ).apply(instance, DragonbornCodecs::createFoodProperties));

    protected static FoodProperties createFoodProperties(int nutrition, float saturationModifier, boolean isMeat, boolean canAlwaysEat, boolean fastFood, List<Pair<MobEffectInstance, Float>> effects) {
        FoodProperties.Builder builder = new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturationModifier);
        if (isMeat)
            builder.meat();
        if (canAlwaysEat)
            builder.alwaysEat();
        if (fastFood)
            builder.fast();
        for (var effect : effects)
            builder.effect(effect::getFirst, effect.getSecond());
        return builder.build();
    }
}