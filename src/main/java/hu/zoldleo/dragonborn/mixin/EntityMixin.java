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
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    DragonStateHandler dragonborn$tempHandler = null;

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("HEAD"))
    private void removeDragon(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            dragonborn$tempHandler = player.getData(DSDataAttachments.DRAGON_HANDLER);
            if (DragonbornUtils.isDragonborn(dragonborn$tempHandler))
                player.setData(DSDataAttachments.DRAGON_HANDLER, DragonbornUtils.emptyHandler);
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("RETURN"))
    private void restoreDragon(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            player.setData(DSDataAttachments.DRAGON_HANDLER, dragonborn$tempHandler);
        }
    }

    @Inject(method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", at = @At("HEAD"))
    private void removeDragonForPos(Entity passenger, Entity.MoveFunction callback, CallbackInfo ci) {
        if (passenger instanceof Player player) {
            dragonborn$tempHandler = player.getData(DSDataAttachments.DRAGON_HANDLER);
            if (DragonbornUtils.isDragonborn(dragonborn$tempHandler))
                player.setData(DSDataAttachments.DRAGON_HANDLER, DragonbornUtils.emptyHandler);
        }
    }

    @Inject(method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", at = @At("TAIL"))
    private void restoreDragonForPos(Entity passenger, Entity.MoveFunction callback, CallbackInfo ci) {
        if (passenger instanceof Player player) {
            player.setData(DSDataAttachments.DRAGON_HANDLER, dragonborn$tempHandler);
        }
    }
}