package hu.zoldleo.dragonborn.mixin;

/*/import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.network.player.SynchronizeDragonCap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;*/

//@Mixin(value = SynchronizeDragonCap.class, remap = false)
public class SynchronizeDragonCapMixin {
    /*/@ModifyExpressionValue(method = "decode(Lnet/minecraft/network/FriendlyByteBuf;)Lby/dragonsurvivalteam/dragonsurvival/network/player/SynchronizeDragonCap;", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/DragonTypes;getStaticSubtype(Ljava/lang/String;)Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/AbstractDragonType;"))
    private static AbstractDragonType getRegisteredDragon(AbstractDragonType original, @Local(name = "typeS") String typeS) {
        return original != null ? original : DataDrivenDragonType.getRegisteredDragonTypeByName(typeS);
    }*/
}