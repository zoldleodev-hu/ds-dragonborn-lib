package hu.zoldleo.dragonborn.server;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ServerEventListener {
    @SubscribeEvent(receiveCanceled = true)
    public static void handleLanding(final LivingFallEvent event) throws NoSuchFieldException, IllegalAccessException {
        if (event.getEntity() instanceof Player player)
            player.getClass().getField("dragonborn$landed").setBoolean(player, true);
    }

    @SubscribeEvent
    public static void handleLanding(final PlayerFlyableFallEvent event) throws NoSuchFieldException, IllegalAccessException {
        event.getEntity().getClass().getField("dragonborn$landed").setBoolean(event.getEntity(), true);
    }
}