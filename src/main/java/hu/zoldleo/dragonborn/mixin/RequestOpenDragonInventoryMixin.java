//  This file is part of Dragonborn lib.
//  Copyright (C) 2025  ZoldLeo
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
//  USA
//
//  zoldleo.dev@gmail.compackage hu.zoldleo.dragonborn.util;

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