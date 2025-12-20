package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.DragonAltarScreen;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonAltarScreen.class)
public class DragonAltarScreenMixin {
    @Shadow
    @Final
    private DragonStateHandler handler1;

    @Shadow
    @Final
    private DragonStateHandler handler2;

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
    }
}
