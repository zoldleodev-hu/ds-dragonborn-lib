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

package hu.zoldleo.dragonborn.client;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import com.mojang.authlib.GameProfile;
import hu.zoldleo.dragonborn.mixin.DragonStateHandlerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;

import javax.annotation.Nullable;

public class DragonbornClientUtils {
    public static void setFakeProfile(DragonStateHandler handler, @Nullable ResolvableProfile profile) {
        if (profile == null || !profile.isResolved()) // TODO: do I have to call resolve here?
            return;
        Minecraft.getInstance().getSkinManager().getOrLoad(profile.gameProfile()).thenAccept(skin -> {
            if (!skin.equals(getFakePlayerSkin(handler))) {
                ((DragonStateHandlerAccessor)handler).dragonborn$setFakeSkin(skin);
                handler.recompileCurrentSkin();
            }
        });
    }
    public static void setFakeProfile(DragonStateHandler handler, GameProfile profile) {
        setFakeProfile(handler, new ResolvableProfile(profile));
    }

    public static void setLocalFakeProfile(DragonStateHandler handler) {
        //noinspection DataFlowIssue
        setFakeProfile(handler, Minecraft.getInstance().player.getGameProfile());
    }

    public static PlayerSkin getFakePlayerSkin(DragonStateHandler handler) {
        return ((DragonStateHandlerAccessor)handler).dragonborn$getFakeSkin();
    }
}