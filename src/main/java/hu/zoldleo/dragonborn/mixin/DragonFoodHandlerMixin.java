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

import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.Dragonborn;
import net.minecraft.core.Holder;
import net.minecraft.world.food.FoodProperties;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonFoodHandler.class)
public class DragonFoodHandlerMixin {
    @ModifyReturnValue(method = "getDragonFoodProperties", at = @At(value = "RETURN", ordinal = 3))
    private static @Nullable FoodProperties addHumanFood(@Nullable FoodProperties dsOriginal, @Local(argsOnly = true) Holder<DragonSpecies> species, @Local(argsOnly = true) FoodProperties original) {
        if (species.is(Dragonborn.CAN_EAT_HUMAN_FOOD))
            return original;
        return dsOriginal;
    }
}