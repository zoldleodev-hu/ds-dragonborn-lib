package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.SortingHandler;
import hu.zoldleo.dragonborn.server.DragonbornContainer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SortingHandler.class)
public class SortingHandlerMixin {
    @Inject(method = "sortInventory(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    private static void considerDragonborn(Player player, CallbackInfo ci) {
        if (player.containerMenu instanceof DragonbornContainer) {
            InvWrapper wrapper = new InvWrapper(player.getInventory());
            SortingHandler.sortInventory(wrapper, 9, 36);
        }
    }
}
