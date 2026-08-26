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

package hu.zoldleo.dragonborn.common.ability;

import by.dragonsurvivalteam.dragonsurvival.registry.attachments.DSDataAttachments;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.util.DSColors;
import com.mojang.serialization.MapCodec;
import hu.zoldleo.dragonborn.Dragonborn;
import hu.zoldleo.dragonborn.network.SyncShapeshift;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public record ShapeshiftAbilityEffect(Holder<ShapeshiftForm> form) implements AbilityEntityEffect {
    public static final MapCodec<ShapeshiftAbilityEffect> CODEC = ShapeshiftForm.CODEC.fieldOf("form").xmap(ShapeshiftAbilityEffect::new, ShapeshiftAbilityEffect::form);

    @Override
    public void apply(ServerPlayer player, DragonAbilityInstance ability, Entity target) {
        if (target instanceof Player && target.getData(DSDataAttachments.DRAGON_HANDLER).isDragon()) {
            if (ShapeshiftForm.isTransformed(player) && ShapeshiftForm.getData(player).equals(form))
                target.removeData(Dragonborn.SHAPESHIFT_DATA);
            else
                target.setData(Dragonborn.SHAPESHIFT_DATA, form);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new SyncShapeshift(target.getId()));
        }
    }

    public List<MutableComponent> getDescription(Player dragon, DragonAbilityInstance ability) {
        return List.of(Component.translatable("dragonsurvival.gui.shapeshift", DSColors.dynamicValue(ShapeshiftForm.getName(form))));
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}