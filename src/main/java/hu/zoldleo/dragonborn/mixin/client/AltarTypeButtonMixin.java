package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.AltarTypeButton;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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

    @ModifyArg(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIFFIIII)V"))
    private ResourceLocation customTexture2(ResourceLocation loc) {
        if (Arrays.asList(null, DragonTypes.CAVE, DragonTypes.FOREST, DragonTypes.SEA).contains(type))
            return loc;
        return new ResourceLocation("dragonsurvival", "textures/gui/custom/altar/" + type.getTypeNameLowerCase() + "/altar_icon.png");
    }

    @ModifyVariable(method = "renderWidget", at = @At(value = "STORE", ordinal = 0), name = "uOffset")
    private int noOffset(int uOffset) {
        if (type != null)
            return 0;
        return uOffset;
    }
}