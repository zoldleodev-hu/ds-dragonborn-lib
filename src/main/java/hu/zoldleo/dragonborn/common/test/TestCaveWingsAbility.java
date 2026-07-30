package hu.zoldleo.dragonborn.common.test;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.innate.CaveWingsAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import hu.zoldleo.dragonborn.Dragonborn;

@RegisterDragonAbility
public class TestCaveWingsAbility extends CaveWingsAbility {
    @Override
    public String getName() {
        return "test_cave_wings";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return Dragonborn.TEST_TYPE;
    }
}