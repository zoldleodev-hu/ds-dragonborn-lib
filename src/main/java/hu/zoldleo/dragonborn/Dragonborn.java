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

import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonAbility;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonBody;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonDiet;
import hu.zoldleo.dragonborn.common.datadriven.DataDrivenDragonType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod(Dragonborn.MODID)
public class Dragonborn {
    public static final String MODID = "dragonborn_lib";
    public Dragonborn() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(Dragonborn::datapackRegistryEvent);
    }

    public static void datapackRegistryEvent(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(DataDrivenDragonType.REGISTRY, DataDrivenDragonType.DIRECT_CODEC);
        event.dataPackRegistry(DataDrivenDragonBody.REGISTRY, DataDrivenDragonBody.DIRECT_CODEC);
        event.dataPackRegistry(DataDrivenDragonAbility.REGISTRY, DataDrivenDragonAbility.DIRECT_CODEC);
        event.dataPackRegistry(DataDrivenDragonDiet.REGISTRY, DataDrivenDragonDiet.DietModifier.DIRECT_CODEC);
    }
}