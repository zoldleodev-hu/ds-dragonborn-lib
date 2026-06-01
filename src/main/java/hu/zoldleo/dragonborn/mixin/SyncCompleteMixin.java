package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.CompleteDataSync;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CompleteDataSync.class, remap = false)
public class SyncCompleteMixin {
    @Inject(method = "lambda$runServer$1", at = @At("TAIL"))
    private static void reinsertClawToolsForDragonborn(CompleteDataSync message, ServerPlayer player, DragonStateHandler handler, CallbackInfo ci) {
        if (DragonbornUtils.isDragonborn(handler))
            DragonbornUtils.reInsertClawTools(player);
    }
}