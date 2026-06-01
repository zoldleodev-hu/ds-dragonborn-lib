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

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.models.DragonModel;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.mixin.DragonStateHandlerAccessor;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = DragonModel.class, remap = false)
public abstract class DragonModelMixin {
    @ModifyReturnValue(method = "getModelResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"))
    private ResourceLocation getDragonbornModel(ResourceLocation original, @Local(argsOnly = true) DragonEntity dragon) throws NoSuchFieldException, IllegalAccessException {
        if (dragon.getPlayer() instanceof AbstractClientPlayer player) {
            if (player instanceof FakeClientPlayer fake && DragonbornUtils.isDragonborn(fake.handler)) {
                if (fake.handler == DragonEditorScreen.handler && Minecraft.getInstance().player != null)
                    player = Minecraft.getInstance().player;

                String fakeSkin = (String)fake.handler.getClass().getField("dragonborn$fakeSkinModelName").get(fake.handler);
                String skin = (fakeSkin == null) ? player.getModelName() : fakeSkin;
                ResourceLocation fakeSkinTexture = (ResourceLocation)fake.handler.getClass().getField("dragonborn$fakeSkinTexture").get(fake.handler);
                if (skin.equals("slim"))
                    return fakeSkinTexture.withSuffix("_slim");
                return fakeSkinTexture.withSuffix("_wide");
            }
            else if (DragonbornUtils.isDragonborn(player)) {
                return original.withSuffix("_extras");
            }
        }
        return original;
    }

    @ModifyExpressionValue(method = "getTextureResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;getTypeNameLowerCase()Ljava/lang/String;"))
    private String getCustomTypeName(String original) {
        return new ResourceLocation(original).getPath();
    }

    @ModifyConstant(method = "getTextureResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", constant = @Constant(stringValue = "dragonsurvival"), slice = @Slice(from = @At("HEAD"), to = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;getSkinData()Lby/dragonsurvivalteam/dragonsurvival/common/capability/subcapabilities/SkinCap;", ordinal = 3)))
    private String setCustomTypeNamespace(String constant, @Local(name = "handler") DragonStateHandler handler) {
        ResourceLocation loc = new ResourceLocation(handler.getTypeNameLowerCase());
        return loc.getNamespace().equals("minecraft") ? "dragonsurvival" : loc.getNamespace();
    }
}