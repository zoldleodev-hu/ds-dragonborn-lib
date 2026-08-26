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

package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.client.DragonbornClientUtils;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftForm;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInvisible()Z"))
    private boolean noDragonbornDragonHitbox(boolean original, @Local(argsOnly = true) Entity entity) {
        if (entity instanceof DragonEntity dragon) {
            Player player = dragon.getPlayer();
            if (player != null && DragonbornUtils.isDragonborn(player) &&!ShapeshiftForm.isTransformed(player))
                return true;
        }
        return original;
    }

    @ModifyArg(method = "getRenderer", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    private Object getFakeModel(Object key, @Local(name = "abstractclientplayer") AbstractClientPlayer abstractclientplayer) {
        if (abstractclientplayer instanceof FakeClientPlayer fake && DragonbornClientUtils.getFakePlayerSkin(fake.handler) != null)
            return DragonbornClientUtils.getFakePlayerSkin(fake.handler).model();
        return key;
    }
}