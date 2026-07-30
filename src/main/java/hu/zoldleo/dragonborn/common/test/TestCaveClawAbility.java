package hu.zoldleo.dragonborn.common.test;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.innate.CaveClawAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import hu.zoldleo.dragonborn.Dragonborn;

@RegisterDragonAbility
public class TestCaveClawAbility extends CaveClawAbility {
    @Override
    public String getName() {
        return "test_cave_claws_and_teeth";
    }

    @Override
    public AbstractDragonType getDragonType() {
        return Dragonborn.TEST_TYPE;
    }
}