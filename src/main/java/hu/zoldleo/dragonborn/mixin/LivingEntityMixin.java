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

import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    protected abstract boolean isImmobile();

    @ModifyReturnValue(method = "isFallFlying", at = @At("RETURN"))
    private boolean spinOrGlide(boolean original) {
        if ((LivingEntity)(Object)this instanceof Player player) {
            if (ServerFlightHandler.distanceFromGround(player) > 1)
                ((PlayerAccessor)player).landed(false);
            return original || (DragonbornUtils.isDragonborn(player) && (ServerFlightHandler.isSpin(player) || (ServerFlightHandler.isGliding(player) && !((PlayerAccessor)player).landed())));
        }
        return original;
    }

    @Inject(
            method = {"aiStep"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isImmobile()Z")}
    )
    private void dragonSurvival$preventMovement(CallbackInfo ci) {
        if ((Object)this instanceof Player player && isImmobile())
            player.yHeadRot = player.getYRot();
    }
}