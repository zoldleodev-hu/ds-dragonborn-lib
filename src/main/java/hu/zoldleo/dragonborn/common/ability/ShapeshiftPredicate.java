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

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ShapeshiftPredicate(Optional<HolderSet<ShapeshiftForm>> form, boolean transformed) implements EntitySubPredicate {
    public static final MapCodec<ShapeshiftPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(ShapeshiftForm.REGISTRY).optionalFieldOf("form").forGetter(ShapeshiftPredicate::form),
            Codec.BOOL.optionalFieldOf("transformed", true).forGetter(ShapeshiftPredicate::transformed)
    ).apply(instance, ShapeshiftPredicate::new));

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(@NotNull Entity entity, @NotNull ServerLevel serverLevel, @Nullable Vec3 vec3) {
        if (!(entity instanceof ServerPlayer player))
            return false;
        Holder<ShapeshiftForm> shapeshiftForm = ShapeshiftForm.isTransformed(player) ? ShapeshiftForm.getData(player) : null;
        if (!transformed)
            return shapeshiftForm == null;
        if (shapeshiftForm == null)
            return false;
        return form.isEmpty() || form.get().contains(shapeshiftForm);
    }
}