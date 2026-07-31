package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.ToolTipHandler;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonFoodHandler;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.api.dragon_type.IFoodTooltipProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ToolTipHandler.class, remap = false)
public class ToolTipHandlerMixin {
    @Shadow
    private static MutableComponent createFoodTooltip(Item item, AbstractDragonType type, ChatFormatting color, String nutritionIcon, String saturationIcon) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(method = "checkIfDragonFood", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraftforge/event/entity/player/ItemTooltipEvent;getToolTip()Ljava/util/List;"))
    private static void addCustomFoodTooltip(ItemTooltipEvent tooltipEvent, CallbackInfo ci, @Local(name = "toolTip") List<Component> toolTip, @Local(name = "item") Item item) {
        for (AbstractDragonType type : DragonTypes.staticTypes.values())
            if (type instanceof IFoodTooltipProvider provider && DragonFoodHandler.getEdibleFoods(type).contains(item))
                toolTip.add(createFoodTooltip(item, type, provider.foodTooltipColor(), provider.nutritionIcon(), provider.saturationIcon()));
    }
}