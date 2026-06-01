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
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DragonStateHandler.class)
public interface DragonStateHandlerAccessor { // Dynamic Accessors aren't working for some reason
    /*/@Accessor(value = "dragonborn$fakeSkinModelName", remap = false)
    @Dynamic
    String dragonborn$getFakeSkinModelName();

    @Accessor(value = "dragonborn$fakeSkinTexture", remap = false)
    @Dynamic
    ResourceLocation dragonborn$getFakeSkinTexture();

    @Accessor(value = "dragonborn$fakeSkinModelName", remap = false)
    @Dynamic
    void dragonborn$setFakeSkinModelName(String skin);

    @Accessor(value = "dragonborn$fakeSkinTexture", remap = false)
    @Dynamic
    void dragonborn$setFakeSkinTexture(ResourceLocation skin);*/
}