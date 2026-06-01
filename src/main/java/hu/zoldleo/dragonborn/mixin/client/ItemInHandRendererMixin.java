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

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemInHandRenderer.class, priority = 1500, remap = false)
public class ItemInHandRendererMixin {
    @TargetHandler(mixin = "by.dragonsurvivalteam.dragonsurvival.mixins.MixinItemInHandRenderer", name = "hideArmsForDragon")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/util/DragonUtils;isDragon(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean renderArm(Entity entity, Operation<Boolean> original) {
        return original.call(entity) && !DragonbornUtils.isDragonDragonborn(entity);
    }

    @TargetHandler(mixin = "by.dragonsurvivalteam.dragonsurvival.mixins.MixinItemInHandRenderer", name = "hideArmsForDragonTwoHandedMap")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/util/DragonUtils;isDragon(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean renderArmWhenHoldingMaps(Entity entity, Operation<Boolean> original) {
        return original.call(entity) && !DragonbornUtils.isDragonDragonborn(entity);
    }
}