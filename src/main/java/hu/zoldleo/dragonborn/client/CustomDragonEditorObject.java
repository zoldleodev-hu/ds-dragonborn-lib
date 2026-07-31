package hu.zoldleo.dragonborn.client;

import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.EnumSkinLayer;
import by.dragonsurvivalteam.dragonsurvival.client.skin_editor_system.objects.DragonEditorObject;
import by.dragonsurvivalteam.dragonsurvival.util.DragonLevel;

import java.util.HashMap;

public class CustomDragonEditorObject {
    public HashMap<DragonLevel, HashMap<EnumSkinLayer, String>> defaults = new HashMap<>();
    public DragonEditorObject.Dragon parts;
}
