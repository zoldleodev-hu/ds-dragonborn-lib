package hu.zoldleo.dragonborn.api.dragon_body;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

/**
 * Provides the model resource for the body. By default, it generates the location from the body's name. If not applied, the default model will be used.
 * <p>
 * Generated location: dragonsurvival/geo/&ltbody&gt.geo.json
 * <p>
 * Usable on: dragon body
 */
public interface ICustomModelProvider {
    default ResourceLocation modelResource() {
        return new ResourceLocation("dragonsurvival", ((AbstractDragonBody)this).getBodyName().toLowerCase(Locale.ENGLISH));
    }

    default boolean canUseCustomSkin() {
        return false;
    }
}