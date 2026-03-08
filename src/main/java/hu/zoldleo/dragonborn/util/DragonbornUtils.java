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
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import hu.zoldleo.dragonborn.Dragonborn;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class DragonbornUtils {
    public static final DragonStateHandler emptyHandler = new DragonStateHandler();

    public static boolean isDragonborn(DragonStateHandler handler) {
        return handler.isDragon() && handler.species().is(Dragonborn.DRAGONBORN_SPECIES);
    }

    public static boolean isDragonborn(Player player) {
        return isDragonborn(player.getData(DSDataAttachments.DRAGON_HANDLER));
    }

    public static boolean isDragonborn(@Nullable Entity entity) {
        return entity instanceof Player player && isDragonborn(player.getData(DSDataAttachments.DRAGON_HANDLER));
    }

    public static boolean isDragonDragonborn(DragonStateHandler handler) {
        return handler.species().is(Dragonborn.DRAGONBORN_SPECIES);
    }

    public static boolean isDragonDragonborn(Player player) {
        return isDragonDragonborn(player.getData(DSDataAttachments.DRAGON_HANDLER));
    }

    public static boolean isDragonDragonborn(@Nullable Entity entity) {
        return entity instanceof Player player && isDragonDragonborn(player.getData(DSDataAttachments.DRAGON_HANDLER));
    }
}