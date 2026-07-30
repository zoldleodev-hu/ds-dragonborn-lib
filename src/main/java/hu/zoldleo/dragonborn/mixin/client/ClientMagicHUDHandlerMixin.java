package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientMagicHUDHandler;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientMagicHUDHandler.class)
public class ClientMagicHUDHandlerMixin {
    @WrapOperation(method = "renderAbilityHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 1))
    private static void renderCustomManaIcons(GuiGraphics instance, ResourceLocation texture, int x, int y, float u, float v, int du, int dv, int texWidth, int texHeight, Operation<Void> original, @Local(name = "condiXPos") int condiXPos, @Local(argsOnly = true) DragonStateHandler handler, @Local(name = "curMana") int curMana, @Local(name = "manaSlot") int manaSlot, @Local(name = "goodCondi") boolean goodCondi, @Local(name = "rescale") float rescale) {
        if (condiXPos == 36 && !DragonbornUtils.isDragonType(handler, DragonTypes.CAVE)) {
            ResourceLocation loc = curMana <= manaSlot ? (goodCondi ?
                    DragonSurvivalMod.res("textures/gui/custom/mana_icons/" + handler.getTypeNameLowerCase() + "/recovery.png") :
                    DragonSurvivalMod.res("textures/gui/custom/mana_icons/" + handler.getTypeNameLowerCase() + "/empty.png")) :
                    DragonSurvivalMod.res("textures/gui/custom/mana_icons/" + handler.getTypeNameLowerCase() + "/full.png");
            instance.blit(loc, x, y, 0, 0, (int)(18 / rescale), (int)(18 / rescale), (int)(18 / rescale), (int)(18 / rescale));
        }
        else {
            original.call(instance, texture, x, y, u, v, du, dv, texWidth, texHeight);
        }
    }

    @WrapOperation(method = "renderAbilityHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 2))
    private static void renderCustomCastBar(GuiGraphics instance, ResourceLocation texture, int x, int y, float u, float v, int du, int dv, int texWidth, int texHeight, Operation<Void> original, @Local(name = "yPos1") int yPos1, @Local(argsOnly = true) DragonStateHandler handler) {
        if (yPos1 == 94 && !DragonbornUtils.isDragonType(handler, DragonTypes.SEA))
            instance.blit(DragonSurvivalMod.res("textures/gui/custom/casting_bars/" + handler.getTypeNameLowerCase() + "/cast_bar.png"), x, y, u, 0, du, dv, 196, 47);
        else
            original.call(instance, texture, x, y, u, v, du, dv, texWidth, texHeight);
    }

    @WrapOperation(method = "renderAbilityHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V", ordinal = 3))
    private static void renderCustomCastFill(GuiGraphics instance, ResourceLocation texture, int x, int y, float u, float v, int du, int dv, int texWidth, int texHeight, Operation<Void> original, @Local(name = "yPos2") int yPos2, @Local(argsOnly = true) DragonStateHandler handler) {
        if (yPos2 == 152 && !DragonbornUtils.isDragonType(handler, DragonTypes.SEA))
            instance.blit(DragonSurvivalMod.res("textures/gui/custom/casting_bars/" + handler.getTypeNameLowerCase() + "/cast_fill.png"), x, y, u, 0, du, dv, 191, 4);
        else
            original.call(instance, texture, x, y, u, v, du, dv, texWidth, texHeight);
    }
}