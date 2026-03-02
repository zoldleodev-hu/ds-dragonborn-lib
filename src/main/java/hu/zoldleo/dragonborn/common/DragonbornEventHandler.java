package hu.zoldleo.dragonborn.common;

import by.dragonsurvivalteam.dragonsurvival.util.DragonAnimations;
import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.client.DragonbornDB;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@EventBusSubscriber
public class DragonbornEventHandler {
    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player && !(player instanceof ServerPlayer) && DragonbornUtils.isDragonborn(player)) {
            DragonbornDB.getOrCreateDragonborn(player).mainAnimController.tryTriggerAnimation("jump");
        }
    }
}