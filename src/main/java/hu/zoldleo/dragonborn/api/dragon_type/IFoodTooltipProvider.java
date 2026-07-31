package hu.zoldleo.dragonborn.api.dragon_type;

import net.minecraft.ChatFormatting;

/**
 * Provies food tooltip settings. Not applying this interface results in the tooltip not showing.
 * <p>
 * Usable on: dragon type
 */
public interface IFoodTooltipProvider {
    ChatFormatting foodTooltipColor();

    String nutritionIcon();

    String saturationIcon();
}