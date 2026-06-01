package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.gui.DragonScreen;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;

@Mixin(value = DragonScreen.class, remap = false)
public class DragonScreenMixin {
    @WrapOperation(method = "renderBg", at = @At(value = "INVOKE", target = "Ljava/util/HashMap;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object generateMissingTextures(HashMap<String, ResourceLocation> instance, Object key, Operation<ResourceLocation> original) {
        ResourceLocation loc = original.call(instance, key);
        if (loc == null) {
            String str = (String)key;
            ResourceLocation parsed = new ResourceLocation(str);
            String namespace = parsed.getNamespace().equals("minecraft") ? "dragonsurvival" : parsed.getNamespace();
            loc = new ResourceLocation(namespace, "textures/gui/growth/" + parsed.getPath() + ".png");
            instance.put(str, loc);
        }
        return loc;
    }
}