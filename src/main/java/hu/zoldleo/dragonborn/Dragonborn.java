//  This file is part of Dragonborn lib.
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
//  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
//  USA
//
//  zoldleo.dev@gmail.com

package hu.zoldleo.dragonborn;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import hu.zoldleo.dragonborn.registry.DragonbornContainers;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Dragonborn.MODID)
public class Dragonborn {
    public static final String MODID = "dragonborn_lib";
    public static final TagKey<DragonSpecies> DRAGONBORN_SPECIES = TagKey.create(DragonSpecies.REGISTRY, DragonSurvival.res("dragonborn_species"));
    public static final TagKey<DragonSpecies> CAN_EAT_HUMAN_FOOD = TagKey.create(DragonSpecies.REGISTRY, DragonSurvival.res("can_eat_human_food"));
    public static final TagKey<DragonBody> CAN_USE_CUSTOM_SKIN = TagKey.create(DragonBody.REGISTRY, DragonSurvival.res("can_use_custom_skin"));

    public Dragonborn(IEventBus modEventBus) {
        DragonbornContainers.REGISTRY.register(modEventBus);
    }
}