package hu.zoldleo.dragonborn.common;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import by.dragonsurvivalteam.dragonsurvival.common.capability.subcapabilities.ClawInventory;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.common.codec.DragonbornCodecs;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record MiscResources(
        Optional<ResourceLocation> foodSprites,
        Optional<ManaSprites> manaSprites,
        ResourceLocation altarBanner,
        ResourceLocation castBar,
        HoverIcon growthLeftArrow,
        HoverIcon growthRightArrow,
        FillIcon growthCrystal,
        FoodTooltip foodTooltip,
        TextColor primaryColor,
        TextColor secondaryColor,
        ClawInventory.Slot clawTextureSlot
) {
    public static final Codec<MiscResources> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("food_sprites").forGetter(MiscResources::foodSprites),
            ManaSprites.CODEC.optionalFieldOf("mana_sprites").forGetter(MiscResources::manaSprites),
            ResourceLocation.CODEC.fieldOf("altar_banner").forGetter(MiscResources::altarBanner),
            ResourceLocation.CODEC.fieldOf("ability_bar").forGetter(MiscResources::castBar),
            HoverIcon.CODEC.optionalFieldOf("growth_left_arrow", HoverIcon.DEFAULT_LEFT).forGetter(MiscResources::growthLeftArrow),
            HoverIcon.CODEC.optionalFieldOf("growth_right_arrow", HoverIcon.DEFAULT_RIGHT).forGetter(MiscResources::growthRightArrow),
            FillIcon.CODEC.fieldOf("growth_crystal").forGetter(MiscResources::growthCrystal),
            FoodTooltip.CODEC.fieldOf("food_tooltip").forGetter(MiscResources::foodTooltip),
            TextColor.CODEC.fieldOf("primary_color").forGetter(MiscResources::primaryColor),
            TextColor.CODEC.fieldOf("secondary_color").forGetter(MiscResources::secondaryColor),
            DragonbornCodecs.CLAW_INVENTORY_SLOT_CODEC.optionalFieldOf("claw_texture_slot", ClawInventory.Slot.PICKAXE).forGetter(MiscResources::clawTextureSlot)
    ).apply(instance, MiscResources::new));

    public record HoverIcon(ResourceLocation hoverIcon, ResourceLocation icon) {
        public static final HoverIcon DEFAULT_LEFT = new HoverIcon(
                DragonSurvivalMod.res("textures/gui/custom/stage/generic/left_arrow_hover.png"),
                DragonSurvivalMod.res("textures/gui/custom/stage/generic/left_arrow_main.png")
        );

        public static final HoverIcon DEFAULT_RIGHT = new HoverIcon(
                DragonSurvivalMod.res("textures/gui/custom/stage/generic/right_arrow_hover.png"),
                DragonSurvivalMod.res("textures/gui/custom/stage/generic/right_arrow_main.png")
        );

        public static final Codec<HoverIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("hover_icon").forGetter(HoverIcon::hoverIcon),
                ResourceLocation.CODEC.fieldOf("icon").forGetter(HoverIcon::icon)
        ).apply(instance, HoverIcon::new));
    }

    public record FillIcon(ResourceLocation empty, ResourceLocation full) {
        public static final Codec<FillIcon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("empty").forGetter(FillIcon::empty),
                ResourceLocation.CODEC.fieldOf("full").forGetter(FillIcon::full)
        ).apply(instance, FillIcon::new));
    }

    public record ManaSprites(ResourceLocation full, ResourceLocation reserved, ResourceLocation recovery, ResourceLocation empty) {
        public static final ManaSprites DEFAULT = new ManaSprites(
                DragonSurvivalMod.res("textures/gui/custom/mana_icons/generic/full.png"),
                DragonSurvivalMod.res("textures/gui/custom/mana_icons/generic/reserved.png"),
                DragonSurvivalMod.res("textures/gui/custom/mana_icons/generic/recovery.png"),
                DragonSurvivalMod.res("textures/gui/custom/mana_icons/generic/empty.png")
        );

        public static final Codec<ManaSprites> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("full").forGetter(ManaSprites::full),
                ResourceLocation.CODEC.fieldOf("reserved").forGetter(ManaSprites::reserved),
                ResourceLocation.CODEC.fieldOf("recovery").forGetter(ManaSprites::recovery),
                ResourceLocation.CODEC.fieldOf("empty").forGetter(ManaSprites::empty)
        ).apply(instance, ManaSprites::new));
    }

    public record FoodTooltip(ResourceLocation font, String nutritionIcon, String saturationIcon, Optional<TextColor> color) {
        public static final ResourceLocation DEFAULT_FOOD_TOOLTIP_FONT = DragonSurvivalMod.res("food_tooltip_icon_font");

        public static final Codec<FoodTooltip> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("font", DEFAULT_FOOD_TOOLTIP_FONT).forGetter(FoodTooltip::font),
                Codec.STRING.fieldOf("nutrition_icon").forGetter(FoodTooltip::nutritionIcon),
                Codec.STRING.fieldOf("saturation_icon").forGetter(FoodTooltip::saturationIcon),
                TextColor.CODEC.optionalFieldOf("color").forGetter(FoodTooltip::color)
        ).apply(instance, FoodTooltip::new));
    }
}