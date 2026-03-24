package hu.zoldleo.dragonborn.mixin;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DragonEntity.class)
public class DragonEntityMixin {
    @ModifyExpressionValue(method = "predicate", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/codecs/ability/animation/AbilityAnimation;locksHead()Z"))
    private boolean dontLockHead(boolean original, @Local Player player) {
        return original && !DragonbornUtils.isDragonborn(player);
    }

    @ModifyExpressionValue(method = "predicate", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/codecs/ability/animation/AbilityAnimation;locksTail()Z"))
    private boolean dontLockTail(boolean original, @Local Player player) {
        return original && !DragonbornUtils.isDragonborn(player);
    }
}
