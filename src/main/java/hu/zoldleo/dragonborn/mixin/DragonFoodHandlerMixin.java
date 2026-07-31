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

package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.api.dragon_type.IDietProvider;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Mixin(value = DragonFoodHandler.class, remap = false)
public class DragonFoodHandlerMixin {
    @Unique
    private static Map<AbstractDragonType, CopyOnWriteArrayList<Item>> dragonborn$dietByType = new HashMap<>();

    @Shadow
    private static ConcurrentHashMap<Item, FoodProperties> buildDragonFoodMap(AbstractDragonType type) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @SuppressWarnings("all")
    @Inject(method = "getEdibleFoods", at = @At(value = "JUMP", ordinal = 10), cancellable = true)
    private static void getCustomFoodDataCache(AbstractDragonType type, CallbackInfoReturnable<CopyOnWriteArrayList<Item>> cir) {
        if (dragonborn$dietByType.containsKey(type))
            cir.setReturnValue(dragonborn$dietByType.get(type));
    }

    @Inject(method = "getEdibleFoods", at = @At(value = "RETURN", ordinal = 4))
    private static void putCustomFoodDataCache(AbstractDragonType type, CallbackInfoReturnable<CopyOnWriteArrayList<Item>> cir, @Local(name = "foods") CopyOnWriteArrayList<Item> foods) {
        if (!List.of(DragonTypes.CAVE, DragonTypes.FOREST, DragonTypes.SEA).contains(type))
            dragonborn$dietByType.put(type, foods);
    }

    @Inject(method = "clearTooltipMaps", at = @At("HEAD"))
    private static void clearCustomFoodCache(CallbackInfo ci) {
        dragonborn$dietByType.clear();
    }

    @Inject(method = "rebuildFoodMap", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/handlers/DragonFoodHandler;clearTooltipMaps()V"))
    private static void buildCustomSpeciesFood(CallbackInfo ci, @Local(name = "map") ConcurrentHashMap<String, ConcurrentHashMap<Item, FoodProperties>> map) {
        DragonTypes.staticTypes.values().stream().filter(x ->
                !(DragonbornUtils.isDragonType(x, DragonTypes.CAVE) ||
                DragonbornUtils.isDragonType(x, DragonTypes.FOREST) ||
                DragonbornUtils.isDragonType(x, DragonTypes.SEA))
        ).forEach(x -> map.put(x.getTypeName(), buildDragonFoodMap(x)));
    }

    @ModifyArg(method = "buildDragonFoodMap", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;of([Ljava/lang/Object;)Ljava/util/stream/Stream;"))
    private static Object[] getFoodListFromProvider(Object[] values, @Local(argsOnly = true) AbstractDragonType type) {
        return type instanceof IDietProvider provider ? provider.getDietConfig().toArray() : values;
    }

    @WrapOperation(method = "renderFoodBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    private static void renderCustomFoodIcons(GuiGraphics instance, ResourceLocation texture, int x, int y, int u, int v, int du, int dv, Operation<Void> original, @Local(name = "handler") DragonStateHandler handler, @Local(name = "type") int type) {
        if (type == 18 && !DragonbornUtils.isDragonType(handler, DragonTypes.SEA))
            instance.blit(DragonSurvivalMod.res("textures/gui/custom/food_icons/" + handler.getTypeNameLowerCase() + "_food_icons.png"), x, y, u, 0, du, dv);
        else
            original.call(instance, texture, x, y, u, v, du, dv);
    }
}