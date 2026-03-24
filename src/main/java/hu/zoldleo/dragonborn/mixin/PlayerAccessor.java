package hu.zoldleo.dragonborn.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface PlayerAccessor {
    @Accessor("dragonborn$landed")
    @Dynamic
    boolean landed();

    @Accessor("dragonborn$landed")
    @Dynamic
    void landed(boolean value);
}
