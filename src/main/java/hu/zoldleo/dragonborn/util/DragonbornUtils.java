package hu.zoldleo.dragonborn.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import hu.zoldleo.dragonborn.Dragonborn;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class DragonbornUtils {
    public static final DragonStateHandler emptyHandler = new DragonStateHandler();

    public static boolean isDragonborn(DragonStateHandler handler) {
        return handler != null && handler.isDragon() && handler.species().is(Dragonborn.DRAGONBORN_SPECIES);
    }

    public static boolean isDragonborn(Player player) {
        return isDragonborn(player.getData(DSDataAttachments.DRAGON_HANDLER));
    }

    public static boolean isDragonborn(@Nullable Entity entity) {
        return entity instanceof Player player && isDragonborn(player.getData(DSDataAttachments.DRAGON_HANDLER));
    }
}
