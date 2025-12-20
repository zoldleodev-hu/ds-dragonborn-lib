package hu.zoldleo.dragonborn.client;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import hu.zoldleo.dragonborn.common.DragonbornEntity;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.jetbrains.annotations.NotNull;

public class DragonbornPartsLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public DragonbornPartsLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull AbstractClientPlayer player, float v, float v1, float partialTicks, float v3, float v4, float v5) {
        DragonStateHandler handler = player.getData(DSDataAttachments.DRAGON_HANDLER);
        if (DragonbornUtils.isDragonborn(handler) && !player.isInvisible()) {
            DragonbornEntity dragon = DragonbornDB.getOrCreateDragonborn(player);
            poseStack.pushPose();
            poseStack.scale(-1, -1, -1);
            Minecraft.getInstance().getEntityRenderDispatcher().render(dragon, 0, 0, 0, 0, partialTicks, poseStack, buffer, packedLight);
            poseStack.popPose();
        }
    }
}