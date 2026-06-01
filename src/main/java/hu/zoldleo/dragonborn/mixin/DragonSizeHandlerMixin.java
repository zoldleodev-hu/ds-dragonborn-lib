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
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DragonSizeHandler.class, remap = false)
public abstract class DragonSizeHandlerMixin {
    @Inject(method = "overridePose", at = @At("HEAD"), cancellable = true)
    private static void excludeDragonborn(Player player, CallbackInfoReturnable<Pose> cir) {
        if (DragonbornUtils.isDragonborn(player)) {
            player.setForcedPose(null);
            cir.setReturnValue(player.getPose());
        }
    }

    @ModifyExpressionValue(method = "getDragonSize", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;isDragon()Z"))
    private static boolean dragonbornSize(boolean original, @Local(name = "handler") DragonStateHandler handler) {
        return original && !DragonbornUtils.isDragonDragonborn(handler);
    }

    /*/@Inject(method = "calculateDimensions", at = @At("HEAD"), cancellable = true)
    private static void injectPlayerDim(DragonStateHandler handler, Player player, Pose overridePose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (DragonbornUtils.isDragonborn(player)) {
            cir.setReturnValue(player.getDimensions(player.getPose()));
        }
    }*/

    @WrapOperation(method = "canPoseFit", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/handlers/DragonSizeHandler;calculateDimensions(DD)Lnet/minecraft/world/entity/EntityDimensions;"))
    private static EntityDimensions asd(double width, double height, Operation<EntityDimensions> original, @Local(argsOnly = true) LivingEntity player) {
        if (DragonbornUtils.isDragonborn(player))
            return player.getDimensions(player.getPose());
        return original.call(width, height);
    }

    /*/@Inject(method = "fudgePositionAfterSizeChange", at = @At("HEAD"), cancellable = true)
    private static void excludePassengerDragonborn(Entity entity, EntityDimensions currentDimension, EntityDimensions newDimensions, CallbackInfo ci) {
        if (DragonbornUtils.isDragonborn(entity) && entity.isPassenger())
            ci.cancel();
    }*/
}