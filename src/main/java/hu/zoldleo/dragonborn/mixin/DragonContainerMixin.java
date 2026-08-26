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

import by.dragonsurvivalteam.dragonsurvival.server.containers.DragonContainer;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonContainer.class)
public class DragonContainerMixin {
    @Shadow
    @Mutable
    @Final
    public CraftingContainer craftMatrix;

    @Shadow
    @Final
    public Player player;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/server/containers/DragonContainer;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;", ordinal = 5))
    private void reduceCraftingGridSize(int id, Inventory inventory, CallbackInfo ci) {
        if (DragonbornUtils.humanCraftingGrid(player))
            craftMatrix = new TransientCraftingContainer((DragonContainer)(Object)this, 2, 2);
    }

    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/attachments/ClawInventoryData;isMenuOpen()Z"))
    private boolean checkForClawSlotTag(boolean original) {
        return original && !DragonbornUtils.noClawSlots(player);
    }

    @WrapOperation(method = "<init>", at = @At(value = "NEW", target = "(Lnet/minecraft/world/Container;III)Lnet/minecraft/world/inventory/Slot;", ordinal = 3))
    private Slot fixCraftingGridPosition(Container container, int slot, int x, int y, Operation<Slot> original, @Local(name = "column") int column, @Local(name = "row") int row) {
        int xOffset = 116;
        int yOffset = 26;
        if (DragonbornUtils.humanCraftingGrid(player)) {
            xOffset = 125;
            yOffset = 35;
        }
        return original.call(craftMatrix, column + row * craftMatrix.getHeight(), xOffset + column * 18, yOffset + row * 18);
    }
}