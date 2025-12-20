package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.container.RequestOpenDragonInventory;
import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import hu.zoldleo.dragonborn.server.DragonbornContainer;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RequestOpenDragonInventory.class)
public class RequestOpenDragonInventoryMixin {
    @Inject(method = "handleServer", at = @At("HEAD"), cancellable = true)
    private static void considerDragonborn(RequestOpenDragonInventory ignored, IPayloadContext context, CallbackInfo ci) {
        context.enqueueWork(() -> {
            DragonStateHandler handler = DragonStateProvider.getData(context.player());
            if (DragonbornUtils.isDragonborn(handler)) {
                context.player().containerMenu.removed(context.player());
                context.player().openMenu(new SimpleMenuProvider((containerId, inventory, player) -> new DragonbornContainer(containerId, inventory), Component.empty()));
            }
            else if (handler.isDragon()) {
                context.player().containerMenu.removed(context.player());
                context.player().openMenu(new SimpleMenuProvider((containerId, inventory, player) -> new DragonContainer(containerId, inventory), Component.empty()));
            }
        });
        ci.cancel();
    }
}
