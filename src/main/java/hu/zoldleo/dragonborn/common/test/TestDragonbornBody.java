package hu.zoldleo.dragonborn.common.test;

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.AbstractDragonBody;
import hu.zoldleo.dragonborn.api.dragon_body.ICustomAnimationProvider;
import hu.zoldleo.dragonborn.api.dragon_body.ICustomModelProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class TestDragonbornBody extends AbstractDragonBody implements ICustomModelProvider, ICustomAnimationProvider {
    @Override
    public String getBodyName() {
        return "dragonborn";
    }

    @Override
    public void onPlayerUpdate() {

    }

    @Override
    public void onPlayerDeath() {

    }

    @Override
    public CompoundTag writeNBT() {
        return new CompoundTag();
    }

    @Override
    public void readNBT(CompoundTag compoundTag) {

    }

    @Override
    public ResourceLocation modelResource() {
        return new ResourceLocation("dragonsurvival", "dragonborn");
    }

    @Override
    public ResourceLocation animResource() {
        return new ResourceLocation("dragonsurvival", "dragonborn");
    }
}