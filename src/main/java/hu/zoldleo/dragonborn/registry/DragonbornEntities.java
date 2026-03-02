package hu.zoldleo.dragonborn.registry;

import com.google.common.collect.ImmutableSet;
import hu.zoldleo.dragonborn.common.DragonbornEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber
public class DragonbornEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY;
    public static DeferredHolder<EntityType<?>, EntityType<DragonbornEntity>> DRAGONBORN;

    @SubscribeEvent
    public static void attributeCreationEvent(EntityAttributeCreationEvent event) {
        event.put(DRAGONBORN.value(), LivingEntity.createLivingAttributes().build());
    }

    static {
        REGISTRY = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, "dragonsurvival");
        DRAGONBORN = REGISTRY.register("dragonborn_extras", () -> new EntityType<>(DragonbornEntity::new, MobCategory.MISC, true, false, false, false, ImmutableSet.of(), EntityDimensions.fixed(0, 0), 1.0F, 0, 0, FeatureFlagSet.of(FeatureFlags.VANILLA)));
    }
}
