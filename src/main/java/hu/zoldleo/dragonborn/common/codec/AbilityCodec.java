package hu.zoldleo.dragonborn.common.codec;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.serialization.Codec;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AbilityCodec {
    public static Map<ResourceLocation, Codec<DataDrivenDragonAbility>> lookup = createLookup();

    protected static Map<ResourceLocation, Codec<DataDrivenDragonAbility>> createLookup() {
        HashMap<ResourceLocation, Codec<DataDrivenDragonAbility>> map = new HashMap<>();
        /*/map.put(DragonSurvivalMod.res("active"), ACTIVE);
        map.put(DragonSurvivalMod.res("aoe_buff"), AOE_BUFF);
        map.put(DragonSurvivalMod.res("breath"), BREATH);
        map.put(DragonSurvivalMod.res("channeling"), CHANNELING);
        map.put(DragonSurvivalMod.res("charge"), CHARGE);
        map.put(DragonSurvivalMod.res("instant"), INSTANT);

        map.put(DragonSurvivalMod.res("innate"), INNATE);
        map.put(DragonSurvivalMod.res("wing"), WING);
        map.put(DragonSurvivalMod.res("claws"), CLAWS);

        map.put(DragonSurvivalMod.res("passive"), PASSIVE);
        map.put(DragonSurvivalMod.res("athletics"), ATHLETICS);
        map.put(DragonSurvivalMod.res("magic"), MAGIC);
        map.put(DragonSurvivalMod.res("tickable"), TICKABLE);*/
        return map;
    }

    /*/public static final Codec<DataDrivenDragonAbility> ACTIVE;
    public static final Codec<DataDrivenDragonAbility> AOE_BUFF;
    public static final Codec<DataDrivenDragonAbility> BREATH;
    public static final Codec<DataDrivenDragonAbility> CHANNELING;
    public static final Codec<DataDrivenDragonAbility> CHARGE;
    public static final Codec<DataDrivenDragonAbility> INSTANT;

    public static final Codec<DataDrivenDragonAbility> INNATE;
    public static final Codec<DataDrivenDragonAbility> WING;
    public static final Codec<DataDrivenDragonAbility> CLAWS;

    public static final Codec<DataDrivenDragonAbility> PASSIVE;
    public static final Codec<DataDrivenDragonAbility> ATHLETICS;
    public static final Codec<DataDrivenDragonAbility> MAGIC;
    public static final Codec<DataDrivenDragonAbility> TICKABLE;*/
}