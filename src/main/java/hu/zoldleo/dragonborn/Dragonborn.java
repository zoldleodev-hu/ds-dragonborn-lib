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

package hu.zoldleo.dragonborn;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import hu.zoldleo.dragonborn.common.test.TestCaveDragonbornType;
import hu.zoldleo.dragonborn.common.test.TestDragonbornBody;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod(Dragonborn.MODID)
public class Dragonborn {
    public static final String MODID = "dragonborn_lib";
    public Dragonborn(IEventBus bus) {
        //TODO
        //region Test
        DragonTypes.registerType(TestCaveDragonbornType::new);
        DragonBodies.registerType(TestDragonbornBody::new);
        //endregion
        bus.addListener(Dragonborn::addRenderLayer);
    }

    @SubscribeEvent
    public static void addRenderLayer(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(skin -> {
            LivingEntityRenderer<Player, EntityModel<Player>> renderer = event.getSkin(skin);
            if (renderer == null)
                return;
            renderer.addLayer(new RenderLayer<>(renderer) {
                @Override
                public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull Player player, float v, float v1, float partialTicks, float v3, float v4, float v5) {
                    DragonStateHandler handler = DragonbornUtils.getHandler(player);
                    if (DragonbornUtils.isDragonborn(handler) && !player.isInvisible()) {
                        poseStack.pushPose();
                        poseStack.mulPose(Axis.XP.rotationDegrees(180f));
                        float scale = 1f / player.getScale();
                        poseStack.scale(scale, scale, scale);
                        DragonEntity dragon = ClientDragonRender.getDragon(player);
                        if (dragon != null)
                            Minecraft.getInstance().getEntityRenderDispatcher().render(dragon, 0, 0, 0, 0, partialTicks, poseStack, buffer, packedLight);
                        poseStack.popPose();
                    }
                }
            });
        });
    }
}