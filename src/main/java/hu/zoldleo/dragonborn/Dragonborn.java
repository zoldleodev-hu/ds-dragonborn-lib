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

import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonBodies;
import by.dragonsurvivalteam.dragonsurvival.common.dragon_types.DragonTypes;
import hu.zoldleo.dragonborn.common.test.TestCaveDragonbornType;
import hu.zoldleo.dragonborn.common.test.TestDragonbornBody;
import net.minecraftforge.fml.common.Mod;

@Mod(Dragonborn.MODID)
public class Dragonborn {
    public static final String MODID = "dragonborn_lib";
    public Dragonborn() {
        //TODO
        //region Test
        DragonTypes.registerType(TestCaveDragonbornType::new);
        DragonBodies.registerType(TestDragonbornBody::new);
        //endregion
    }
}