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
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, priority = 1500, remap = false)
public abstract class PlayerMixin {
    @ModifyExpressionValue(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isAutoSpinAttack()Z"))
    private boolean spin(boolean original) {
        Player player = (Player)(Object)this;
        return original || (DragonbornUtils.isDragonborn(player) && ServerFlightHandler.isSpin(player));
    }

    @ModifyExpressionValue(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isFallFlying()Z"))
    private boolean glide(boolean original) {
        Player player = (Player)(Object)this;
        return original || (DragonbornUtils.isDragonborn(player) && ServerFlightHandler.isGliding(player));
    }

    @ModifyExpressionValue(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSleeping()Z"))
    private boolean rest(boolean original) {
        Player player = (Player)(Object)this;
        return original || DragonbornUtils.getHandler(player).treasureResting;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void setBedOrientation(CallbackInfo ci) {
        Player player = (Player)(Object)this;
        if (DragonbornUtils.isDragonborn(player) && !DragonbornUtils.getHandler(player).treasureResting)
            dragonborn$sleepDir = player.yHeadRot;
    }

    @Unique
    @SuppressWarnings("unused")
    public boolean dragonborn$landed;

    @Unique
    @SuppressWarnings("all")
    public float dragonborn$sleepDir;
}