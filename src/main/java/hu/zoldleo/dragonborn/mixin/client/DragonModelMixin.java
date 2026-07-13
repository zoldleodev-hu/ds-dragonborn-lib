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
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.api.dragon_body.ICustomAnimationProvider;
import hu.zoldleo.dragonborn.api.dragon_body.ICustomModelProvider;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DragonModel.class, remap = false)
public abstract class DragonModelMixin {
    @ModifyReturnValue(method = "getModelResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"))
    private ResourceLocation customModel(ResourceLocation original, @Local(argsOnly = true) DragonEntity dragon) {
        DragonStateHandler handler = DragonbornUtils.getHandler(dragon.getPlayer());

        if (!(handler.getBody() instanceof ICustomModelProvider provider))
            return original;

        if (!DragonbornUtils.isDragonborn(handler))
            return dragonborn$wrap(provider.modelResource());

        if (dragon.getPlayer() instanceof FakeClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer) dragon.getPlayer();
            if (handler == DragonEditorScreen.handler && Minecraft.getInstance().player != null)
                player = Minecraft.getInstance().player;

            if (player.getModelName().equals("slim"))
                return dragonborn$wrap(provider.modelResource().withSuffix("_slim"));
            return dragonborn$wrap(provider.modelResource().withSuffix("_wide"));
        }
        return dragonborn$wrap(provider.modelResource().withSuffix("_extras"));
    }

    @Inject(method = "getAnimationResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "INVOKE_ASSIGN", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;getBody()Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/AbstractDragonBody;"), cancellable = true)
    private void customAnimation(DragonEntity dragon, CallbackInfoReturnable<ResourceLocation> cir, @Local(name = "body") AbstractDragonBody body) {
        if (body instanceof ICustomAnimationProvider provider)
            cir.setReturnValue(provider.animResource().withPrefix("animations/").withSuffix(".json"));
    }

    @Unique
    private static ResourceLocation dragonborn$wrap(ResourceLocation loc) {
        return loc.withPrefix("geo/").withSuffix(".geo.json");
    }

    /*/@ModifyReturnValue(method = "getTextureResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"))
    private ResourceLocation test(ResourceLocation original) {
        return new ResourceLocation("dragonsurvival", "textures/white.png");
    }*/
}