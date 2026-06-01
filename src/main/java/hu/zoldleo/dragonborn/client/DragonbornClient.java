package hu.zoldleo.dragonborn.client;

import hu.zoldleo.dragonborn.registry.DragonbornContainers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DragonbornClient {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        MenuScreens.register(DragonbornContainers.dragonbornContainer, DragonbornInventoryScreen::new);
    }
}