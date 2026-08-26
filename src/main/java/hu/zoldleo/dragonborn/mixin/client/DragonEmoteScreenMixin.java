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

package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.DragonEmoteScreen;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.emotes.DragonEmote;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.emotes.DragonEmoteSet;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftForm;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(DragonEmoteScreen.class)
public class DragonEmoteScreenMixin {
    @ModifyExpressionValue(method = "reinitializeEmoteComponents", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/dragon/body/emotes/DragonEmoteSet;emotes()Ljava/util/List;"))
    private List<DragonEmote> initTransformedEmotes(List<DragonEmote> original) {
        Player player = Minecraft.getInstance().player;
        //noinspection DataFlowIssue -> local player should not be null when the screen is open
        if (ShapeshiftForm.isTransformed(player))
            return ShapeshiftForm.getData(player).value().emotes().value().emotes();
        return original;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/dragon/body/emotes/DragonEmoteSet;emotes()Ljava/util/List;"))
    private List<DragonEmote> transformedEmoteSetSize(List<DragonEmote> original) {
        Player player = Minecraft.getInstance().player;
        //noinspection DataFlowIssue -> local player should not be null when the screen is open
        if (ShapeshiftForm.isTransformed(player))
            return ShapeshiftForm.getData(player).value().emotes().value().emotes();
        return original;
    }

    @ModifyExpressionValue(method = "mouseScrolled", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/dragon/body/emotes/DragonEmoteSet;emotes()Ljava/util/List;"))
    private List<DragonEmote> transformedEmoteSetSizeForScroll(List<DragonEmote> original) {
        Player player = Minecraft.getInstance().player;
        //noinspection DataFlowIssue -> local player should not be null when the screen is open
        if (ShapeshiftForm.isTransformed(player))
            return ShapeshiftForm.getData(player).value().emotes().value().emotes();
        return original;
    }

    @ModifyExpressionValue(method = "addEmote", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/dragon/body/DragonBody;emotes()Lnet/minecraft/core/Holder;"))
    private static Holder<DragonEmoteSet> addTransformedEmote(Holder<DragonEmoteSet> original) {
        Player player = Minecraft.getInstance().player;
        //noinspection DataFlowIssue -> local player should not be null when the screen is open
        if (ShapeshiftForm.isTransformed(player))
            return ShapeshiftForm.getData(player).value().emotes();
        return original;
    }
}