package hu.zoldleo.dragonborn.mixin.bettercombat;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import hu.zoldleo.dragonborn.util.DragonbornUtils;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = HumanoidArmorLayer.class, priority = 1500)
public class HumanoidArmorLayerMixin {
    @TargetHandler(mixin = "by.dragonsurvivalteam.dragonsurvival.mixins.bettercombat.HumanoidArmorLayerMixin", name = "dragonSurvival$hideArmor")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lby/dragonsurvivalteam/dragonsurvival/common/capability/DragonStateProvider;isDragon(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean renderArmor(Entity entity, Operation<Boolean> original) {
        return original.call(entity) && !DragonbornUtils.isDragonDragonborn(entity);
    }
}