package hu.zoldleo.dragonborn.common.datadriven;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.DragonEmoteSet;
import hu.zoldleo.dragonborn.common.Modifier;
import hu.zoldleo.dragonborn.common.UnlockableBehavior;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DataDrivenDragonBody extends AbstractDragonBody {
    public static final ResourceKey<Registry<DataDrivenDragonBody>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvivalMod.res("dragon_body"));
    public static final ResourceLocation DEFAULT_MODEL = DragonSurvivalMod.res("dragon_model");

    public static final Codec<DataDrivenDragonBody> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("body_name").forGetter(body -> body.bodyName),
            Codec.BOOL.optionalFieldOf("can_use_custom_skin", false).forGetter(body -> body.canUseCustomSkin),
            Codec.BOOL.optionalFieldOf("is_default", false).forGetter(body -> body.isDefault),
            UnlockableBehavior.CODEC.optionalFieldOf("unlockable_behavior", null).forGetter(body -> body.unlockableBehavior),
            Modifier.CODEC.listOf().fieldOf("modifiers").forGetter(body -> body.modifiers),
            Codec.BOOL.optionalFieldOf("can_hide_wings", true).forGetter(DataDrivenDragonBody::canHideWings),
            ResourceLocation.CODEC.optionalFieldOf("model", DEFAULT_MODEL).forGetter(body -> body.model),
            TextureSize.CODEC.optionalFieldOf("texture_size", new TextureSize(512, 512)).forGetter(body -> body.textureSize),
            ResourceLocation.CODEC.fieldOf("animation").forGetter(body -> body.animation),
            ResourceLocation.CODEC.optionalFieldOf("default_icon", null).forGetter(body -> body.defaultIcon),
            Codec.STRING.listOf().optionalFieldOf("bones_to_hide_for_toggle", List.of("WingLeft", "WingRight", "SmallWingLeft", "SmallWingRight")).forGetter(body -> body.bonesToHideForToggle),
            DragonEmoteSet.DIRECT_CODEC.fieldOf("emotes").forGetter(body -> body.emotes),
            ScalingProportions.CODEC.fieldOf("scaling_proportions").forGetter(body -> body.scalingProportions),
            Codec.doubleRange(0, 100).fieldOf("crouch_height_ratio").forGetter(body -> body.crouchHeightRatio),
            MountingOffsets.CODEC.optionalFieldOf("mounting_offset", null).forGetter(body -> body.mountingOffsets),
            BackpackOffsets.CODEC.optionalFieldOf("backpack_offset", null).forGetter(body -> body.backpackOffsets)
    ).apply(instance, instance.stable(DataDrivenDragonBody::new)));

    public static final Codec<Holder<DataDrivenDragonBody>> CODEC = RegistryFixedCodec.create(REGISTRY);

    public String bodyName;
    public boolean canUseCustomSkin;
    public boolean isDefault;
    public UnlockableBehavior unlockableBehavior;
    public List<Modifier> modifiers;
    public ResourceLocation model;
    public TextureSize textureSize;
    public ResourceLocation animation;
    public ResourceLocation defaultIcon;
    public List<String> bonesToHideForToggle;
    public DragonEmoteSet emotes;
    public ScalingProportions scalingProportions;
    public double crouchHeightRatio;
    public MountingOffsets mountingOffsets;
    public BackpackOffsets backpackOffsets;

    public DataDrivenDragonBody(String bodyName, Boolean canUseCustomSkin, Boolean isDefault, UnlockableBehavior unlockableBehavior, List<Modifier> modifiers, Boolean canHideWings, ResourceLocation model, TextureSize textureSize, ResourceLocation animation, ResourceLocation defaultIcon, List<String> bonesToHideForToggle, DragonEmoteSet emotes, ScalingProportions scalingProportions, Double crouchHeightRatio, MountingOffsets mountingOffsets, BackpackOffsets backpackOffsets) {

    }

    @Override
    public String getBodyName() {
        return bodyName;
    }

    @Override
    public void onPlayerUpdate() {

    }

    @Override
    public void onPlayerDeath() {

    }

    @Override
    public CompoundTag writeNBT() {
        return null;
    }

    @Override
    public void readNBT(CompoundTag compoundTag) {

    }

    public record TextureSize(int width, int height) {
        public static final Codec<TextureSize> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("width").forGetter(TextureSize::width),
                Codec.INT.fieldOf("height").forGetter(TextureSize::height)
        ).apply(instance, TextureSize::new));
    }

    public record ScalingProportions(double width, double height, double eyeHeight, double scaleMultiplier, double shadowMultiplier) {
        public static final Codec<ScalingProportions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("width").forGetter(ScalingProportions::width),
                Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("height").forGetter(ScalingProportions::height),
                Codec.doubleRange(0, Double.MAX_VALUE).fieldOf("eye_height").forGetter(ScalingProportions::eyeHeight),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("scale_multiplier", 1.0).forGetter(ScalingProportions::scaleMultiplier),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("shadow_multiplier", 1.0).forGetter(ScalingProportions::shadowMultiplier)
        ).apply(instance, ScalingProportions::new));

        public static ScalingProportions of(final double width, final double height, final double eyeHeight, final double offset, final double shadowOffset) {
            return new ScalingProportions(width, height, eyeHeight, offset, shadowOffset);
        }
    }

    public record MountingOffsets(Vec3 humanOffset, Vec3 dragonOffset, Vec3 scale) {
        public static final Codec<MountingOffsets> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Vec3.CODEC.optionalFieldOf("human_offset", Vec3.ZERO).forGetter(MountingOffsets::humanOffset),
                Vec3.CODEC.optionalFieldOf("dragon_offset", Vec3.ZERO).forGetter(MountingOffsets::dragonOffset),
                Vec3.CODEC.optionalFieldOf("offset_per_scale_above_one", Vec3.ZERO).forGetter(MountingOffsets::scale)
        ).apply(instance, MountingOffsets::new));

        public static MountingOffsets of(final Vec3 humanOffset, final Vec3 dragonOffset, final Vec3 scale) {
            return new MountingOffsets(humanOffset, dragonOffset, scale);
        }
    }

    public record BackpackOffsets(Vec3 posOffset, Vec3 rotOffset, Vec3 scale) {
        public static final Codec<BackpackOffsets> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Vec3.CODEC.optionalFieldOf("position_offset", Vec3.ZERO).forGetter(BackpackOffsets::posOffset),
                Vec3.CODEC.optionalFieldOf("rotation_offset", Vec3.ZERO).forGetter(BackpackOffsets::rotOffset),
                Vec3.CODEC.optionalFieldOf("scale", new Vec3(1, 1, 1)).forGetter(BackpackOffsets::scale)
        ).apply(instance, BackpackOffsets::new));

        public static BackpackOffsets of(final Vec3 pos_offset, final Vec3 rot_offset, final Vec3 scale) {
            return new BackpackOffsets(pos_offset, rot_offset, scale);
        }
    }
}