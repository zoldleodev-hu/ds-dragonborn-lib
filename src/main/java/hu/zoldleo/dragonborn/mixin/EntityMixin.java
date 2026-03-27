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

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Entity.class, priority = 1500)
public abstract class EntityMixin {
    @TargetHandler(mixin = "by.dragonsurvivalteam.dragonsurvival.mixins.EntityMixin", name = "dragonSurvival$canRide")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateProvider;isDragon(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 0))
    private boolean excludeDragonborn(Entity player, Operation<Boolean> original) {
        return original.call(player) && !DragonbornUtils.isDragonDragonborn(player);
    }

    @TargetHandler(mixin = "by.dragonsurvivalteam.dragonsurvival.mixins.EntityMixin", name = "dragonSurvival$modifyPassengerRidingPosition")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateProvider;isDragon(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 0))
    private boolean dragonbornNoOffset1(Entity player, Operation<Boolean> original) {
        return original.call(player) && !DragonbornUtils.isDragonDragonborn(player);
    }

    @TargetHandler(mixin = "by.dragonsurvivalteam.dragonsurvival.mixins.EntityMixin", name = "dragonSurvival$modifyPassengerAttachmentPoint")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateProvider;isDragon(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 1))
    private boolean dragonbornNoOffset2(Entity player, Operation<Boolean> original) {
        return original.call(player) && !DragonbornUtils.isDragonDragonborn(player);
    }

    @TargetHandler(mixin = "by.dragonsurvivalteam.dragonsurvival.mixins.EntityMixin", name = "dragonSurvival$modifyPassengerAttachmentPoint")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateProvider;isDragon(Lnet/minecraft/world/entity/Entity;)Z", ordinal = 2))
    private boolean dragonbornNoOffset3(Entity player, Operation<Boolean> original) {
        return original.call(player) && !DragonbornUtils.isDragonDragonborn(player);
    }
}