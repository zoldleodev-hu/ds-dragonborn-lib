package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.Dragonborn;
import net.minecraft.core.Holder;
import net.minecraft.world.food.FoodProperties;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonFoodHandler.class)
public class DragonFoodHandlerMixin {
    @ModifyReturnValue(method = "getDragonFoodProperties", at = @At(value = "RETURN", ordinal = 3))
    private static @Nullable FoodProperties asd(@Nullable FoodProperties dsOriginal, @Local(argsOnly = true) Holder<DragonSpecies> species, @Local(argsOnly = true) FoodProperties original) {
        if (species.is(Dragonborn.CAN_EAT_HUMAN_FOOD))
            return original;
        return dsOriginal;
    }
}
