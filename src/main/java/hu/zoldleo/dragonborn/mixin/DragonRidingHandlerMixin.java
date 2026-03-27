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

import by.dragonsurvivalteam.dragonsurvival.server.handlers.DragonRidingHandler;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DragonRidingHandler.class)
public abstract class DragonRidingHandlerMixin {
    @ModifyVariable(method = "playerCanRideDragon", at = @At("STORE"), name = "dragonIsTooSmallToRide")
    private static boolean dragonIsTooSmallToRide(boolean original, @Local(ordinal = 0, argsOnly = true) Player rider, @Local(name = "scaleRatio") double scaleRatio) {
        if (DragonbornUtils.isDragonborn(rider))
            return scaleRatio >= 0.8;
        return original;
    }

    @ModifyVariable(method = "onRideAttempt", at = @At("STORE"), name = "ridingScaleRatio")
    private static float ridingScaleRatio(float original, @Local(name = "self") Player self) {
        if (DragonbornUtils.isDragonborn(self))
            return 0.8f;
        return original;
    }
}