package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.AltarTypeButton;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Arrays;

@Mixin(AltarTypeButton.class)
public class AltarTypeButtonMixin {
    @Shadow
    public AbstractDragonType type;

    @ModifyArg(method = "renderWidget", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"), index = 1)
    private ResourceLocation customTexture(ResourceLocation loc) {
        if (Arrays.asList(null, DragonTypes.CAVE, DragonTypes.FOREST, DragonTypes.SEA).contains(type))
            return loc;
        return new ResourceLocation("dragonsurvival", "textures/gui/custom/altar/" + type.getTypeNameLowerCase() + "/altar_icon.png");
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"))
    private void renderCustomBanner(GuiGraphics instance, ResourceLocation texture, int x, int y, float u, float v, int du, int dv, int texWidth, int texHeight, Operation<Void> original, @Local(name = "uOffset") int uOffset) {
        if (uOffset == 3 && type != null)
            instance.blit(DragonSurvivalMod.res("textures/gui/custom/altar/" + type.getTypeNameLowerCase() + "/altar_icon.png"), x, y, 0, ((AltarTypeButton)(Object)this).isHovered() ? 0 : 147, 49, 147, 49, 294);
        else
            original.call(instance, texture, x, y, u, v, du, dv, texWidth, texHeight);
    }
}