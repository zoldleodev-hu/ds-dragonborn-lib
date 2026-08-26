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
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftForm;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DragonModel.class)
public abstract class DragonModelMixin {
    @ModifyExpressionValue(method = "getModelResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;getModel()Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation getDragonbornModel(ResourceLocation original, @Local(name = "player") Player player) {
        if (player instanceof FakeClientPlayer fake && fake.handler.equals(DragonEditorScreen.HANDLER) && DragonEditorScreenAccessor.getForm() != null)
            return DragonEditorScreenAccessor.getForm().value().model();
        if (ShapeshiftForm.isTransformed(player))
            return ShapeshiftForm.getData(player).value().model();
        return original;
    }

    @ModifyExpressionValue(method = "getTextureResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;equals(Ljava/lang/Object;)Z"))
    private boolean useCustomSkin(boolean original, @Local(name = "handler") DragonStateHandler handler, @Local(name = "player") Player player) {
        return original || (handler.body().is(Dragonborn.CAN_USE_CUSTOM_SKIN) && !ShapeshiftForm.isTransformed(player)) || (ShapeshiftForm.isTransformed(player) && ShapeshiftForm.getData(player).value().canUseCustomSkin());
    }

    @Inject(method = "getAnimationResource(Lby/dragonsurvivalteam/dragonsurvival/common/entity/DragonEntity;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/client/models/DragonModel;getAnimationResource(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/resources/ResourceLocation;"), cancellable = true)
    private void getShapeshiftAnimation(DragonEntity dragon, CallbackInfoReturnable<ResourceLocation> cir, @Local(name = "player") Player player) {
        if (player instanceof FakeClientPlayer fake && fake.handler.equals(DragonEditorScreen.HANDLER) && DragonEditorScreenAccessor.getForm() != null)
            cir.setReturnValue(DragonEditorScreenAccessor.getForm().value().animation().withPrefix("animations/").withSuffix(".json"));
        else if (ShapeshiftForm.isTransformed(player))
            cir.setReturnValue(ShapeshiftForm.getData(player).value().animation().withPrefix("animations/").withSuffix(".json"));
    }
}