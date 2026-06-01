package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.MagicHandler;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagicHandler.class)
public class MagicHandlerMixin {
    @Inject(method = "lambda$magicUpdate$0", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;getMagicData()Lby/dragonsurvivalteam/dragonsurvival/common/capability/subcapabilities/MagicCap;", ordinal = 0))
    private static void tickDataDrivenAbilities(AttributeInstance moveSpeed, DragonStateHandler cap, CallbackInfo ci, @Local TickEvent.PlayerTickEvent event) {
        //if (!(cap.getType() instanceof DataDrivenDragonType type))
        //    return;
        //type.abilities.forEach(x -> x.get().tick(event.player));
    }
}