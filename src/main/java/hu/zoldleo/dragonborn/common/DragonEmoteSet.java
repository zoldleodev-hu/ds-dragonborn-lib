package hu.zoldleo.dragonborn.common;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvivalMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.List;

@Mod.EventBusSubscriber
public record DragonEmoteSet(List<DragonEmote> emotes) {
    public static final ResourceKey<Registry<DragonEmoteSet>> REGISTRY = ResourceKey.createRegistryKey(DragonSurvivalMod.res("dragon_emote_set"));

    public static final Codec<DragonEmoteSet> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DragonEmote.CODEC.listOf().fieldOf("emotes").forGetter(DragonEmoteSet::emotes)
    ).apply(instance, DragonEmoteSet::new));

    public static final Codec<Holder<DragonEmoteSet>> CODEC = RegistryFixedCodec.create(REGISTRY);

    @SubscribeEvent
    public static void register(final DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(REGISTRY, DIRECT_CODEC, DIRECT_CODEC);
    }

    public DragonEmote getEmote(String animationKey) {
        return emotes.stream().filter(emote -> emote.key().equals(animationKey)).findFirst().orElse(null);
    }
}