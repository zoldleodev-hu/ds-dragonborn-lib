package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SmithingScreen.class)
public class SmithingScreenMixin {
    @Unique
    DragonStateHandler dragonborn$tempHandler = null;
    private @Nullable DragonEntity dragonSurvival$dragon;

    @Inject(method = "subInit", at = @At("HEAD"))
    private void removeDragon(CallbackInfo ci) {
        if (Minecraft.getInstance().player instanceof LocalPlayer player) {
            dragonborn$tempHandler = player.getData(DSDataAttachments.DRAGON_HANDLER);
            if (DragonbornUtils.isDragonborn(dragonborn$tempHandler)) {
                player.setData(DSDataAttachments.DRAGON_HANDLER, DragonbornUtils.emptyHandler);
                if (dragonSurvival$dragon != null)
                    dragonSurvival$dragon = null;
            }
        }
    }

    @Inject(method = "subInit", at = @At("TAIL"))
    private void restoreDragon(CallbackInfo ci) {
        if (Minecraft.getInstance().player instanceof LocalPlayer player)
            player.setData(DSDataAttachments.DRAGON_HANDLER, dragonborn$tempHandler);
        System.out.println(dragonSurvival$dragon == null);
    }
}
