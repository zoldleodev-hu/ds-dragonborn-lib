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

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorHandler;
import by.dragonsurvivalteam.dragonsurvival.client.util.FakeClientPlayer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@SuppressWarnings("all")
@Mixin(value = DragonEditorHandler.class, remap = false)
public class DragonEditorHandlerMixin {
    @ModifyVariable(method = "genTextures", at = @At(value = "STORE", ordinal = 1))
    private static NativeImage addPlayerTexture(NativeImage original, @Local(argsOnly = true) Player player, @Local(argsOnly = true) DragonStateHandler handler) throws NoSuchFieldException, IllegalAccessException {
        if (player instanceof FakeClientPlayer fake)
            handler = fake.handler;

        if (DragonbornUtils.isDragonborn(handler) && player instanceof AbstractClientPlayer clientPlayer) {
            ResourceLocation fakeSkin = (ResourceLocation)handler.getClass().getField("dragonborn$fakeSkinTexture").get(handler);
            ResourceLocation skin = (fakeSkin == null) ? clientPlayer.getSkinTextureLocation() : fakeSkin;
            AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(skin);

            if (handler == DragonEditorScreen.handler)
                texture = Minecraft.getInstance().getTextureManager().getTexture(Minecraft.getInstance().player.getSkinTextureLocation());

            try {
                Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(skin);
                if (resource.isEmpty())
                    throw new IOException(String.format("Resource %s not found!", skin.getPath()));

                InputStream textureStream = ((Resource)resource.get()).open();
                NativeImage tempColorPicker = NativeImage.read(textureStream);
                textureStream.close();

                for(int x = 0; x < tempColorPicker.getWidth(); ++x) {
                    for(int y = 0; y < tempColorPicker.getHeight(); ++y) {
                        Color color = new Color(tempColorPicker.getPixelRGBA(x, y), true);
                        if (color.getAlpha() != 0)
                            original.setPixelRGBA(x, y, color.getRGB());
                    }
                }

                tempColorPicker.close();
            } catch (IOException e) {
                DragonSurvivalMod.LOGGER.error("An error occured while compiling the dragon skin texture", e);
            }
        }
        return original;
    }
}