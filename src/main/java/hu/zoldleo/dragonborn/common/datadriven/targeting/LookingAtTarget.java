package hu.zoldleo.dragonborn.common.datadriven.targeting;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.LevelBasedValue;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.function.Predicate;

public record LookingAtTarget(Either<BlockTargeting, EntityTargeting> target, LevelBasedValue range) implements AbilityTargeting {
    private static final String LOOKING_AT_TARGET_BLOCK = "dragonsurvival.gui.ability_target.looking_at.block";
    private static final String LOOKING_AT_TARGET_ENTITY = "dragonsurvival.gui.ability_target.looking_at.entity";

    public static final MapCodec<LookingAtTarget> CODEC = RecordCodecBuilder.mapCodec(instance -> AbilityTargeting.codecStart(instance).and(LevelBasedValue.CODEC.fieldOf("range").forGetter(LookingAtTarget::range)).apply(instance, LookingAtTarget::new));
    public static final ResourceLocation TYPE = DragonSurvivalMod.res("looking_at");

    @Override
    public void apply(final ServerPlayer dragon, final DataDrivenDragonAbility ability) {
        target().ifLeft(blockTarget -> {
            HitResult result = getBlockHitResult(dragon, ability);

            if (result.getType() == HitResult.Type.MISS || !(result instanceof BlockHitResult blockHitResult)) {
                return;
            }

            if (!blockTarget.matches(dragon, blockHitResult.getBlockPos()) || /* This is always checked by the predicate */ !dragon.serverLevel().isLoaded(blockHitResult.getBlockPos())) {
                return;
            }

            blockTarget.effects().forEach(target -> target.apply(dragon, ability, blockHitResult.getBlockPos(), blockHitResult.getDirection()));
        }).ifRight(entityTarget -> {
            Predicate<Entity> filter = entity -> entityTarget.targetingMode().isEntityRelevant(dragon, entity) && entityTarget.matches(dragon, entity, entity.position());
            HitResult result = getEntityHitResult(dragon, filter, ability);

            if (result.getType() == HitResult.Type.MISS || !(result instanceof EntityHitResult entityHitResult)) {
                return;
            }

            entityTarget.effects().forEach(target -> target.apply(dragon, ability, entityHitResult.getEntity()));
        });
    }

    @Override
    public MutableComponent getDescription(final Player dragon, final DataDrivenDragonAbility ability) {
        Component targetingComponent = target.map(block -> null, entity -> entity.targetingMode().translation());
        MutableComponent range = Component.literal(FORMAT.format(getDistance(dragon, ability))).withStyle(ChatFormatting.BLUE);

        if (targetingComponent == null)
            return Component.translatable(LOOKING_AT_TARGET_BLOCK, range);
        return Component.translatable(LOOKING_AT_TARGET_ENTITY, ((MutableComponent)targetingComponent).withStyle(ChatFormatting.BLUE), range);
    }

    @Override
    public ResourceLocation type() {
        return TYPE;
    }

    @Override
    public float getDistance(final Player dragon, final DataDrivenDragonAbility instance) {
        return range.calculate(instance.level);
    }

    public HitResult getBlockHitResult(final Player dragon, final DataDrivenDragonAbility ability) {
        return dragon.pick(getDistance(dragon, ability), 0, false);
    }

    public HitResult getEntityHitResult(final Player dragon, final Predicate<Entity> filter, final DataDrivenDragonAbility ability) {
        return ProjectileUtil.getHitResultOnViewVector(dragon, filter, getDistance(dragon, ability));
    }

    @Override
    public MapCodec<? extends AbilityTargeting> codec() {
        return CODEC;
    }
}