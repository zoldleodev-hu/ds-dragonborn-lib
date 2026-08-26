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

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.MiscCodecs;
import by.dragonsurvivalteam.dragonsurvival.common.codecs.ability.ActionContainer;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.FlightData;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.ClientEffectProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.AbilityTargeting;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.SelfTarget;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.emotes.DragonEmoteSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hu.zoldleo.dragonborn.Dragonborn;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@EventBusSubscriber
public record ShapeshiftForm(Optional<ResourceLocation> icon, ResourceLocation model, ResourceLocation animation, Holder<DragonEmoteSet> emotes, DragonBody.ScalingProportions scalingProportions, double crouchHeightRatio, boolean canUseCustomSkin) {
    public static final ResourceKey<Registry<ShapeshiftForm>> REGISTRY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Dragonborn.MODID, "shapeshift_form"));

    public static final Codec<Holder<ShapeshiftForm>> CODEC = RegistryFixedCodec.create(REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<ShapeshiftForm>> STREAM_CODEC = ByteBufCodecs.holderRegistry(REGISTRY);

    public static final Codec<ShapeshiftForm> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("icon").forGetter(ShapeshiftForm::icon),
            ResourceLocation.CODEC.fieldOf("model").forGetter(ShapeshiftForm::model),
            ResourceLocation.CODEC.fieldOf("animation").forGetter(ShapeshiftForm::animation),
            DragonEmoteSet.CODEC.fieldOf("emotes").forGetter(ShapeshiftForm::emotes),
            DragonBody.ScalingProportions.CODEC.fieldOf("scaling_proportions").forGetter(ShapeshiftForm::scalingProportions),
            MiscCodecs.doubleRange(0, 100).fieldOf("crouch_height_ratio").forGetter(ShapeshiftForm::crouchHeightRatio),
            Codec.BOOL.optionalFieldOf("can_use_custom_skin", false).forGetter(ShapeshiftForm::canUseCustomSkin)
    ).apply(instance, ShapeshiftForm::new));

    public static final ClientEffectProvider SHAPESHIFT_EFFECT = new ClientEffectProvider() {
        private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Dragonborn.MODID, "shapeshift_effect");

        public Component getDescription() {
            return Component.empty();
        }

        public ClientEffectProvider.ClientData clientData() {
            Player player = DragonSurvival.PROXY.getLocalPlayer();
            if (player != null && isTransformed(player)) {
                Holder<ShapeshiftForm> form = getData(player);
                Optional<ResourceLocation> icon = form.value().icon;
                if (icon.isPresent())
                    return new ClientEffectProvider.ClientData(ID, icon.get(), getName(form), CommonComponents.EMPTY);
            }
            return new ClientEffectProvider.ClientData(ID, FlightData.DEFAULT_ICON, getName(null), CommonComponents.EMPTY);
        }

        public int getDuration() {
            return -1;
        }

        public int currentDuration() {
            return 0;
        }
    };

    public static boolean isTransformed(Player player) {
        return player.hasData(Dragonborn.SHAPESHIFT_DATA);
    }

    public static Holder<ShapeshiftForm> getData(Player player) {
        return player.getData(Dragonborn.SHAPESHIFT_DATA);
    }

    public static MutableComponent getName(Holder<ShapeshiftForm> form) {
        return Component.translatable(form == null || form.getKey() == null ? "shapeshift_form.dragonborn_lib.empty" : form.getKey().location().toLanguageKey("shapeshift_form"));
    }

    public static List<Holder<ShapeshiftForm>> collectDefaultForms(DragonStateHandler handler) {
        if (!handler.isDragon())
            return List.of();
        return collectFormsFromAbilities(handler.species().value().abilities().stream().map(Holder::value));
    }

    public static List<Holder<ShapeshiftForm>> collectAllForms(Player player) {
        return collectFormsFromAbilities(MagicData.getData(player).getAbilities().values().stream().map(DragonAbilityInstance::value));
    }

    private static List<Holder<ShapeshiftForm>> collectFormsFromAbilities(Stream<DragonAbility> stream) {
        List<Holder<ShapeshiftForm>> list = new ArrayList<>();
        list.add(null);
        list.addAll(stream.map(DragonAbility::actions).map(x -> x.stream().map(ActionContainer::effect).filter(y -> y instanceof SelfTarget).map(AbilityTargeting::target).map(y -> y.right()).filter(Optional::isPresent).map(Optional::get).map(y -> y.effects().stream().filter(z -> z instanceof ShapeshiftAbilityEffect).map(z -> ((ShapeshiftAbilityEffect)z).form())).reduce(Stream.of(), Stream::concat)).reduce(Stream.of(), Stream::concat).toList());
        return list;
    }

    @SubscribeEvent
    public static void registerRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }
}