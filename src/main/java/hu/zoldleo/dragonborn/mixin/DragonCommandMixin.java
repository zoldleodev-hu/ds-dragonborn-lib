package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.commands.DragonCommand;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = DragonCommand.class, remap = false)
public class DragonCommandMixin {
    @ModifyExpressionValue(method = "lambda$register$2", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/DragonTypes;getAllSubtypes()Ljava/util/List;"))
    private static List<String> addDataDrivenTypes(List<String> original) {
        original.addAll(DataDrivenDragonType.getRegisteredDragonTypeNames());
        return original;
    }

    @ModifyExpressionValue(method = "lambda$register$2", at = @At(value = "INVOKE", target = "Ljava/lang/String;toLowerCase(Ljava/util/Locale;)Ljava/lang/String;"))
    private static String appendStringMarks(String original) {
        return original.contains(":") ? ("\"" + original + "\"") : original;
    }

    @ModifyExpressionValue(method = "runCommand", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/DragonTypes;getStaticSubtype(Ljava/lang/String;)Lby/dragonsurvivalteam/dragonsurvival/common/dragon_types/AbstractDragonType;"))
    private static AbstractDragonType getDataDrivenType(AbstractDragonType original, @Local(argsOnly = true, ordinal = 0) String type) {
        return original != null ? original : DataDrivenDragonType.getRegisteredDragonTypeByName(type);
    }
}