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
import by.dragonsurvivalteam.dragonsurvival.common.items.DragonSoulItem;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.ClawInventoryData;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonSoulItem.class)
public class DragonSoulItemMixin {
    @Inject(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/attachments/PenaltySupply;clear(Lnet/minecraft/world/entity/player/Player;)V"))
    private void reinsertItemsFixAndStoreProfile(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir, @Local(name = "player") Player player, @Local(name = "handler") DragonStateHandler handler) {
        if (DragonbornUtils.noClawSlotsDragon(handler))
            ClawInventoryData.reInsertClawTools(player);
        stack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
    }

    @Inject(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;revertToHumanForm(Lnet/minecraft/world/entity/player/Player;Z)V"))
    private void storeProfile(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir, @Local(name = "player") Player player) {
        stack.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
    }

    @Inject(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;remove(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private void removeProfile(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        stack.remove(DataComponents.PROFILE);
    }
}