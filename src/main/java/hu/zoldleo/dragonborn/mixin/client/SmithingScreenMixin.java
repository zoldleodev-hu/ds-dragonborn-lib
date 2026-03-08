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

package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmithingScreen.class)
public class SmithingScreenMixin {
    @Unique
    DragonStateHandler dragonborn$tempHandler = null;
    private @Nullable DragonEntity dragonSurvival$dragon;

    @Inject(method = "subInit", at = @At("HEAD"))
    private void removeDragon(CallbackInfo ci) {
        if (Minecraft.getInstance().player instanceof LocalPlayer player) {
            dragonborn$tempHandler = player.getData(DSDataAttachments.DRAGON_HANDLER);
            if (DragonbornUtils.isDragonborn(dragonborn$tempHandler)) {
                player.setData(DSDataAttachments.DRAGON_HANDLER, DragonbornUtils.emptyHandler);
                if (dragonSurvival$dragon != null)
                    dragonSurvival$dragon = null;
            }
        }
    }

    @Inject(method = "subInit", at = @At("TAIL"))
    private void restoreDragon(CallbackInfo ci) {
        if (Minecraft.getInstance().player instanceof LocalPlayer player)
            player.setData(DSDataAttachments.DRAGON_HANDLER, dragonborn$tempHandler);
        System.out.println(dragonSurvival$dragon == null);
    }
}