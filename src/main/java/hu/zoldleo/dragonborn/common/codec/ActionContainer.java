package hu.zoldleo.dragonborn.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.LevelBasedValue;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import hu.zoldleo.dragonborn.common.datadriven.targeting.AbilityTargeting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public record ActionContainer(AbilityTargeting effect, TriggerPoint triggerPoint, LevelBasedValue triggerRate) {
    public static final Codec<ActionContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AbilityTargeting.CODEC.fieldOf("target_selection").forGetter(ActionContainer::effect),
            TriggerPoint.CODEC.optionalFieldOf("trigger_point", TriggerPoint.DEFAULT).forGetter(ActionContainer::triggerPoint),
            LevelBasedValue.CODEC.optionalFieldOf("trigger_rate", LevelBasedValue.constant(1)).forGetter(ActionContainer::triggerRate)
    ).apply(instance, ActionContainer::new));

    public void tick(final ServerPlayer dragon, final DataDrivenDragonAbility instance, int currentTick) {
        int actualTick = currentTick - instance.activation.getCastTime(instance.getLevel());
        float rate = triggerRate.calculate(instance.getLevel());

        if (rate > 0 && actualTick % rate != 0)
            return;

        effect.apply(dragon, instance);
    }

    public void remove(final ServerPlayer dragon, final DataDrivenDragonAbility instance) {
        effect.remove(dragon, instance);
    }

    public enum TriggerPoint implements StringRepresentable {
        DEFAULT("default"),
        CHARGING("charging"),
        CHANNEL_COMPLETION("channel_completion");

        public static final Codec<TriggerPoint> CODEC = StringRepresentable.fromEnum(TriggerPoint::values);

        private final String name;

        TriggerPoint(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}