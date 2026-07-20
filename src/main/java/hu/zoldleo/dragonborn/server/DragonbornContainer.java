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

package hu.zoldleo.dragonborn.server;

import com.mojang.datafixers.util.Pair;
import hu.zoldleo.dragonborn.registry.DragonbornContainers;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Optional;

public class DragonbornContainer extends AbstractContainerMenu {
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    public final CraftingContainer craftMatrix = new TransientCraftingContainer(this, 2, 2);
    public final ResultContainer craftResult = new ResultContainer();
    public final Player player;
    public final Inventory playerInventory;

    public DragonbornContainer(int id, Inventory inventory) {
        super(DragonbornContainers.dragonbornContainer, id);
        this.player = inventory.player;
        this.playerInventory = inventory;
        // Crafting result
        this.addSlot(new ResultSlot(inventory.player, this.craftMatrix, this.craftResult, 0, 178, 33));
        addCraftingSlots();
        addArmorSlots(inventory);
        addInventorySlots(inventory);
        addHotbarSlots(inventory);
        // Offhand
        this.addSlot(new Slot(inventory, 40, 26, 62));

        this.broadcastChanges();
    }

    private void addArmorSlots(Inventory inventory) {
        for (int i = 0; i < 4; i++) {
            final EquipmentSlot equipmentSlot = VALID_EQUIPMENT_SLOTS[i];
            this.addSlot(new Slot(inventory, 39 - i, 8, 8 + i * 18) {
                public boolean mayPlace(@NotNull ItemStack itemStack) {
                    return itemStack.canEquip(equipmentSlot, DragonbornContainer.this.player);
                }

                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, DragonbornContainer.ARMOR_SLOT_TEXTURES[equipmentSlot.getIndex()]);
                }

                public boolean mayPickup(@NotNull Player player) {
                    ItemStack itemStack = this.getItem();
                    return (itemStack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.mayPickup(player);
                }
            });
        }
    }

    private void addHotbarSlots(Inventory inventory) {
        for (int i = 0; i < 9; i++)
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
    }

    private void addInventorySlots(Inventory inventory) {
        for (int column = 0; column < 3; column++)
            for (int row = 0; row < 9; row++)
                this.addSlot(new Slot(inventory, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
    }

    private void addCraftingSlots() {
        for (int row = 0; row < 2; row++)
            for (int column = 0; column < 2; column++)
                this.addSlot(new Slot(this.craftMatrix, column + row * 2, 120 + column * 18, 24 + row * 18));
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            EquipmentSlot equipmentslot = LivingEntity.getEquipmentSlotForItem(itemstack);
            if (index == 0) {
                if (!moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= 1 && index < 5) {
                if (!moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentslot.getType() == EquipmentSlot.Type.ARMOR && !slots.get(8 - equipmentslot.getIndex()).hasItem()) {
                int i = 8 - equipmentslot.getIndex();
                if (!moveItemStackTo(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (equipmentslot == EquipmentSlot.OFFHAND && !slots.get(45).hasItem()) {
                if (!moveItemStackTo(itemstack1, 45, 46, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 9 && index < 36) {
                if (!moveItemStackTo(itemstack1, 36, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 36 && index < 45) {
                if (!moveItemStackTo(itemstack1, 9, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, 9, 45, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
            if (index == 0) {
                player.drop(itemstack1, false);
            }
        }

        return itemstack;
    }

    public boolean canTakeItemForPickAll(@Nonnull ItemStack itemStack, Slot slot) {
        return slot.container != craftResult && super.canTakeItemForPickAll(itemStack, slot);
    }

    public void removed(@NotNull Player player) {
        super.removed(player);
        clearContainer(player, craftMatrix);
    }

    public void slotsChanged(@NotNull Container inventory) {
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<CraftingRecipe> recipeOptional = serverPlayer.serverLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftMatrix, serverPlayer.level());
            if (recipeOptional.isPresent()) {
                CraftingRecipe recipe = recipeOptional.get();
                if (craftResult.setRecipeUsed(player.level(), serverPlayer, recipe))
                    itemStack = recipe.assemble(craftMatrix, serverPlayer.level().registryAccess());
            }

            craftResult.setItem(45, itemStack);
            setRemoteSlot(45, itemStack);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 45, itemStack));
        }

    }

    public boolean stillValid(@NotNull Player ignored) {
        return true;
    }
}