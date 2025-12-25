package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.dragon_editor.DragonEditorScreen;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonEditorScreen.class)
public class DragonEditorScreenMixin {
    @ModifyReturnValue(method = "setZoom", at = @At("RETURN"))
    private static float dontZoomDragonborn(float original) {
        return DragonbornUtils.isDragonborn(DragonEditorScreen.HANDLER) ? 32 : original;
    }
}
