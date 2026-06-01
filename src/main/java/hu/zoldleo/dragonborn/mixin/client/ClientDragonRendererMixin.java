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

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import by.dragonsurvivalteam.dragonsurvival.util.AnimationUtils;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender.setDragonMovementData;

@Mixin(value = ClientDragonRender.class, remap = false)
public class ClientDragonRendererMixin {
    @SuppressWarnings("all")
    @ModifyExpressionValue(method = "thirdPersonPreRender", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;isDragon()Z"))
    private static boolean cancelDragonRender(boolean original, @Local(argsOnly = true) RenderPlayerEvent.Pre event, @Local(name = "dragon") DragonEntity dragon, @Local(name = "player") AbstractClientPlayer player) {
        if (original && DragonbornUtils.isDragonDragonborn(player)) {
            if (!dragon.isInInventory && player != Minecraft.getInstance().player || !Minecraft.getInstance().options.getCameraType().isFirstPerson() || !ServerFlightHandler.isGliding(player) || ClientDragonRender.renderFirstPersonFlight)
                setDragonMovementData(player, AnimationUtils.getRealtimeDeltaTicks());
            return false;
        }
        return original;
    }

    @Inject(method = "lambda$setDragonMovementData$2", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;setMovementData(DDDLnet/minecraft/world/phys/Vec3;)V"), cancellable = true)
    private static void correctYawForDragonborn(Player player, float realtimeDeltaTick, DragonStateHandler playerStateHandler, CallbackInfo ci, @Local(name = "moveVector") Vec3 moveVector) {
        if (DragonbornUtils.isDragonDragonborn(player)) {
            playerStateHandler.setMovementData(player.yBodyRot, player.yHeadRot, player.getViewXRot(realtimeDeltaTick), moveVector);
            ci.cancel();
        }
    }
}