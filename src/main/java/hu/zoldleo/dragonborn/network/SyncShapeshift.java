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

package hu.zoldleo.dragonborn.network;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import hu.zoldleo.dragonborn.Dragonborn;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncShapeshift(int playerId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncShapeshift> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Dragonborn.MODID, "sync_animation"));
    public static final StreamCodec<ByteBuf, SyncShapeshift> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncShapeshift::playerId,
            SyncShapeshift::new
    );


    public static void handleClient(SyncShapeshift packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(packet.playerId());
            if (entity instanceof Player player) {
                player.refreshDimensions();
                DragonEntity dragon = ClientDragonRenderer.getDragon(player);
                if (dragon != null) {
                    dragon.mainAnimationController.forceAnimationReset();
                    dragon.stopAllEmotes();
                }
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}