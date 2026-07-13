package hu.zoldleo.dragonborn.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRender;
import by.dragonsurvivalteam.dragonsurvival.client.skins.DragonSkins;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import hu.zoldleo.dragonborn.registry.DragonbornContainers;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DragonbornClient {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        MenuScreens.register(DragonbornContainers.dragonbornContainer, DragonbornInventoryScreen::new);
    }

    @SubscribeEvent
    public static void addRenderLayer(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(skin -> {
            LivingEntityRenderer<Player, EntityModel<Player>> renderer = event.getSkin(skin);
            if (renderer == null)
                return;
            renderer.addLayer(new RenderLayer<>(renderer) {
                @Override
                public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull Player player, float v, float v1, float partialTicks, float v3, float v4, float v5) {
                    DragonStateHandler handler = DragonbornUtils.getHandler(player);
                    if (DragonbornUtils.isDragonborn(handler) && !player.isInvisible()) {
                        poseStack.pushPose();
                        poseStack.mulPose(Axis.XP.rotationDegrees(180f));
                        float scale = 1f / player.getScale();
                        poseStack.scale(scale, scale, scale);
                        DragonEntity dragon = ClientDragonRender.getDragon(player);
                        ClientDragonRender.dragonModel.setCurrentTexture(DragonSkins.getPlayerSkin(player, handler.getType(), handler.getLevel()));
                        if (dragon != null)
                            Minecraft.getInstance().getEntityRenderDispatcher().render(dragon, 0, 0, 0, 0, partialTicks, poseStack, buffer, packedLight);
                        ClientDragonRender.dragonModel.setCurrentTexture(null);
                        poseStack.popPose();
                    }
                }
            });
        });
    }
}