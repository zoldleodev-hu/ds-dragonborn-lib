package hu.zoldleo.dragonborn;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import hu.zoldleo.dragonborn.registry.DragonbornContainers;
import hu.zoldleo.dragonborn.registry.DragonbornEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Dragonborn.MODID)
public class Dragonborn {
    public static final String MODID = "dragonborn_lib";
    public static final TagKey<DragonSpecies> DRAGONBORN_SPECIES = TagKey.create(DragonSpecies.REGISTRY, res("dragonborn_species"));
    public static final TagKey<DragonBody> DRAGONBORN_BODIES = TagKey.create(DragonBody.REGISTRY, res("dragonborn_bodies"));

    public Dragonborn(IEventBus modEventBus, ModContainer modContainer) {
        DragonbornEntities.REGISTRY.register(modEventBus);
        DragonbornContainers.REGISTRY.register(modEventBus);
    }

    public static ResourceLocation res(final String path) {
        return DragonSurvival.location(Dragonborn.MODID, path);
    }
}

//  Dragonborn lib: An addon for dragonsurvival that allows the use of the playermodel for custom species.
//  Copyright (C) 2025  ZoldLeo
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//
//  zoldleo.dev@gmail.com