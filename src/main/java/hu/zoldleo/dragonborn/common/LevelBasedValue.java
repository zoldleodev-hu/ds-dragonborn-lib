package hu.zoldleo.dragonborn.common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface LevelBasedValue {
    Map<ResourceLocation, MapCodec<? extends LevelBasedValue>> levelBasedValueRegistry = new HashMap<>();
    Codec<LevelBasedValue> DISPATCH_CODEC = ResourceLocation.CODEC.dispatch(LevelBasedValue::type, loc -> levelBasedValueRegistry.get(loc).codec());
    Codec<LevelBasedValue> CODEC = Codec.either(Constant.CODEC, DISPATCH_CODEC).xmap((either) -> either.map(constant -> constant, value -> value), value -> value instanceof Constant constant ? Either.left(constant) : Either.right(value));
    boolean init = init();

    static Constant constant(float value) {
        return new Constant(value);
    }

    static Linear perLevel(float base, float perLevelAfterFirst) {
        return new Linear(base, perLevelAfterFirst);
    }

    static Linear perLevel(float p_perLevel) {
        return perLevel(p_perLevel, p_perLevel);
    }

    static Lookup lookup(List<Float> values, LevelBasedValue fallback) {
        return new Lookup(values, fallback);
    }

    float calculate(int var1);

    MapCodec<? extends LevelBasedValue> codec();

    ResourceLocation type();

    static boolean init() {
        levelBasedValueRegistry.put(Clamped.TYPE, Clamped.CODEC);
        levelBasedValueRegistry.put(Fraction.TYPE, Fraction.CODEC);
        levelBasedValueRegistry.put(LevelsSquared.TYPE, LevelsSquared.CODEC);
        levelBasedValueRegistry.put(Linear.TYPE, Linear.CODEC);
        levelBasedValueRegistry.put(Lookup.TYPE, Lookup.CODEC);
        return true;
    }

    record Clamped(LevelBasedValue value, float min, float max) implements LevelBasedValue {
        public static final Function<Clamped, DataResult<Clamped>> validator = clamped -> clamped.max <= clamped.min ? DataResult.error(() -> "Max must be larger than min, min: " + clamped.min + ", max: " + clamped.max) : DataResult.success(clamped);
        public static final MapCodec<Clamped> CODEC = RecordCodecBuilder.<Clamped>mapCodec((instance) -> instance.group(LevelBasedValue.CODEC.fieldOf("value").forGetter(Clamped::value), Codec.FLOAT.fieldOf("min").forGetter(Clamped::min), Codec.FLOAT.fieldOf("max").forGetter(Clamped::max)).apply(instance, Clamped::new)).flatXmap(validator, validator);
        public static final ResourceLocation TYPE = new ResourceLocation("minecraft", "clamped");

        public float calculate(int level) {
            return Mth.clamp(this.value.calculate(level), this.min, this.max);
        }

        public MapCodec<Clamped> codec() {
            return CODEC;
        }

        public ResourceLocation type() {
            return TYPE;
        }
    }

    record Constant(float value) implements LevelBasedValue {
        public static final Codec<Constant> CODEC = Codec.FLOAT.xmap(Constant::new, Constant::value);
        public static final MapCodec<Constant> TYPED_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codec.FLOAT.fieldOf("value").forGetter(Constant::value)).apply(instance, Constant::new));
        public static final ResourceLocation TYPE = new ResourceLocation("minecraft", "constant");

        public float calculate(int level) {
            return this.value;
        }

        public MapCodec<Constant> codec() {
            return TYPED_CODEC;
        }

        public ResourceLocation type() {
            return TYPE;
        }
    }

    record Fraction(LevelBasedValue numerator, LevelBasedValue denominator) implements LevelBasedValue {
        public static final MapCodec<Fraction> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(LevelBasedValue.CODEC.fieldOf("numerator").forGetter(Fraction::numerator), LevelBasedValue.CODEC.fieldOf("denominator").forGetter(Fraction::denominator)).apply(instance, Fraction::new));
        public static final ResourceLocation TYPE = new ResourceLocation("minecraft", "fraction");

        public float calculate(int level) {
            float f = this.denominator.calculate(level);
            return f == 0.0F ? 0.0F : this.numerator.calculate(level) / f;
        }

        public MapCodec<Fraction> codec() {
            return CODEC;
        }

        public ResourceLocation type() {
            return TYPE;
        }
    }

    record LevelsSquared(float added) implements LevelBasedValue {
        public static final MapCodec<LevelsSquared> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codec.FLOAT.fieldOf("added").forGetter(LevelsSquared::added)).apply(instance, LevelsSquared::new));
        public static final ResourceLocation TYPE = new ResourceLocation("minecraft", "levels_squared");

        public float calculate(int level) {
            return (float)Mth.square(level) + this.added;
        }

        public MapCodec<LevelsSquared> codec() {
            return CODEC;
        }

        public ResourceLocation type() {
            return TYPE;
        }
    }

    record Linear(float base, float perLevelAboveFirst) implements LevelBasedValue {
        public static final MapCodec<Linear> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codec.FLOAT.fieldOf("base").forGetter(Linear::base), Codec.FLOAT.fieldOf("per_level_above_first").forGetter(Linear::perLevelAboveFirst)).apply(instance, Linear::new));
        public static final ResourceLocation TYPE = new ResourceLocation("minecraft", "linear");

        public float calculate(int level) {
            return this.base + this.perLevelAboveFirst * (float)(level - 1);
        }

        public MapCodec<Linear> codec() {
            return CODEC;
        }

        public ResourceLocation type() {
            return TYPE;
        }
    }

    record Lookup(List<Float> values, LevelBasedValue fallback) implements LevelBasedValue {
        public static final MapCodec<Lookup> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(Codec.FLOAT.listOf().fieldOf("values").forGetter(Lookup::values), LevelBasedValue.CODEC.fieldOf("fallback").forGetter(Lookup::fallback)).apply(instance, Lookup::new));
        public static final ResourceLocation TYPE = new ResourceLocation("minecraft", "lookup");

        public float calculate(int level) {
            return level <= this.values.size() ? this.values.get(level - 1) : this.fallback.calculate(level);
        }

        public MapCodec<Lookup> codec() {
            return CODEC;
        }

        public ResourceLocation type() {
            return TYPE;
        }
    }
}