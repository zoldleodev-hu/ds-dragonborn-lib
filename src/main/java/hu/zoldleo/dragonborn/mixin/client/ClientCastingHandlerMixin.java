package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientCastingHandler;
import by.dragonsurvivalteam.dragonsurvival.magic.common.active.ActiveDragonAbility;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientCastingHandler.class, remap = false)
public class ClientCastingHandlerMixin {
    @Inject(method = "abilityKeyBindingChecks", at = @At(value = "INVOKE_ASSIGN", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/subcapabilities/MagicCap;getAbilityFromSlot(I)Lby/dragonsurvivalteam/dragonsurvival/magic/common/active/ActiveDragonAbility;", ordinal = 1), cancellable = true)
    private static void skipIfNull(TickEvent.ClientTickEvent clientTickEvent, CallbackInfo ci, @Local(name = "ability") ActiveDragonAbility ability) {
        if (ability == null)
            ci.cancel();
    }
}