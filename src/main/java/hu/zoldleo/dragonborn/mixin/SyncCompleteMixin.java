package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.network.syncing.SyncComplete;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ClawInventoryData;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SyncComplete.class)
public class SyncCompleteMixin {
    @Inject(method = "lambda$handleServer$1", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;refreshMagicData(Lnet/minecraft/server/level/ServerPlayer;Z)V", ordinal = 1))
    private static void reinsertClawToolsForDragonborn(IPayloadContext context, SyncComplete packet, CallbackInfo ci, @Local(name = "player") ServerPlayer player, @Local(name = "handler") DragonStateHandler handler) {
        if (DragonbornUtils.isSpeciesDragonborn(handler.species()))
            ClawInventoryData.reInsertClawTools(player);
    }
}