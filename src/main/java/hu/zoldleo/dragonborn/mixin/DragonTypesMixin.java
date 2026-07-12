package hu.zoldleo.dragonborn.mixin;

/*/import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.api.event.RegisterDragonTypeEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;*/

//@Mixin(value = DragonTypes.class, remap = false)
public class DragonTypesMixin {
    /*/@Unique
    private static ArrayList<AbstractDragonType> dragonborn$emptyList = new ArrayList<>();

    @ModifyReturnValue(method = "getTypes", at = @At("RETURN"), remap = false)
    private static List<String> addDataDrivenTypes(List<String> original) {
        List<String> modifyableCopy = new ArrayList<>(original);
        modifyableCopy.addAll(DataDrivenDragonType.getRegisteredDragonTypeNames());
        return modifyableCopy;
    }

    @ModifyReturnValue(method = "getStaticSubtype", at = @At("RETURN"), remap = false)
    private static AbstractDragonType addDataDrivenTypes(AbstractDragonType original, @Local(argsOnly = true) String name) {
        if (original != null)
            return original;
        return DataDrivenDragonType.getRegisteredDragonTypeByName(name);
    }

    @ModifyExpressionValue(method = "getAllSubtypes", at = @At(value = "INVOKE", target = "Ljava/util/HashMap;get(Ljava/lang/Object;)Ljava/lang/Object;"), remap = false)
    private static Object considerNull(Object original, @Local(name = "type") String type, @Local(name = "res") ArrayList<String> res) {
        if (original != null)
            return original;
        res.add(type);
        return dragonborn$emptyList;
    }*/

    /*/@ModifyReturnValue(method = "newDragonTypeInstance", at = @At("RETURN"))
    private static AbstractDragonType createDataDrivenInstance(AbstractDragonType original, @Local(argsOnly = true) String name) {
        if (original != null)
            return original;
        DataDrivenDragonType registered = DataDrivenDragonType.getRegisteredDragonTypeByName(name);
        if (registered == null)
            return null;
        return registered.copy();
    }*/
}