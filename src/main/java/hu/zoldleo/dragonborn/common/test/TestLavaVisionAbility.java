package hu.zoldleo.dragonborn.common.test;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonType;
import by.dragonsurvivalteam.dragonsurvival.magic.abilities.CaveDragon.active.LavaVisionAbility;
import by.dragonsurvivalteam.dragonsurvival.magic.common.RegisterDragonAbility;
import hu.zoldleo.dragonborn.Dragonborn;

@RegisterDragonAbility
public class TestLavaVisionAbility extends LavaVisionAbility {
    public String getName() {
        return "test_lava_vision";
    }

    public AbstractDragonType getDragonType() {
        return Dragonborn.TEST_TYPE;
    }
}