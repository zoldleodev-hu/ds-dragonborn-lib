package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.gui.screens.DragonInventoryScreen;
import by.dragonsurvivalteam.dragonsurvival.client.gui.widgets.buttons.generic.ClickHoverButton;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DragonInventoryScreen.class)
public class DragonInventoryScreenMixin {
    @Shadow
    @Final
    private Player player;

    @Shadow
    private boolean clawsMenu;

    @ModifyArg(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0))
    private ResourceLocation renderHumanCraftingGrid(ResourceLocation atlasLocation) {
        return DragonbornUtils.humanCraftingGrid(player) ? Dragonborn.ClientEvents.HUMAN_CRAFTING_GRID_BACKGROUND : atlasLocation;
    }

    @WrapOperation(method = "init", at = @At(value = "NEW", target = "(IIIIIIIILnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/components/Button$OnPress;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/resources/ResourceLocation;)Lby/dragonsurvivalteam/dragonsurvival/client/gui/widgets/buttons/generic/ClickHoverButton;"))
    private ClickHoverButton noClawButton(int xPos, int yPos, int width, int height, int uOffset, int vOffset, int textureWidth, int textureHeight, Component displayString, Button.OnPress handler, ResourceLocation click, ResourceLocation hover, ResourceLocation main, Operation<ClickHoverButton> original) {
        if (DragonbornUtils.noClawSlots(player)) {
            clawsMenu = false;
            return null;
        }
        return original.call(xPos, yPos, width, height, uOffset, vOffset, textureWidth, textureHeight, displayString, handler, click, hover, main);
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/screens/DragonInventoryScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;", ordinal = 0))
    private GuiEventListener noClawButton(DragonInventoryScreen instance, GuiEventListener guiEventListener, Operation<GuiEventListener> original) {
        return DragonbornUtils.noClawSlots(player) ? guiEventListener : original.call(instance, guiEventListener);
    }
}