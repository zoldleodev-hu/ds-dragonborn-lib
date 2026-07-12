package hu.zoldleo.dragonborn.common.test;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.types.CaveDragonType;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigOption;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigSide;
import by.dragonsurvivalteam.dragonsurvival.config.obj.ConfigType;
import hu.zoldleo.dragonborn.api.dragon_type.IBodyListProvider;
import hu.zoldleo.dragonborn.api.dragon_type.IDietProvider;
import hu.zoldleo.dragonborn.api.dragon_type.IDragonborn;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.List;

public class TestCaveDragonbornType extends CaveDragonType implements IDragonborn, IDietProvider, IBodyListProvider {
    @ConfigType(Item.class)
    @ConfigOption(
            side = ConfigSide.SERVER,
            category = {"food", "cave_dragonborn"},
            key = "caveDragonborn",
            comment = {"Dragon food formatting: mod_id:item_id:nutrition:saturation", "Nutrition / saturation values are optional as the human values will be used if missing.", "Saturation can be defined with decimals (e.g. 0.3)"}
    )
    public static List<String> caveDragonbornFoods = Arrays.asList("minecraft:diamond:2:2", "minecraft:planks:1:1");

    @Override
    public String getTypeName() {
        return "dragonborn_cave";
    }

    @Override
    public List<TagKey<Block>> mineableBlocks() {
        return List.of();
    }

    @Override
    public List<String> getDietConfig() {
        return caveDragonbornFoods;
    }

    @Override
    public boolean canEatHumanFood() {
        return true;
    }

    @Override
    public List<String> getBodies() {
        return List.of("dragonborn");
    }
}