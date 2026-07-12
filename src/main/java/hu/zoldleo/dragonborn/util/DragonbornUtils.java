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
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import hu.zoldleo.dragonborn.api.dragon_type.IDragonborn;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DragonbornUtils extends DragonUtils {
    public static boolean isDragonborn(DragonStateHandler handler) {
        return handler.getType() instanceof IDragonborn;
    }

    public static boolean isDragonborn(Player player) {
        return DragonStateProvider.getCap(player).filter(x -> x.getType() instanceof IDragonborn).isPresent();
    }

    public static boolean isDragonborn(@Nullable Entity entity) {
        return entity instanceof Player && DragonStateProvider.getCap(entity).filter(x -> x.getType() instanceof IDragonborn).isPresent();
    }

    public static boolean isSpeciesDragonborn(AbstractDragonType species) {
        return species instanceof IDragonborn;
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
}