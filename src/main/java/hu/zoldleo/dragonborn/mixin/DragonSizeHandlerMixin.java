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
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonSizeHandler.class)
public abstract class DragonSizeHandlerMixin {
    @Inject(method = "overridePose", at = @At("HEAD"), cancellable = true)
    private static void excludeDragonborn(Player player, CallbackInfoReturnable<Pose> cir) {
        if (DragonbornUtils.isDragonborn(player)) {
            player.setForcedPose(null);
            cir.setReturnValue(player.getPose());
        }
    }

    @Inject(method = "getDragonSize", at = @At(value = "INVOKE_ASSIGN", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateProvider;getData(Lnet/minecraft/world/entity/player/Player;)Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;"), cancellable = true)
    private static void dragonbornSize(EntityEvent.Size event, CallbackInfo ci, @Local DragonStateHandler handler) {
        if (DragonbornUtils.isDragonborn(handler)) {
            ci.cancel();
        }
    }

    @Inject(method = "calculateDimensions", at = @At("HEAD"), cancellable = true)
    private static void injectPlayerDim(DragonStateHandler handler, Player player, Pose overridePose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (DragonbornUtils.isDragonborn(player)) {
            cir.setReturnValue(player.getDimensions(player.getPose()));
        }
    }

    @Inject(method = "fudgePositionAfterSizeChange", at = @At("HEAD"), cancellable = true)
    private static void excludePassengerDragonborn(Entity entity, EntityDimensions currentDimension, EntityDimensions newDimensions, CallbackInfo ci) {
        if (DragonbornUtils.isDragonborn(entity) && entity.isPassenger())
            ci.cancel();
    }
}