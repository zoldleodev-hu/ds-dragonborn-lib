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

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ClawInventoryData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(DragonStateHandler.class)
public class DragonStateHandlerMixin {
    @Unique
    @SuppressWarnings("unused")
    public PlayerSkin dragonborn$fakeSkin;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "setSpecies(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/Holder;Z)V", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/attachments/PenaltySupply;clear(Lnet/minecraft/world/entity/player/Player;)V", ordinal = 0))
    private void reinsertClawToolsForDragonborn(@Nullable Player player, @Nullable Holder<DragonSpecies> species, boolean savedForSoul, CallbackInfo ci) {
        if (DragonbornUtils.noClawSlots(species))
            ClawInventoryData.reInsertClawTools(player);
    }
}