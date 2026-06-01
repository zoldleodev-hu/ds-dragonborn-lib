package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.DragonEditorScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.dragon_editor.buttons.DragonBodyButton;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = DragonBodyButton.class, remap = false)
public class DragonBodyButtonMixin {
    @ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/AbstractDragonType;getTypeNameLowerCase()Ljava/lang/String;"))
    private String resolveCustomType(String original) {
        return new ResourceLocation(original).getPath();
    }

    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "dragonsurvival"))
    private String setCustomTypeNamespace(String constant, @Local(argsOnly = true) DragonEditorScreen screen) {
        ResourceLocation loc = new ResourceLocation(screen.dragonType.getTypeNameLowerCase());
        return loc.getNamespace().equals("minecraft") ? "dragonsurvival" : loc.getNamespace();
    }
}