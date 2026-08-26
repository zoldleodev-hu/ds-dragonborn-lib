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

package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.DragonAltarScreen;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonAltarScreen.class)
public class DragonAltarScreenMixin {
    @Shadow
    @Final
    private DragonStateHandler handler1;

    @Shadow
    @Final
    private DragonStateHandler handler2;

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;clamp(JII)I", ordinal = 0))
    private int entity1Scale(int original) {
        if (DragonbornUtils.isDragonborn(handler1))
            return 40;
        return original;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;clamp(JII)I", ordinal = 1))
    private int entity2Scale(int original) {
        if (DragonbornUtils.isDragonborn(handler2))
            return 40;
        return original;
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;isDragon()Z"))
    private boolean renderFakeDragonborn(DragonStateHandler instance, Operation<Boolean> original) {
        return original.call(instance) && !DragonbornUtils.isDragonDragonborn(instance);
    }
}