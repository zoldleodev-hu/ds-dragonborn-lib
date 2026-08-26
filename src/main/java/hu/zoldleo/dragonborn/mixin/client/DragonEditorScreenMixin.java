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
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HoverButton;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayerUtils;
import by.dragonsurvivalteam.dragonsurvival.client.util.TextRenderUtil;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.util.Functions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import hu.zoldleo.dragonborn.client.DragonbornClientUtils;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftForm;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

@Mixin(DragonEditorScreen.class)
public abstract class DragonEditorScreenMixin extends Screen {
    @Shadow
    @Final
    public DragonEditorScreen.UndoRedoList actionHistory;

    @Shadow
    @Final
    private static ResourceLocation SMALL_LEFT_ARROW_MAIN;

    @Shadow
    @Final
    private static ResourceLocation SMALL_LEFT_ARROW_HOVER;

    @Shadow
    @Final
    private static ResourceLocation SMALL_RIGHT_ARROW_MAIN;

    @Shadow
    @Final
    private static ResourceLocation SMALL_RIGHT_ARROW_HOVER;

    @Unique
    private List<Holder<ShapeshiftForm>> dragonborn$forms = List.of();

    @Unique
    private int dragonborn$selectedForm;

    @Unique
    @Nullable
    private static Holder<ShapeshiftForm> dragonborn$form;

    @Unique
    private final Function<Holder<ShapeshiftForm>, Holder<ShapeshiftForm>> dragonborn$selectFormAction = newForm -> {
        Holder<ShapeshiftForm> previousForm = dragonborn$form;
        dragonborn$form = newForm;
        FakeClientPlayerUtils.getFakePlayer(0, DragonEditorScreen.HANDLER).animationController.forceAnimationReset();
        return previousForm;
    };

    protected DragonEditorScreenMixin(Component title) {
        super(title);
    }

    @ModifyReturnValue(method = "setZoom", at = @At("RETURN"))
    private static float dontZoomDragonborn(float original) {
        return DragonbornUtils.isDragonborn(DragonEditorScreen.HANDLER) ? 32 : original;
    }

    @ModifyExpressionValue(method = "lambda$initDragonRender$36", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/registry/dragon/body/DragonBody;noDragonModelRendering()Z"))
    private boolean renderFakeDragonborn(boolean original) {
        return original || (DragonbornUtils.isDragonDragonborn(DragonEditorScreen.HANDLER) && dragonborn$form == null);
    }

    @Inject(method = "initDummyDragon", at = @At("HEAD"))
    private void setLocalPlayerSkin(DragonStateHandler localHandler, CallbackInfo ci) {
        DragonbornClientUtils.setLocalFakeProfile(DragonEditorScreen.HANDLER);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/client/util/TextRenderUtil;drawCenteredScaledText(Lnet/minecraft/client/gui/GuiGraphics;IIFLjava/lang/String;I)V", ordinal = 0))
    private void drawShapeshiftFormText(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (dragonborn$forms.size() <= 1)
            return;
        TextRenderUtil.drawCenteredScaledText(graphics, width / 2, 60, 1, ShapeshiftForm.getName(dragonborn$form).getString(), DyeColor.WHITE.getTextColor());
    }

    @Inject(method = "init", at = @At(value = "INVOKE_ASSIGN", target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/screens/dragon_editor/DragonEditorScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 1))
    private void initShapeshiftFormArrows(CallbackInfo ci) {
        dragonborn$forms = ShapeshiftForm.collectDefaultForms(DragonEditorScreen.HANDLER);
        HoverButton leftArrow = new HoverButton(width / 2 - 50, 56, 10, 16, 10, 16, SMALL_LEFT_ARROW_MAIN, SMALL_LEFT_ARROW_HOVER, button -> {
            dragonborn$selectedForm = Functions.wrap(dragonborn$selectedForm - 1, 0, dragonborn$forms.size() - 1);
            actionHistory.add(new DragonEditorScreen.EditorAction<>(dragonborn$selectFormAction, dragonborn$forms.get(dragonborn$selectedForm)));
        });
        addRenderableWidget(leftArrow);
        HoverButton rightArrow = new HoverButton(width / 2 + 40, 56, 10, 16, 10, 16, SMALL_RIGHT_ARROW_MAIN, SMALL_RIGHT_ARROW_HOVER, button -> {
            dragonborn$selectedForm = Functions.wrap(dragonborn$selectedForm + 1, 0, dragonborn$forms.size() - 1);
            actionHistory.add(new DragonEditorScreen.EditorAction<>(dragonborn$selectFormAction, dragonborn$forms.get(dragonborn$selectedForm)));
        });
        addRenderableWidget(rightArrow);
    }
}