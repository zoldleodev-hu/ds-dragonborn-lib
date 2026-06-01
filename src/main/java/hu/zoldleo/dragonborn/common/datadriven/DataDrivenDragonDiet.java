package hu.zoldleo.dragonborn.common.datadriven;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.codec.DragonbornCodecs;
import hu.zoldleo.dragonborn.common.ResourceLocationWrapper;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber
public class DataDrivenDragonDiet {
    public static final ResourceKey<Registry<DietModifier>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvivalMod.res("diet_entries"));
    protected static final Map<String, Map<Item, Optional<FoodProperties>>> dietByType = new HashMap<>();

    private static void generateDiets() { // No need to clear the diets first; they get overwritten
        Registry<DietModifier> registry = DragonbornUtils.resolveRegistry(REGISTRY);
        if (registry == null)
            return;
        HashMap<String, ArrayList<DietModifier>> modifierListByTypeMap = new HashMap<>();
        registry.stream().forEach(modifier -> modifierListByTypeMap.computeIfAbsent(modifier.type(), type -> new ArrayList<>()).add(modifier));
        modifierListByTypeMap.values().forEach(modifierList -> modifierList.sort(Comparator.comparingInt(DietModifier::priority)));
        modifierListByTypeMap.forEach((type, modifierList) -> {
            HashMap<Item, Optional<FoodProperties>> propertiesByItem = new HashMap<>();
            modifierList.forEach(modifier -> {
                modifier.toAdd().forEach(entry -> entry.toSingleList().forEach(single -> propertiesByItem.put(single.item(), single.properties())));
                ArrayList<Item> itemsToRemove = new ArrayList<>();
                modifier.toRemove().forEach(string -> ResourceLocationWrapper.getEntries(string, BuiltInRegistries.ITEM).forEach(key -> itemsToRemove.add(ForgeRegistries.ITEMS.getValue(key))));
                itemsToRemove.forEach(propertiesByItem::remove);
            });
            dietByType.put(type, propertiesByItem);
        });
    }

    public static Map<Item, Optional<FoodProperties>> getDietForType(String type) {
        return dietByType.getOrDefault(type, Map.of());
    }

    public static boolean canEatItem(String type, Item item) {
        return getDietForType(type).containsKey(item);
    }

    public static @Nullable FoodProperties getFoodProperties(String type, ItemStack stack, @Nullable LivingEntity entity) {
        return getDietForType(type).getOrDefault(stack.getItem(), Optional.empty()).orElse(stack.getFoodProperties(entity));
    }

    @SubscribeEvent
    public static void onTagUpdateEvent(TagsUpdatedEvent event) {
        generateDiets();
    }

    public record DietModifier(int priority, String type, List<DietEntry> toAdd, List<String> toRemove) {
        public static final Codec<DietModifier> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("priority").forGetter(DietModifier::priority),
                Codec.STRING.fieldOf("dragon_type").forGetter(DietModifier::type),
                DietEntry.CODEC.listOf().fieldOf("add").forGetter(DietModifier::toAdd),
                ResourceLocationWrapper.validatedCodec().listOf().fieldOf("remove").forGetter(DietModifier::toRemove)
        ).apply(instance, DietModifier::new));

        public int hashCode() {
            return ((priority * 31 + type.hashCode()) * 31 + toAdd.hashCode()) * 31 + toRemove.hashCode();
        }

        public boolean equals(Object obj) {
            return obj instanceof DietModifier other &&
                    priority == other.priority &&
                    type.equals(other.type) &&
                    toAdd.equals(other.toAdd) &&
                    toRemove.equals(other.toRemove);
        }
    }

    public record DietEntry(String items, Optional<FoodProperties> properties) {
        public static final Codec<DietEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocationWrapper.validatedCodec().fieldOf("items").forGetter(DietEntry::items),
                DragonbornCodecs.FOOD_PROPERTIES_CODEC.optionalFieldOf("properties").forGetter(DietEntry::properties)
        ).apply(instance, DietEntry::new));

        public int hashCode() {
            return items.hashCode() * 31 + properties.hashCode();
        }

        public List<Single> toSingleList() {
            return ResourceLocationWrapper.getEntries(items, BuiltInRegistries.ITEM).stream().map(ForgeRegistries.ITEMS::getValue).map(item -> new Single(item, properties)).toList();
        }

        public record Single(Item item, Optional<FoodProperties> properties) {}
    }
}