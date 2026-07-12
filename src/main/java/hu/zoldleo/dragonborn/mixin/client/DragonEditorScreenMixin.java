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
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons.DragonBodyButton;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import hu.zoldleo.dragonborn.api.dragon_type.IBodyListProvider;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Locale;

@Mixin(value = DragonEditorScreen.class, remap = false)
public abstract class DragonEditorScreenMixin extends Screen {
    @Shadow
    private boolean isEditor;

    protected DragonEditorScreenMixin(Component p_96550_) {
        super(p_96550_);
    }

    @Unique
    private DragonEditorScreen dragonborn$self() {
        return (DragonEditorScreen)(Object)this;
    }

    @Inject(method = "initialize", at = @At("RETURN"))
    private void dontZoomDragonborn(DragonStateHandler localHandler, CallbackInfo ci) {
        if (DragonbornUtils.isDragonborn(localHandler))
            dragonborn$self().dragonRender.zoom = 32;
    }

    @ModifyArg(method = "initialize", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/DragonBodies;getStatic(Ljava/lang/String;)Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/AbstractDragonBody;"))
    private String getFirstBody(String name) {
        if (dragonborn$self().dragonType instanceof IBodyListProvider provider)
            return provider.getBodies().get(0);
        return name;
    }

    @ModifyExpressionValue(method = "initialize", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;getBody()Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/AbstractDragonBody;"))
    private AbstractDragonBody validateAndSwapBody(AbstractDragonBody original) {
        if (original == null)
            return null;
        if (dragonborn$self().dragonType instanceof IBodyListProvider provider) {
            if (!provider.getBodies().contains(original.getBodyName()))
                return DragonBodies.getStatic(provider.getBodies().get(0));
        } else if (!List.of(DragonBodies.ORDER).contains(original.getBodyName().toUpperCase(Locale.ENGLISH)))
            return DragonBodies.getStatic("center");
        return original;
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/dragon_editor/DragonEditorScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 3))
    private GuiEventListener skipDefaultBodies(DragonEditorScreen instance, GuiEventListener guiEventListener, Operation<GuiEventListener> original) {
        return guiEventListener;
    }

    @Inject(method = "init", at = @At(value = "INVOKE_ASSIGN", target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/dragon_editor/DragonEditorScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 2))
    private void addCustomBodies(CallbackInfo ci) {
        if (dragonborn$self().dragonType instanceof IBodyListProvider provider)
            for (int i = 0; i < provider.getBodies().size(); i++)
                addRenderableWidget(new DragonBodyButton(dragonborn$self(), width / 2 - 71 + i * 27, height / 2 + 69, 25, 25, DragonBodies.getStatic(provider.getBodies().get(i)), i, isEditor));
        else
            for (int i = 0; i < DragonBodies.ORDER.length; i++)
                addRenderableWidget(new DragonBodyButton(dragonborn$self(), width / 2 - 71 + i * 27, height / 2 + 69, 25, 25, DragonBodies.getStatic(DragonBodies.ORDER[i]), i, isEditor));
    }
}