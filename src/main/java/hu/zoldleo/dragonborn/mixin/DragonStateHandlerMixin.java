package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DragonStateHandler.class)
public class DragonStateHandlerMixin {
    @Unique
    public PlayerSkin dragonborn$fakeSkin;
}
