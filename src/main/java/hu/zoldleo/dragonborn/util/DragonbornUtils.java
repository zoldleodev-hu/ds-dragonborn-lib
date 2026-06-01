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

package hu.zoldleo.dragonborn.util;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;

public class DragonbornUtils extends DragonUtils {
    public static boolean isDragonborn(DragonStateHandler handler) {
        return isSpeciesDragonborn(handler.getType());
    }

    public static boolean isDragonborn(Player player) {
        return isDragon(player) && isDragonborn(getHandler(player));
    }

    public static boolean isDragonborn(@Nullable Entity entity) {
        return entity instanceof Player player && isDragonborn(getHandler(player));
    }

    public static boolean isDragonDragonborn(DragonStateHandler handler) {
        return isSpeciesDragonborn(handler.getType());
    }

    public static boolean isSpeciesDragonborn(AbstractDragonType species) {
        return species instanceof DataDrivenDragonType type && type.isDragonborn;
    }

    public static boolean isDragonDragonborn(Player player) {
        return isDragonDragonborn(getHandler(player));
    }

    @SuppressWarnings("all")
    public static boolean isDragonDragonborn(@Nullable Entity entity) {
        return entity instanceof Player player && isDragonDragonborn(getHandler(player));
    }

    public static void reInsertClawTools(final Player player) {
        SimpleContainer clawsContainer = getHandler(player).getClawToolData().getClawsInventory();
        for (int i = 0; i < 4; i++) {
            ItemStack stack = clawsContainer.getItem(i);
            if (player instanceof ServerPlayer serverPlayer && !serverPlayer.addItem(stack))
                serverPlayer.level().addFreshEntity(new ItemEntity(serverPlayer.level(), serverPlayer.position().x, serverPlayer.position().y, serverPlayer.position().z, stack));
        }
        clawsContainer.clearContent();
    }

    public static <T> HolderLookup.@Nullable RegistryLookup<T> resolveLookup(ResourceKey<? extends Registry<T>> key) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
            return server.registryAccess().lookup(key).orElse(null);
        if (!FMLEnvironment.dist.isClient())
            return null;
        ClientLevel level = Minecraft.getInstance().level;
        return level != null ? level.registryAccess().lookup(key).orElse(null) : null;
    }

    public static <T> Registry<T> resolveRegistry(ResourceKey<? extends Registry<T>> key) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
            return server.registryAccess().registry(key).orElse(null);
        if (!FMLEnvironment.dist.isClient())
            return null;
        ClientLevel level = Minecraft.getInstance().level;
        return level != null ? level.registryAccess().registry(key).orElse(null) : null;
    }

    public static NumberFormat numberFormat(int digits) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(digits);
        return format;
    }
}