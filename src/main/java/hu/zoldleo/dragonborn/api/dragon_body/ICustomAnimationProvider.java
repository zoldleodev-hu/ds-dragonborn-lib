package hu.zoldleo.dragonborn.api.dragon_body;

import net.minecraft.resources.ResourceLocation;

/**
 * Provides the animation resource for the body. If not applied, the location will be generated from the body's name.
 * <p>
 * Generated location: dragonsurvival/animations/dragon_&ltbody&gt.json
 * <p>
 * Usable on: dragon body
 */
public interface ICustomAnimationProvider {
    ResourceLocation animResource();
}