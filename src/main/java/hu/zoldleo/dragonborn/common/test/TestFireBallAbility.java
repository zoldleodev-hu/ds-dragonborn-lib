package hu.zoldleo.dragonborn.common.test;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.FireBallAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import hu.zoldleo.dragonborn.Dragonborn;

@RegisterDragonAbility
public class TestFireBallAbility extends FireBallAbility {
    public String getName() {
        return "test_fireball";
    }

    public AbstractDragonType getDragonType() {
        return Dragonborn.TEST_TYPE;
    }
}