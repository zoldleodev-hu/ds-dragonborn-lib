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

import by.dragonsurvivalteam.dragonsurvival.client.render.blocks.DragonSoulRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.server.tileentity.DragonSoulBlockEntity;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import hu.zoldleo.dragonborn.client.DragonbornClientUtils;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DragonSoulRenderer.class)
public abstract class DragonSoulRendererMixin {
    @Inject(method = "render(Lby/dragonsurvivalteam/dragonsurvival/server/tileentity/DragonSoulBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At(value = "INVOKE_ASSIGN", target = "Lby/dragonsurvivalteam/dragonsurvival/client/util/FakeClientPlayerUtils;getNextIndex()I"))
    private void setFakeDragonbornSkin(DragonSoulBlockEntity soul, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay, CallbackInfo ci, @Local(name = "handler") DragonStateHandler handler) {
        if (DragonbornUtils.isDragonDragonborn(handler))
            DragonbornClientUtils.setFakeProfile(handler, soul.components().get(DataComponents.PROFILE));
    }

    @WrapOperation(method = "render(Lby/dragonsurvivalteam/dragonsurvival/server/tileentity/DragonSoulBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void renderFakeDragonborn(EntityRenderer<?> instance, Entity p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Operation<Void> original, @Local(name = "handler") DragonStateHandler handler, @Local(name = "player") FakeClientPlayer player) {
        if (DragonbornUtils.isDragonDragonborn(handler)) {
            player.yHeadRot = 0;
            Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player).render(player, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            return;
        }
        original.call(instance, p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}