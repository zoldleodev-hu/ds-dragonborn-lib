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

import by.dragonsurvivalteam.dragonsurvival.client.gui.settings.ConfigSideSelectionScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.TabButton;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.DSImageButton;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.ClientEvents;
import by.dragonsurvivalteam.dragonsurvival.client.util.RenderingUtils;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.config.ServerConfig;
import by.dragonsurvivalteam.dragonsurvival.network.NetworkHandler;
import by.dragonsurvivalteam.dragonsurvival.network.container.OpenInventory;
import by.dragonsurvivalteam.dragonsurvival.network.container.SortInventoryPacket;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.DragonUtils;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import hu.zoldleo.dragonborn.server.DragonbornContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;

public class DragonbornInventoryScreen extends EffectRenderingInventoryScreen<DragonbornContainer> {
    public static final ResourceLocation INVENTORY_TOGGLE_BUTTON = new ResourceLocation("dragonsurvival", "textures/gui/inventory_button.png");
    public static final ResourceLocation SORTING_BUTTON = new ResourceLocation("dragonsurvival", "textures/gui/sorting_button.png");
    public static final ResourceLocation SETTINGS_BUTTON = new ResourceLocation("dragonsurvival", "textures/gui/settings_button.png");
    static final ResourceLocation BACKGROUND = new ResourceLocation("dragonsurvival", "textures/gui/dragonborn_inventory.png");
    private final Player player;
    private boolean buttonClicked;
    private boolean isGrowthIconHovered;
    private static HashMap<String, ResourceLocation> textures;

    public DragonbornInventoryScreen(DragonbornContainer menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
        imageWidth = 203;
        imageHeight = 166;
    }

    private static void initResources() {
        textures = new HashMap<>();
        for(String key : DragonTypes.staticTypes.keySet()) {
            AbstractDragonType type = DragonTypes.staticTypes.get(key);
            String start = "textures/gui/growth/";
            String end = ".png";
            for(int i = 1; i <= DragonLevel.values().length; ++i) {
                String growthResource = createTextureKey(type, "growth", "_" + i);
                textures.put(growthResource, new ResourceLocation("dragonsurvival", start + growthResource + end));
            }
            String circleResource = createTextureKey(type, "circle", "");
            textures.put(circleResource, new ResourceLocation("dragonsurvival", start + circleResource + end));
        }
    }

    private static String createTextureKey(AbstractDragonType type, String textureType, String addition) {
        return textureType + "_" + type.getTypeNameLowerCase() + addition;
    }

    protected void init() {
        super.init();
        if (minecraft == null || minecraft.player == null || minecraft.screen == null)
            return;

        if (ClientEvents.mouseX != -1.0 && ClientEvents.mouseY != -1.0) {
            InputConstants.grabOrReleaseMouse(minecraft.getWindow().getWindow(), 212993, ClientEvents.mouseX, ClientEvents.mouseY);
            ClientEvents.mouseX = -1.0;
            ClientEvents.mouseY = -1.0;
        }

        leftPos = (width - imageWidth) / 2;
        addRenderableWidget(new TabButton(leftPos, topPos - 28, TabButton.TabType.INVENTORY, this));
        addRenderableWidget(new TabButton(leftPos + 28, topPos - 26, TabButton.TabType.ABILITY, this));
        addRenderableWidget(new TabButton(leftPos + 57, topPos - 26, TabButton.TabType.GITHUB_REMINDER, this));
        addRenderableWidget(new TabButton(leftPos + 86, topPos - 26, TabButton.TabType.SKINS, this));

        if (ClientEvents.inventoryToggle) {
            this.addRenderableWidget(new DSImageButton(this.leftPos + this.imageWidth - 28, this.height / 2 - 30 + 47, 20, 18, 0, 0, 19, INVENTORY_TOGGLE_BUTTON, (p_onPress_1_) -> {
                Minecraft.getInstance().setScreen(new InventoryScreen(this.player));
                NetworkHandler.CHANNEL.sendToServer(new OpenInventory());
            }, Component.translatable("ds.gui.toggle_inventory.vanilla")));
        }

        this.addRenderableWidget(new DSImageButton(this.leftPos + this.imageWidth - 28, this.height / 2 - 1, 20, 18, 0, 0, 18, SORTING_BUTTON, (p_onPress_1_) -> NetworkHandler.CHANNEL.sendToServer(new SortInventoryPacket()), Component.translatable("ds.gui.sort")));
        this.addRenderableWidget(new DSImageButton(this.leftPos + this.imageWidth - 28, this.height / 2 + 35, 20, 18, 0, 0, 18, SETTINGS_BUTTON, (p_onPress_1_) -> Minecraft.getInstance().setScreen(new ConfigSideSelectionScreen(this, Minecraft.getInstance().options, Component.translatable("ds.gui.tab_button.4"))), Component.translatable("ds.gui.tab_button.4")));

    }

    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        RenderSystem.disableBlend();
        DragonStateHandler handler = DragonUtils.getHandler(player);
        double renderedSize = Math.min(handler.getSize(), ServerConfig.DEFAULT_MAX_GROWTH_SIZE) / (double)6.0F;
        int scissorY1 = topPos + 75 - 50 - mouseY;
        int scissorX1 = leftPos + 51 - mouseX;
        int scissorX0 = leftPos + 65;
        int scissorY0 = topPos + 75 + (int)(renderedSize * 1.25);
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, scissorX0, scissorY0, (int)renderedSize + 15, scissorX1, scissorY1, player);

        if (textures == null || textures.isEmpty())
            initResources();

        double curSize = handler.getSize();
        float progress = 0.0F;
        if (handler.getLevel() == DragonLevel.NEWBORN) {
            progress = (float)((curSize - (double)DragonLevel.NEWBORN.size) / (double)(DragonLevel.YOUNG.size - DragonLevel.NEWBORN.size));
        } else if (handler.getLevel() == DragonLevel.YOUNG) {
            progress = (float)((curSize - (double)DragonLevel.YOUNG.size) / (double)(DragonLevel.ADULT.size - DragonLevel.YOUNG.size));
        } else if (handler.getLevel() == DragonLevel.ADULT && handler.getSize() < 40.0) {
            progress = (float)((curSize - (double)DragonLevel.ADULT.size) / (double)(40 - DragonLevel.ADULT.size));
        } else if (handler.getLevel() == DragonLevel.ADULT && handler.getSize() >= (double)40.0F) {
            progress = (float)((curSize - 40.0) / (ServerConfig.maxGrowthSize - 40.0));
        }

        int size = 34;
        int thickness = 5;
        int circleX = this.leftPos - 58;
        int circleY = this.topPos - 40;
        int sides = 6;
        int radius = size / 2;
        Color c = new Color(99, 99, 99);
        RenderSystem.setShaderColor(c.brighter().getRed() / 255.0F, c.brighter().getBlue() / 255.0F, c.brighter().getGreen() / 255.0F, 1.0F);
        RenderingUtils.drawSmoothCircle(guiGraphics, circleX + radius, circleY + radius, radius, sides, 1.0F, 0.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, textures.get(createTextureKey(handler.getType(), "circle", "")));
        RenderingUtils.drawTexturedCircle(guiGraphics, circleX + radius, circleY + radius, radius, 0.5F, 0.5F, 0.5F, sides, progress, -0.5F);
        RenderSystem.setShaderColor(c.getRed() / 255.0F, c.getBlue() / 255.0F, c.getGreen() / 255.0F, 1.0F);
        RenderingUtils.drawSmoothCircle(guiGraphics, circleX + radius, circleY + radius, radius - thickness, sides, 1.0F, 0.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(textures.get(createTextureKey(handler.getType(), "growth", "_" + (handler.getLevel().ordinal() + 1))), circleX + 6, circleY + 6, 150, 0.0F, 0.0F, 20, 20, 20, 20);
    }

    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    static {
        initResources();
    }
}