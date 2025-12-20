package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.DragonSurvivalClient;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonSurvivalClient.class)
public class DragonSurvivalClientMixin {
    @Inject(method = "preventThirdPersonWhenSuffocating", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/mixins/client/LocalPlayerAccessor;dragonSurvival$suffocatesAt(Lnet/minecraft/core/BlockPos;)Z"), cancellable = true)
    private void excludeDragonborn(ClientTickEvent.Post event, CallbackInfo ci, @Local Player player) {
        if (DragonbornUtils.isDragonborn(player))
            ci.cancel();
    }
}
