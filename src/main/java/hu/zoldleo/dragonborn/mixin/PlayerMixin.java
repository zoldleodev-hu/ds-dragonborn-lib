package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler;
import by.dragonsurvivalteam.dragonsurvival.compat.Compat;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.TreasureRestData;
import by.dragonsurvivalteam.dragonsurvival.server.handlers.ServerFlightHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Redirect(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;canPlayerFitWithinBlocksAndEntitiesWhen(Lnet/minecraft/world/entity/Pose;)Z"))
    private boolean desperateTweak(Player instance, Pose pose) {
        return dragonborn$tweakedFitCheck(pose);
    }

    @Unique
    private boolean dragonborn$tweakedFitCheck(Pose pose) {
        Player player = (Player)(Object)this;
        return (DragonStateProvider.isDragon(player) && !DragonbornUtils.isDragonborn(player) && !Compat.hasModelSwap(player)) ?
                DragonSizeHandler.canPoseFit(player, pose) :
                player.level().noCollision(player, player.getDimensions(pose).makeBoundingBox(player.position()).deflate(1.0E-7));
    }

    @ModifyExpressionValue(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isAutoSpinAttack()Z"))
    private boolean spin(boolean original) {
        Player player = (Player)(Object)this;
        return original || (DragonbornUtils.isDragonborn(player) && ServerFlightHandler.isSpin(player));
    }

    @ModifyExpressionValue(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isFallFlying()Z"))
    private boolean glide(boolean original) {
        Player player = (Player)(Object)this;
        return original || (DragonbornUtils.isDragonborn(player) && ServerFlightHandler.isGliding(player));
    }

    @ModifyExpressionValue(method = "updatePlayerPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSleeping()Z"))
    private boolean rest(boolean original) {
        Player player = (Player)(Object)this;
        return original || TreasureRestData.getData(player).isResting();
    }
}
