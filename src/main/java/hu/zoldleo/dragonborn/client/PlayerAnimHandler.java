package hu.zoldleo.dragonborn.client;

//import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
//import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler;
//import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.DragonBody;
//import by.dragonsurvivalteam.dragonsurvival.registry.dragon.body.emotes.DragonEmote;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
//import com.zigythebird.playeranim.animation.PlayerAnimResources;
//import com.zigythebird.playeranim.animation.PlayerAnimationController;
//import com.zigythebird.playeranimcore.animation.Animation;
//import com.zigythebird.playeranimcore.animation.AnimationController;
//import com.zigythebird.playeranimcore.animation.AnimationController.AnimationSetter;
//import com.zigythebird.playeranimcore.animation.AnimationData;
//import com.zigythebird.playeranimcore.animation.RawAnimation;
//import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
//import com.zigythebird.playeranimcore.animation.layered.modifier.SpeedModifier;
//import com.zigythebird.playeranimcore.enums.PlayState;
//import hu.zoldleo.dragonborn.Dragonborn;
//import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
//import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.datamaps.DataMapValueRemover;
import org.jetbrains.annotations.NotNull;

//import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerAnimHandler {
    /*/public static PlayState emotePredicate(AnimationController controller, AnimationData ignored, AnimationSetter animSetter, int slot) {
        PlayerAnimationController playerController = (PlayerAnimationController)controller;
        DragonStateHandler handler = DragonStateProvider.getData(playerController.getPlayer());
        if (!handler.isDragon())
            return PlayState.STOP;
        if (handler.refreshBody)
            controller.forceAnimationReset();
        DragonEmote dragonEmote = ClientDragonRenderer.getOrCreateDragon(playerController.getPlayer()).getCurrentlyPlayingEmotes()[slot];
        if (dragonEmote == null) {
            controller.forceAnimationReset();
            return PlayState.STOP;
        }
        AbstractModifier modifier = controller.getModifier(0);
        if (modifier instanceof SpeedModifier speed)
            speed.speed = (float)dragonEmote.speed();
        else
            controller.addModifier(new SpeedModifier((float) dragonEmote.speed()), 0);
        Animation emote = getEmote(playerController.getPlayer(), dragonEmote);
        if (!dragonEmote.loops())
            animSetter.setAnimation(RawAnimation.begin().thenPlay(emote));
        else
            animSetter.setAnimation(RawAnimation.begin().thenLoop(emote));
        return PlayState.CONTINUE;
    }

    public static PlayState animationPredicate(AnimationController controller, AnimationData ignored, AnimationSetter animSetter) {
        return PlayState.STOP;
    }

    @Nullable
    public static Animation getEmote(Player player, DragonEmote emote) {
        Holder<DragonBody> body = DragonStateProvider.getData(player).body();
        if (body == null)
            return null;
        Map<String, ResourceLocation> set = body.getData(Dragonborn.PLAYER_EMOTE_DATAMAP);
        if (set == null)
            return null;
        return PlayerAnimResources.getAnimation(set.get(emote.animationKey()));
    }*/

    public record DataMapAnimRemover(String key) implements DataMapValueRemover<DragonBody, Map<String, ResourceLocation>> {
        public static final Codec<DataMapAnimRemover> CODEC = Codec.STRING.xmap(DataMapAnimRemover::new, DataMapAnimRemover::key);

        @Override
        public @NotNull Optional<Map<String, ResourceLocation>> remove(@NotNull Map<String, ResourceLocation> value, @NotNull Registry<DragonBody> registry, @NotNull Either<TagKey<DragonBody>, ResourceKey<DragonBody>> source, @NotNull DragonBody object) {
            HashMap<String, ResourceLocation> map = new HashMap<>(value);
            map.remove(key);
            return Optional.of(map);
        }
    }
}