package hu.zoldleo.dragonborn.common.datadriven.targeting;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import hu.zoldleo.dragonborn.common.datadriven.activation.PassiveActivation;
import hu.zoldleo.dragonborn.common.datadriven.activation.trigger.ConstantTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public record SelfTarget(Either<BlockTargeting, EntityTargeting> target) implements AbilityTargeting {
    private static final String SELF_TARGET = "dragonsurvival.gui.ability_target.self_target";

    public static final MapCodec<SelfTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> AbilityTargeting.codecStart(instance).apply(instance, SelfTarget::new));
    public static final ResourceLocation TYPE = DragonSurvivalMod.res("self");

    @Override
    public void apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability) {
        target().ifLeft(blockTarget -> {
            if (blockTarget.matches(dragon, dragon.blockPosition())) {
                blockTarget.effects().forEach(target -> target.apply(dragon, ability, dragon.blockPosition(), null));
            }
        }).ifRight(entityTarget -> {
            if (entityTarget.matches(dragon, dragon, dragon.position())) {
                entityTarget.effects().forEach(target -> target.apply(dragon, ability, dragon));
            } else if (ability.activation instanceof PassiveActivation passive && passive.trigger() instanceof ConstantTrigger) {
                entityTarget.effects().forEach(target -> target.remove(dragon, ability, dragon, true));
            }
        });
    }

    @Override
    public void remove(final ServerPlayer dragon, final DataDrivenDragonAbility ability) {
        target().ifRight(entityTarget -> entityTarget.effects().forEach(target -> target.remove(dragon, ability, dragon, false)));
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public float getDistance(final Player dragon, final DataDrivenDragonAbility instance) {
        return Integer.MAX_VALUE;
    }

    @Override
    public MutableComponent getDescription(final Player dragon, final DataDrivenDragonAbility ability) {
        return Component.translatable(SELF_TARGET);
    }

    @Override
    public MapCodec<? extends AbilityTargeting> codec() {
        return CODEC;
    }
}