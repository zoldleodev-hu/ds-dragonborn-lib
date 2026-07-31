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

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

@Mixin(value = DragonRenderer.class, remap = false)
public class DragonRendererMixin {
    @Inject(method = "preRender(Lcom/mojang/blaze3d/vertex/PoseStack;Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V", at = @At("HEAD"))
    private void dragonbornAttachmentPoints(PoseStack poseStack, DragonEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (animatable.getPlayer() instanceof AbstractClientPlayer player && DragonbornUtils.isDragonborn(player)) {
            Optional<GeoBone> headBone = model.getBone("Head");
            Optional<GeoBone> bodyBone = model.getBone("Body");
            if (headBone.isPresent()) {
                ModelPart root = ((PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player)).getModel().head;
                dragonborn$copyTRSData(root, headBone.get());
            }
            if (bodyBone.isPresent()) {
                ModelPart root = ((PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player)).getModel().body;
                dragonborn$copyTRSData(root, bodyBone.get());
            }
        }
    }

    @Unique
    private void dragonborn$copyTRSData(ModelPart root, GeoBone bone) {
        bone.setPosX(root.x);
        bone.setPosY(-root.y);
        bone.setPosZ(root.z);
        bone.setRotX(-root.xRot);
        bone.setRotY(-root.yRot);
        bone.setRotZ(root.zRot);
        bone.setScaleX(root.xScale);
        bone.setScaleY(root.yScale);
        bone.setScaleZ(root.zScale);
    }
}