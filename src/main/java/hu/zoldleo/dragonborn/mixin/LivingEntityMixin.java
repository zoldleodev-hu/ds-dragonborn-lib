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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyReturnValue(method = "isFallFlying", at = @At("RETURN"))
    private boolean spinOrGlide(boolean original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Vec3 vec3 = entity.position();
        Vec3 vec31 = new Vec3(0, -1, 0);
        Vec3 vec32 = vec3.add(vec31.x * 0.01, vec31.y * 0.01, vec31.z * 0.01);
        BlockHitResult hit = entity.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.OUTLINE, net.minecraft.world.level.ClipContext.Fluid.NONE, entity));
        return original || (entity instanceof Player player && DragonbornUtils.isDragonborn(player) && (ServerFlightHandler.isSpin(player) || (ServerFlightHandler.isGliding(player) && !hit.isInside())));
    }
}