package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.DragonEditorRegistry;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonEditorObject;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;
import by.dragonsurvivalteam.dragonsurvival.util.GsonFactory;
import com.google.gson.Gson;
import hu.zoldleo.dragonborn.client.CustomDragonEditorObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Optional;

@Mixin(value = DragonEditorRegistry.class, remap = false)
public class DragonEditorRegistryMixin {
    @Shadow
    private static void dragonType(AbstractDragonType type, DragonEditorObject.Dragon je) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    public static HashMap<String, HashMap<DragonLevel, HashMap<EnumSkinLayer, String>>> defaultSkinValues;

    @Inject(method = "reload", at = @At(value = "RETURN"))
    private static void reloadCustom(ResourceManager manager, ResourceLocation location, CallbackInfo ci) {
        Gson gson = GsonFactory.getDefault();
        for (AbstractDragonType type : DragonTypes.staticTypes.values()) {
            ResourceLocation loc = DragonSurvivalMod.res("customization/" + type.getTypeNameLowerCase() + ".json");
            try {
                Optional<Resource> resource = manager.getResource(DragonSurvivalMod.res("customization/" + type.getTypeNameLowerCase() + ".json"));
                if (resource.isEmpty())
                    throw new IOException(String.format("Resource %s not found!", loc.getPath()));
                InputStream in = resource.get().open();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    CustomDragonEditorObject je = gson.fromJson(reader, CustomDragonEditorObject.class);
                    DragonEditorRegistry.CUSTOMIZATIONS.computeIfAbsent(type.getTypeNameUpperCase(), key -> new HashMap<>());
                    dragonType(type, je.parts);
                    defaultSkinValues.put(type.getTypeNameUpperCase(), je.defaults);
                } catch (IOException exception) {
                    DragonSurvivalMod.LOGGER.warn("Reader could not be closed", exception);
                }
            } catch (IOException exception) {
                DragonSurvivalMod.LOGGER.error("Resource [{}] could not be opened", loc, exception);
            }
        }
    }
}