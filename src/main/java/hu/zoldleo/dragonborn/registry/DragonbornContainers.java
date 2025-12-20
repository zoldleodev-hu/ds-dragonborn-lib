package hu.zoldleo.dragonborn.registry;

import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.client.DragonbornInventoryScreen;
import hu.zoldleo.dragonborn.server.DragonbornContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber
public class DragonbornContainers {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, Dragonborn.MODID);
    public static final DeferredHolder<MenuType<?>, MenuType<DragonbornContainer>> DRAGONBORN_CONTAINER = REGISTRY.register("dragonborn_container", () -> new MenuType<>(DragonbornContainer::new, FeatureFlags.DEFAULT_FLAGS));

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(DRAGONBORN_CONTAINER.get(), DragonbornInventoryScreen::new);
    }
}
