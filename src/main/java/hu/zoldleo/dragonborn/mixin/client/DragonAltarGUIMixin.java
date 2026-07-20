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

import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonAltarGUI;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.AltarTypeButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HelpButton;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
//import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonType;
import hu.zoldleo.dragonborn.api.dragon_type.IBodyListProvider;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

@Mixin(value = DragonAltarGUI.class, remap = false)
public class DragonAltarGUIMixin extends Screen {
    /*/@Shadow
    public DragonStateHandler handler2;

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;clamp(JII)I", ordinal = 0))
    private int entity1Scale(int original) {
        if (DragonbornUtils.isDragonborn(handler1))
            return 40;
        return original;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;clamp(JII)I", ordinal = 1))
    private int entity2Scale(int original) {
        if (DragonbornUtils.isDragonborn(handler2))
            return 40;
        return original;
    }*/

    @Shadow
    private boolean hasInit;
    @Shadow
    public DragonStateHandler handler1;
    @Unique
    private List<AbstractDragonType> dragonborn$types = new ArrayList<>();
    @Unique
    private int dragonborn$scroll = 0; // [0; types.length - 4]
    @Unique
    List<AltarTypeButton> dragonborn$altarButtons = new ArrayList<>();

    protected DragonAltarGUIMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void customInit(CallbackInfo ci) {
        super.init();
        if (!hasInit)
            hasInit = true;

        dragonborn$types.clear();
        dragonborn$types.addAll(List.of(DragonTypes.CAVE, DragonTypes.FOREST, DragonTypes.SEA));
        dragonborn$types.addAll(DragonTypes.staticTypes.values().stream().filter( x ->
                !(DragonbornUtils.isDragonType(x, DragonTypes.CAVE) ||
                DragonbornUtils.isDragonType(x, DragonTypes.FOREST) ||
                DragonbornUtils.isDragonType(x, DragonTypes.SEA))).toList());
        /*/for (String type : DragonTypes.staticTypes.keySet().stream().sorted().toList())
            dragonborn$types.add(DragonTypes.getStatic(type));
        dragonborn$types.addAll(DataDrivenDragonType.getRegisteredDragonTypes());*/
        dragonborn$types.add(null);

        DragonAltarGUI self = (DragonAltarGUI)(Object)this;

        addRenderableWidget(new HelpButton(width / 2, 37, 16, 16, "ds.help.altar", 1));
        dragonborn$altarButtons.clear();
        dragonborn$altarButtons.add(new AltarTypeButton(self, DragonTypes.CAVE, width / 2 - 104, height / 2 - 65));
        dragonborn$altarButtons.add(new AltarTypeButton(self, DragonTypes.FOREST, width / 2 - 51, height / 2 - 65));
        dragonborn$altarButtons.add(new AltarTypeButton(self, DragonTypes.SEA, width / 2 + 2, height / 2 - 65));
        dragonborn$altarButtons.add(new AltarTypeButton(self, null, width / 2 + 55, height / 2 - 65));
        addRenderableWidget(dragonborn$altarButtons.get(0));
        addRenderableWidget(dragonborn$altarButtons.get(1));
        addRenderableWidget(dragonborn$altarButtons.get(2));
        addRenderableWidget(dragonborn$altarButtons.get(3));
        addRenderableWidget(new ExtendedButton(self.width / 2 - 75, height - 25, 150, 20, Component.translatable("ds.gui.dragon_editor"), (btn) -> Minecraft.getInstance().setScreen(new DragonEditorScreen(Minecraft.getInstance().screen))) {
            public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                Minecraft mc = DragonAltarGUIMixin.this.minecraft;
                visible = DragonUtils.isDragon(mc != null ? mc.player : null);
                super.render(guiGraphics, mouseX, mouseY, partialTick);
            }
        });
        addRenderableWidget(new Button(width / 2 + 105, height / 2 - 5, 15, 15, Component.empty(), button -> dragonborn$scrollTypes(1), Supplier::get) {
            public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
                active = visible = dragonborn$types.size() > 4;
                super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
            }

            public void renderWidget(@NotNull GuiGraphics guiGraphics, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
                if (isHoveredOrFocused()) {
                    guiGraphics.blit(ClientMagicHUDHandler.widgetTextures, getX(), getY(), 33.0F, 111.0F, 11, 17, 128, 128);
                } else {
                    guiGraphics.blit(ClientMagicHUDHandler.widgetTextures, getX(), getY(), 22.0F, 111.0F, 11, 17, 128, 128);
                }

            }
        });
        addRenderableWidget(new Button(width / 2 - 117, height / 2 - 5, 15, 15, Component.empty(), button -> dragonborn$scrollTypes(-1), Supplier::get) {
            public void render(@NotNull GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTicks) {
                active = visible = dragonborn$types.size() > 4;
                super.render(guiGraphics, pMouseX, pMouseY, pPartialTicks);
            }

            public void renderWidget(@NotNull GuiGraphics guiGraphics, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
                RenderSystem.setShaderTexture(0, ClientMagicHUDHandler.widgetTextures);
                if (isHoveredOrFocused()) {
                    guiGraphics.blit(ClientMagicHUDHandler.widgetTextures, getX(), getY(), 11.0F, 111.0F, 11, 17, 128, 128);
                } else {
                    guiGraphics.blit(ClientMagicHUDHandler.widgetTextures, getX(), getY(), 0.0F, 111.0F, 11, 17, 128, 128);
                }

            }
        });
        ci.cancel();
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/DragonAltarGUI;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 1))
    private GuiEventListener renderFirstAltarButton(DragonAltarGUI instance, GuiEventListener guiEventListener, Operation<GuiEventListener> original) {
        if (!dragonborn$types.isEmpty())
            return original.call(instance, guiEventListener);
        return null;
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/DragonAltarGUI;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 2))
    private GuiEventListener renderSecondAltarButton(DragonAltarGUI instance, GuiEventListener guiEventListener, Operation<GuiEventListener> original) {
        if (dragonborn$types.size() >= 2)
            return original.call(instance, guiEventListener);
        return null;
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/DragonAltarGUI;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 3))
    private GuiEventListener renderThirdAltarButton(DragonAltarGUI instance, GuiEventListener guiEventListener, Operation<GuiEventListener> original) {
        if (dragonborn$types.size() >= 3)
            return original.call(instance, guiEventListener);
        return null;
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/DragonAltarGUI;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 4))
    private GuiEventListener renderFourthAltarButton(DragonAltarGUI instance, GuiEventListener guiEventListener, Operation<GuiEventListener> original) {
        if (dragonborn$types.size() >= 4)
            return original.call(instance, guiEventListener);
        return null;
    }

    @ModifyVariable(method = "render", at = @At("STORE"), name = "button")
    private AltarTypeButton setType(AltarTypeButton button) {
        int index = dragonborn$scroll + renderables.indexOf(button) - 1;
        if (index < dragonborn$types.size())
            button.type = dragonborn$types.get(index);
        return button;
    }

    @Unique
    private void dragonborn$scrollTypes(int amount) {
        dragonborn$scroll = Math.min(Math.max(dragonborn$scroll + amount, 0), Math.max(0, dragonborn$types.size() - 4));
        for (int i = 0; i < dragonborn$altarButtons.size(); i++)
            if (dragonborn$scroll + i < dragonborn$types.size())
                dragonborn$altarButtons.get(i).type = dragonborn$types.get(dragonborn$scroll + i);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;setBody(Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/AbstractDragonBody;)V", ordinal = 0))
    private AbstractDragonBody setFirstBody(AbstractDragonBody body) {
        if (handler1.getType() instanceof IBodyListProvider provider)
            return DragonBodies.staticBodies.get(provider.getBodies().get(0));
        return body;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/HashMap;get(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    private Object validateBody(Object key) {
        if (handler1.getType() instanceof IBodyListProvider provider) {
            if (!provider.getBodies().contains((String)key))
                return provider.getBodies().get(0);
            return key;
        }
        if (!List.of(DragonBodies.ORDER).contains(((String)key).toUpperCase(Locale.ENGLISH)))
            return "center";
        return key;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;setBody(Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/AbstractDragonBody;)V", ordinal = 2))
    private AbstractDragonBody randomBody(AbstractDragonBody body) {
        if (handler1.getType() instanceof IBodyListProvider provider)
            return DragonBodies.staticBodies.get(provider.getBodies().get((int)(Math.random() * provider.getBodies().size())));
        return DragonBodies.getStatic(DragonBodies.ORDER[(int)(Math.random() * DragonBodies.ORDER.length)]);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateHandler;setType(Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/AbstractDragonType;)V"))
    private void validateBodies(DragonStateHandler instance, AbstractDragonType type, Operation<Void> original) {
        original.call(instance, type);
        if (type instanceof IBodyListProvider provider) {
            if (instance.getBody() == null || !provider.getBodies().contains(instance.getBody().getBodyName()))
                instance.setBody(DragonBodies.staticBodies.get(provider.getBodies().get(0)));
            return;
        }
        if (instance.getBody() == null || !List.of(DragonBodies.ORDER).contains(instance.getBody().getBodyName().toUpperCase(Locale.ENGLISH)))
            instance.setBody(DragonBodies.CENTER);
    }
}