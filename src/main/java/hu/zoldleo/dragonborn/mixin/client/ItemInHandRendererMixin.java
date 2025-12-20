package hu.zoldleo.dragonborn.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Unique
    private DragonStateHandler dragonborn$tempHandler = null;

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void removeDragon(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        dragonborn$tempHandler = player.getData(DSDataAttachments.DRAGON_HANDLER);
        if (DragonbornUtils.isDragonborn(dragonborn$tempHandler)) {
            player.setData(DSDataAttachments.DRAGON_HANDLER, DragonbornUtils.emptyHandler);
        }
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void restoreDragon(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, CallbackInfo ci) {
        player.setData(DSDataAttachments.DRAGON_HANDLER, dragonborn$tempHandler);
    }
}
