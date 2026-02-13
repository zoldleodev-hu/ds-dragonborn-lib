package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonRenderer.class)
public class DragonRendererMixin {
    @Inject(method = "getModelOffset", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/attachments/MovementData;getData(Lnet/minecraft/world/entity/Entity;)Lby/dragonsurvivalteam/dragonsurvival/registry/attachments/MovementData;"), cancellable = true)
    private void noOffset(DragonEntity dragon, float partialTicks, CallbackInfoReturnable<Vec3> cir, @Local Player player) {
        if (player instanceof FakeClientPlayer fake && DragonbornUtils.isDragonborn(fake.handler)) {
            cir.setReturnValue(Vec3.ZERO);
            cir.cancel();
        }
    }
}
