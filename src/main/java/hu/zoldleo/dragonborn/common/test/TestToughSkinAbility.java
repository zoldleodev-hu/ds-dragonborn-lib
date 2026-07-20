package hu.zoldleo.dragonborn.common.test;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.ToughSkinAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import hu.zoldleo.dragonborn.Dragonborn;

@RegisterDragonAbility
public class TestToughSkinAbility extends ToughSkinAbility {
    public String getName() {
        return "test_strong_leather";
    }

    public AbstractDragonType getDragonType() {
        return Dragonborn.TEST_TYPE;
    }
}