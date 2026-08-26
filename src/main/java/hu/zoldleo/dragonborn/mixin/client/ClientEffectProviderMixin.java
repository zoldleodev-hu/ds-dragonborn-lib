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

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftForm;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ClientEffectProvider.class)
public interface ClientEffectProviderMixin {
    @Inject(method = "getProviders", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/attachments/FlightData;getData(Lnet/minecraft/world/entity/player/Player;)Lby/dragonsurvivalteam/dragonsurvival/registry/attachments/FlightData;"))
    private static void getShapeshiftProvider(boolean isInventory, CallbackInfoReturnable<List<ClientEffectProvider>> cir, @Local(name = "player") Player player, @Local(name = "providers") List<ClientEffectProvider> providers) {
        if (ShapeshiftForm.isTransformed(player) && ShapeshiftForm.getData(player).value().icon().isPresent())
            providers.add(ShapeshiftForm.SHAPESHIFT_EFFECT);
    }
}