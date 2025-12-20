package hu.zoldleo.dragonborn.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import hu.zoldleo.dragonborn.common.DragonbornEntity;
import hu.zoldleo.dragonborn.registry.DragonbornEntities;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@EventBusSubscriber
public class DragonbornDB {
    private static final Map<Integer, DragonbornEntity> PLAYER_DRAGONBORN_MAP = new ConcurrentHashMap<>();

    public static DragonbornEntity getOrCreateDragonborn(Player player) {
        return PLAYER_DRAGONBORN_MAP.computeIfAbsent(player.getId(), (key) -> {
            DragonbornEntity newDragon = DragonbornEntities.DRAGONBORN.get().create(player.level());
            assert newDragon != null;
            newDragon.playerId = key;
            return newDragon;
        });
    }

    public static @Nullable DragonbornEntity getDragonborn(Player player) {
        return PLAYER_DRAGONBORN_MAP.get(player.getId());
    }

    public static void process(Consumer<DragonbornEntity> processor) {
        PLAYER_DRAGONBORN_MAP.values().forEach(processor);
    }

    @SubscribeEvent
    public static void removeEntry(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            DragonbornEntity dragon = PLAYER_DRAGONBORN_MAP.remove(player.getId());
            if (dragon != null) {
                DragonRenderer.BONE_POSITIONS.remove(dragon.getId());
            }
        }

    }

    @SubscribeEvent
    public static void clearEntries(LevelEvent.Unload event) {
        PLAYER_DRAGONBORN_MAP.clear();
        DragonRenderer.BONE_POSITIONS.clear();
    }
}
