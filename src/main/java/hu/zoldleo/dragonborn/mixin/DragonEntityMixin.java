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
//  zoldleo.dev@gmail.com

package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftForm;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonEntity.class)
public class DragonEntityMixin {
    @ModifyExpressionValue(method = "predicate", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/codecs/ability/animation/AbilityAnimation;locksHead()Z"))
    private boolean dontLockHead(boolean original, @Local(name = "player") Player player) {
        return original && !DragonbornUtils.isDragonborn(player) && !ShapeshiftForm.isTransformed(player);
    }

    @ModifyExpressionValue(method = "predicate", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/codecs/ability/animation/AbilityAnimation;locksTail()Z"))
    private boolean dontLockTail(boolean original, @Local(name = "player") Player player) {
        return original && !DragonbornUtils.isDragonborn(player) && !ShapeshiftForm.isTransformed(player);
    }
}