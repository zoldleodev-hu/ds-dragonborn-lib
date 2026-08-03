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

package hu.zoldleo.dragonborn.client;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.GrowthCrystalButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.TabButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.HoverButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.components.BarComponent;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscResources;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.StageResources;
import by.dragonsurvivalteam.dragonsurvival.network.container.RequestOpenVanillaInventory;
import by.dragonsurvivalteam.dragonsurvival.network.container.SortInventory;
import by.dragonsurvivalteam.dragonsurvival.registry.datagen.Translation;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.stage.DragonStage;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import hu.zoldleo.dragonborn.server.DragonbornContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforgespi.language.IModInfo;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

import static by.dragonsurvivalteam.dragonsurvival.client.gui.screens.DragonInventoryScreen.mouseX;
import static by.dragonsurvivalteam.dragonsurvival.client.gui.screens.DragonInventoryScreen.mouseY;

public class DragonbornInventoryScreen extends EffectRenderingInventoryScreen<DragonbornContainer> {
    private static final String SORT_INVENTORY;
    private static final String TOGGLE_VANILLA_INVENTORY;
    private static final String TOGGLE_CONFIG;
    private static final ResourceLocation BACKGROUND;
    private static final ResourceLocation CONFIG_HOVER;
    private static final ResourceLocation CONFIG_MAIN;
    private static final ResourceLocation VANILLA_INVENTORY_HOVER;
    private static final ResourceLocation VANILLA_INVENTORY_MAIN;
    private static final ResourceLocation SORT_HOVER;
    private static final ResourceLocation SORT_MAIN;
    private HoverButton growthButton;
    private int growthTooltipScroll;
    private final Player player;

    public DragonbornInventoryScreen(DragonbornContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
        imageWidth = 203;
        imageHeight = 166;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.growthButton.isHovered()) {
            this.growthTooltipScroll += (int)(-scrollY);
        } else {
            this.growthTooltipScroll = 0;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    protected void init() {
        super.init();
        if (minecraft == null || minecraft.player == null || minecraft.screen == null)
            return;

        if (mouseX != -1 && mouseY != -1) {
            InputConstants.grabOrReleaseMouse(minecraft.getWindow().getWindow(), 212993, mouseX, mouseY);
            mouseX = -1;
            mouseY = -1;
        }

        leftPos = (width - imageWidth) / 2;
        TabButton.addTabButtonsToScreen(this, leftPos + 5, topPos - 26, TabButton.TabButtonType.INVENTORY_TAB);
        DragonStateHandler data = DragonStateProvider.getData(minecraft.player);
        StageResources.GrowthIcon growthIcon = StageResources.getGrowthIcon(data.species(), data.stageKey());
        growthButton = new HoverButton(leftPos + 175, topPos + 4, 20, growthIcon.icon(), growthIcon.hoverIcon(), () -> {
            DragonStateHandler handler = DragonStateProvider.getData(minecraft.player);
            Pair<List<Either<FormattedText, TooltipComponent>>, Integer> growthDescriptionResult = handler.getGrowthDescription(growthTooltipScroll);
            List<Either<FormattedText, TooltipComponent>> components = growthDescriptionResult.getFirst();
            growthTooltipScroll = growthDescriptionResult.getSecond();
            return components;
        });
        addRenderableWidget(growthButton);
        List<Holder<DragonStage>> stages = data.getStagesSortedByProgression(minecraft.player.registryAccess());
        if (!stages.isEmpty()) {
            List<GrowthCrystalButton> crystals = stages.stream().map((stage) -> new GrowthCrystalButton(0, 0, stage)).toList();
            MiscResources textures = (data.species().value()).miscResources();
            new BarComponent(this, leftPos + 124, topPos + 6, 4, crystals, 2, -11, 39, 1, 12, 16, textures.growthLeftArrow().hoverIcon(), textures.growthLeftArrow().icon(), textures.growthRightArrow().hoverIcon(), textures.growthRightArrow().icon());
        }

        HoverButton vanillaInventoryButton = new HoverButton(leftPos + 177, topPos + 84, 18, 16, 18, 18, VANILLA_INVENTORY_MAIN, VANILLA_INVENTORY_HOVER, (button) -> {
            Minecraft.getInstance().setScreen(new InventoryScreen(player));
            PacketDistributor.sendToServer(RequestOpenVanillaInventory.INSTANCE);
        });
        vanillaInventoryButton.setTooltip(Tooltip.create(Component.translatable(TOGGLE_VANILLA_INVENTORY)));
        addRenderableWidget(vanillaInventoryButton);
        HoverButton configButton = getHoverButton();
        configButton.setTooltip(Tooltip.create(Component.translatable(TOGGLE_CONFIG)));
        this.addRenderableWidget(configButton);
        HoverButton sortInventoryButton = new HoverButton(this.leftPos + 177, this.topPos + 120, 18, 16, 18, 18, SORT_MAIN, SORT_HOVER, (button) -> PacketDistributor.sendToServer(SortInventory.INSTANCE));
        sortInventoryButton.setTooltip(Tooltip.create(Component.translatable(SORT_INVENTORY)));
        this.addRenderableWidget(sortInventoryButton);
    }

    private @Nonnull HoverButton getHoverButton() {
        HoverButton configButton = new HoverButton(leftPos + 177, topPos + 102, 18, 16, 18, 18, CONFIG_MAIN, CONFIG_HOVER, (button) -> {
            Minecraft minecraft = Minecraft.getInstance();
            Optional<Screen> configScreen = ModList.get().getModContainerById("dragonsurvival").flatMap((m) -> {
                IModInfo modInfo = m.getModInfo();
                return IConfigScreenFactory.getForMod(modInfo).map((f) -> {
                    assert minecraft.screen != null;
                    return f.createScreen(m, minecraft.screen);
                });
            });
            minecraft.setScreen(configScreen.orElse(null));
        });
        configButton.setTooltip(Tooltip.create(Component.translatable(TOGGLE_CONFIG)));
        return configButton;
    }

    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        RenderSystem.disableBlend();
        int scissorY1 = topPos + 77;
        int scissorX1 = leftPos + 101;
        int scissorX0 = leftPos + 25;
        int scissorY0 = topPos + 8;
        int scale = (int)(20.0F * player.getScale());
        scale = Math.clamp(scale, 10, 40);
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, scissorX0, scissorY0, scissorX1, scissorY1, scale, 0.0F, (float)mouseX, (float)mouseY, player);
    }

    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
        this.growthButton.renderTooltip(graphics, mouseX, mouseY);
    }

    static {
        SORT_INVENTORY = Translation.Type.GUI.wrap("dragon_inventory.sort_inventory");
        TOGGLE_VANILLA_INVENTORY = Translation.Type.GUI.wrap("dragon_inventory.toggle_vanilla_inventory");
        TOGGLE_CONFIG = Translation.Type.GUI.wrap("inventory.toggle_config");
        BACKGROUND = ResourceLocation.fromNamespaceAndPath("dragonsurvival", "textures/gui/inventory/dragon_inventory_alt.png");
        CONFIG_HOVER = ResourceLocation.fromNamespaceAndPath("dragonsurvival", "textures/gui/inventory/config_hover.png");
        CONFIG_MAIN = ResourceLocation.fromNamespaceAndPath("dragonsurvival", "textures/gui/inventory/config_main.png");
        VANILLA_INVENTORY_HOVER = ResourceLocation.fromNamespaceAndPath("dragonsurvival", "textures/gui/inventory/vanilla_inventory_hover.png");
        VANILLA_INVENTORY_MAIN = ResourceLocation.fromNamespaceAndPath("dragonsurvival", "textures/gui/inventory/vanilla_inventory_main.png");
        SORT_HOVER = ResourceLocation.fromNamespaceAndPath("dragonsurvival", "textures/gui/inventory/sort_hover.png");
        SORT_MAIN = ResourceLocation.fromNamespaceAndPath("dragonsurvival", "textures/gui/inventory/sort_main.png");
        mouseX = -1;
        mouseY = -1;
    }
}