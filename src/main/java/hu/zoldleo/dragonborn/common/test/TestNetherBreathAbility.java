package hu.zoldleo.dragonborn.common.test;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.NetherBreathAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import hu.zoldleo.dragonborn.Dragonborn;

@RegisterDragonAbility
public class TestNetherBreathAbility extends NetherBreathAbility {
    public String getName() {
        return "test_nether_breath";
    }

    public AbstractDragonType getDragonType() {
        return Dragonborn.TEST_TYPE;
    }
}