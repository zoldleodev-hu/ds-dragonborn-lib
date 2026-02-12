package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DragonStateHandler.class)
public interface DragonStateHandlerAccessor {
    @Accessor("dragonborn$fakeSkin")
    @Dynamic
    PlayerSkin dragonborn$getFakeSkin();

    @Accessor("dragonborn$fakeSkin")
    @Dynamic
    void dragonborn$setFakeSkin(PlayerSkin skin);
}
