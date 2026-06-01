package hu.zoldleo.dragonborn.common.datadriven.upgrade;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public record InputData(Type type, Integer input) {
    public static InputData experienceLevels(int experienceLevels) {
        return new InputData(Type.EXPERIENCE_LEVELS, experienceLevels);
    }

    public static InputData growth(int growth) {
        return new InputData(Type.GROWTH, growth);
    }

    public enum Type implements StringRepresentable {
        EXPERIENCE_LEVELS("experience_levels"),
        GROWTH("growth");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        private final String name;

        Type(final String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}