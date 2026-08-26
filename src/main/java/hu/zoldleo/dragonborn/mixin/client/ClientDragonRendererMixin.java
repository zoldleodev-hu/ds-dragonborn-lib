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

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftForm;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer.setDragonMovementData;

@Mixin(ClientDragonRenderer.class)
public class ClientDragonRendererMixin {
    @Inject(method = "renderDragon", at = @At(value = "INVOKE_ASSIGN", target = "Lby/dragonsurvivalteam/dragonsurvival/client/render/ClientDragonRenderer;getOrCreateDragon(Lnet/minecraft/world/entity/player/Player;)Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;"), cancellable = true)
    private static void cancelDragonRender(RenderPlayerEvent.Pre event, CallbackInfo ci, @Local(name = "dragon") DragonEntity dragon, @Local(name = "player") AbstractClientPlayer player, @Local(name = "handler") DragonStateHandler handler) {
        if (DragonbornUtils.isDragonDragonborn(handler) && !ShapeshiftForm.isTransformed(player)) {
            dragon.renderingWasCancelled = true;
            event.setCanceled(false);
            if (!dragon.isInInventory && player != Minecraft.getInstance().player || !Minecraft.getInstance().options.getCameraType().isFirstPerson() || !ServerFlightHandler.isGliding(player) || ClientDragonRenderer.renderFirstPersonFlight)
                setDragonMovementData(player, Minecraft.getInstance().getTimer().getRealtimeDeltaTicks());
            ci.cancel();
        }
    }

    @Inject(method = "setDragonMovementData", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/attachments/MovementData;set(DDDLnet/minecraft/world/phys/Vec3;)V"), cancellable = true)
    private static void correctYawForDragonborn(Player player, float realtimeDeltaTick, CallbackInfo ci, @Local(name = "movement") MovementData movement, @Local(name = "moveVector") Vec3 moveVector) {
        if (DragonbornUtils.isDragonDragonborn(player) && !ShapeshiftForm.isTransformed(player)) {
            movement.set(player.yBodyRot, player.yHeadRot, player.getViewXRot(realtimeDeltaTick), moveVector);
            ci.cancel();
        }
    }
}