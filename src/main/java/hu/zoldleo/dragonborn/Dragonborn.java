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
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import com.mojang.serialization.MapCodec;
import hu.zoldleo.dragonborn.client.DragonbornRenderLayer;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftAbilityEffect;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftForm;
import hu.zoldleo.dragonborn.common.ability.ShapeshiftPredicate;
import hu.zoldleo.dragonborn.network.SyncShapeshift;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(Dragonborn.MODID)
public class Dragonborn {
    public static final String MODID = "dragonborn_lib";
    public static final TagKey<DragonSpecies> DRAGONBORN_SPECIES = TagKey.create(DragonSpecies.REGISTRY, DragonSurvival.res("dragonborn_species"));
    public static final TagKey<DragonSpecies> CAN_EAT_HUMAN_FOOD = TagKey.create(DragonSpecies.REGISTRY, DragonSurvival.res("can_eat_human_food"));
    public static final TagKey<DragonSpecies> HUMAN_CRAFTING_GRID = TagKey.create(DragonSpecies.REGISTRY, DragonSurvival.res("human_crafting_grid"));
    public static final TagKey<DragonSpecies> NO_CLAW_SLOTS = TagKey.create(DragonSpecies.REGISTRY, DragonSurvival.res("no_claw_slots"));
    //public static final TagKey<DragonSpecies> TRUE_DRAGONBORN = TagKey.create(DragonSpecies.REGISTRY, DragonSurvival.res("true_dragonborn")); // no need to define it here
    public static final TagKey<DragonBody> CAN_USE_CUSTOM_SKIN = TagKey.create(DragonBody.REGISTRY, DragonSurvival.res("can_use_custom_skin"));

    public static final DeferredRegister<MapCodec<? extends AbilityEntityEffect>> ABILITIES = DeferredRegister.create(AbilityEntityEffect.REGISTRY_KEY, Dragonborn.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Dragonborn.MODID);
    public static final DeferredRegister<MapCodec<? extends EntitySubPredicate>> PREDICATES = DeferredRegister.create(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE, MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Holder<ShapeshiftForm>>> SHAPESHIFT_DATA = ATTACHMENTS.register("shapeshift_data", () -> AttachmentType.<Holder<ShapeshiftForm>>builder(() -> null).serialize(ShapeshiftForm.CODEC).sync(ShapeshiftForm.STREAM_CODEC).build());

    public Dragonborn(IEventBus modEventBus) {
        ABILITIES.register(modEventBus);
        ATTACHMENTS.register(modEventBus);
        PREDICATES.register(modEventBus);

        modEventBus.addListener(Dragonborn::registerPayloads);
        if (FMLEnvironment.dist == Dist.CLIENT)
            modEventBus.addListener(ClientEvents::registerRenderLayers);
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(SyncShapeshift.TYPE, SyncShapeshift.STREAM_CODEC, SyncShapeshift::handleClient);
    }

    static {
        ABILITIES.register("shapeshift", () -> ShapeshiftAbilityEffect.CODEC);
        PREDICATES.register("shapeshift_predicate", () -> ShapeshiftPredicate.CODEC);
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientEvents {
        public static final ResourceLocation HUMAN_CRAFTING_GRID_BACKGROUND = DragonSurvival.res("textures/gui/inventory/dragon_inventory_alt.png");

        public static void registerRenderLayers(EntityRenderersEvent.AddLayers event) {
            for (PlayerSkin.Model model : event.getSkins())
                if (event.getSkin(model) instanceof PlayerRenderer renderer)
                    renderer.addLayer(new DragonbornRenderLayer(renderer));
        }
    }
}