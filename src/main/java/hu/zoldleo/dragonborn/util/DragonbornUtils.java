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
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import hu.zoldleo.dragonborn.Dragonborn;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class DragonbornUtils {
    public static boolean isDragonborn(DragonStateHandler handler) {
        return handler.isDragon() && handler.species().is(Dragonborn.DRAGONBORN_SPECIES);
    }

    public static boolean isDragonborn(Player player) {
        return isDragonborn(DragonStateProvider.getData(player));
    }

    public static boolean isDragonborn(@Nullable Entity entity) {
        return entity instanceof Player player && isDragonborn(DragonStateProvider.getData(player));
    }

    public static boolean isDragonDragonborn(DragonStateHandler handler) {
        return handler.species().is(Dragonborn.DRAGONBORN_SPECIES);
    }

    public static boolean isDragonDragonborn(Player player) {
        return DragonStateProvider.getData(player).species().is(Dragonborn.DRAGONBORN_SPECIES);
    }

    public static boolean isDragonDragonborn(@Nullable Entity entity) {
        return entity instanceof Player player && DragonStateProvider.getData(player).species().is(Dragonborn.DRAGONBORN_SPECIES);
    }

    public static boolean humanCraftingGrid(Player player) {
        Holder<DragonSpecies> species = DragonStateProvider.getData(player).species();
        return species != null && species.is(Dragonborn.HUMAN_CRAFTING_GRID);
    }

    public static boolean noClawSlots(Player player) {
        Holder<DragonSpecies> species = DragonStateProvider.getData(player).species();
        return species != null && species.is(Dragonborn.NO_CLAW_SLOTS);
    }

    public static boolean noClawSlots(Holder<DragonSpecies> species) {
        return species.is(Dragonborn.NO_CLAW_SLOTS);
    }

    public static boolean noClawSlotsDragon(DragonStateHandler handler) {
        return handler.species().is(Dragonborn.NO_CLAW_SLOTS);
    }
}