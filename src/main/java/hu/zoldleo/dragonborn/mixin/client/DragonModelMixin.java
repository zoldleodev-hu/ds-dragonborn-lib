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

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.models.DragonModel;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.mixin.DragonStateHandlerAccessor;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonModel.class)
public abstract class DragonModelMixin {
    @ModifyExpressionValue(method = "getModelResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;getModel()Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation getDragonbornModel(ResourceLocation original, @Local(argsOnly = true) DragonEntity dragon) {
        if (dragon.getPlayer() instanceof AbstractClientPlayer player) {
            if (player instanceof FakeClientPlayer fake && DragonbornUtils.isDragonborn(fake.handler)) {
                if (fake.handler == DragonEditorScreen.HANDLER && Minecraft.getInstance().player instanceof LocalPlayer local)
                    player = local;
                PlayerSkin fakeSkin = ((DragonStateHandlerAccessor)fake.handler).dragonborn$getFakeSkin();
                PlayerSkin skin = (fakeSkin == null) ? player.getSkin() : fakeSkin;
                if (skin.model() == PlayerSkin.Model.SLIM)
                    return fake.handler.getModel().withSuffix("_slim");
                return fake.handler.getModel().withSuffix("_wide");
            }
            else {
                DragonStateHandler handler = player.getData(DSDataAttachments.DRAGON_HANDLER);
                if (DragonbornUtils.isDragonborn(handler))
                    return original.withSuffix("_extras");
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "getTextureResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;equals(Ljava/lang/Object;)Z"))
    private boolean useCustomSkin(boolean original, @Local(name = "handler") DragonStateHandler handler) {
        return original || handler.body().is(Dragonborn.CAN_USE_CUSTOM_SKIN);
    }
}