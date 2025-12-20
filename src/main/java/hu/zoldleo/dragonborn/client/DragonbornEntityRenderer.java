package hu.zoldleo.dragonborn.client;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hu.zoldleo.dragonborn.common.DragonbornEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Optional;

public class DragonbornEntityRenderer extends GeoEntityRenderer<DragonbornEntity> {
    public DragonbornEntityRenderer(EntityRendererProvider.Context renderManager, GeoModel<DragonbornEntity> model) {
        super(renderManager, model);
    }

    @Override
    public void preRender(PoseStack poseStack, DragonbornEntity animatable, BakedGeoModel model,
                          @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay, int colour) {
        if (animatable.getPlayer() instanceof AbstractClientPlayer player) {
            Optional<GeoBone> headBone = model.getBone("Head");
            Optional<GeoBone> bodyBone = model.getBone("Body");
            if(headBone.isPresent()) {
                ModelPart root = ((PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player)).getModel().head;
                copyTRSData(root, headBone.get());
            }
            if(bodyBone.isPresent()) {
                ModelPart root = ((PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player)).getModel().body;
                copyTRSData(root, bodyBone.get());
            }
        }
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    private void copyTRSData(ModelPart root, GeoBone bone) {
        bone.setPosX(root.x);
        bone.setPosY(-root.y);
        bone.setPosZ(root.z);
        bone.setRotX(-root.xRot);
        bone.setRotY(root.yRot);
        bone.setRotZ(root.zRot);
        bone.setScaleX(root.xScale);
        bone.setScaleY(root.yScale);
        bone.setScaleZ(root.zScale);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, DragonbornEntity animatable, BakedGeoModel model,
                               @Nullable RenderType renderType, MultiBufferSource bufferSource,
                               @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
                               int packedLight, int packedOverlay, int colour) {
        Player player = animatable.getPlayer();
        if (player == null || player.isSpectator() || player.isInvisibleTo(Minecraft.getInstance().player)) {
            return;
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        player.getData(DSDataAttachments.DRAGON_HANDLER).refreshBody = false;
    }
}
