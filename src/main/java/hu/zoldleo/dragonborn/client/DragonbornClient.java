package hu.zoldleo.dragonborn.client;

import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.registry.DragonbornEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = Dragonborn.MODID, dist = Dist.CLIENT)
public class DragonbornClient {
    public  DragonbornClient(final IEventBus bus, final ModContainer container) {
        bus.addListener(this::setup);
    }

    private void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(DragonbornEntities.DRAGONBORN.get(), manager -> new DragonbornEntityRenderer(manager, new DragonbornModel()));
        });
    }
}
