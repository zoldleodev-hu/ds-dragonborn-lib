package hu.zoldleo.dragonborn.common;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.codec.DragonbornCodecs;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonBody;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonType;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public record UnlockableBehavior(Optional<LootItemCondition> unlockCondition, Optional<Visibility> visibility) {
    public static final Codec<UnlockableBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DragonbornCodecs.LOOT_CONDITION_CODEC.optionalFieldOf("unlock_condition").forGetter(behavior -> behavior.unlockCondition),
            Visibility.CODEC.optionalFieldOf("visibility").forGetter(UnlockableBehavior::visibility)
    ).apply(instance, UnlockableBehavior::new));

    public enum Visibility implements StringRepresentable {
        ALWAYS_VISIBLE,
        ALWAYS_HIDDEN,
        VISIBLE_IF_LOCKED;

        public static final Codec<Visibility> CODEC = StringRepresentable.fromEnum(Visibility::values);

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ENGLISH);
        }
    }

    public record SpeciesEntry(Holder<DataDrivenDragonType> species, boolean isUnlocked) {
        public static final Codec<SpeciesEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DataDrivenDragonType.CODEC.fieldOf("species").forGetter(SpeciesEntry::species),
                Codec.BOOL.fieldOf("is_unlocked").forGetter(SpeciesEntry::isUnlocked)
        ).apply(instance, SpeciesEntry::new));
    }

    public record BodyEntry(Holder<DataDrivenDragonBody> body, boolean isUnlocked) {
        public static final Codec<BodyEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DataDrivenDragonBody.CODEC.fieldOf("body").forGetter(BodyEntry::body),
                Codec.BOOL.fieldOf("is_unlocked").forGetter(BodyEntry::isUnlocked)
        ).apply(instance, BodyEntry::new));
    }
}