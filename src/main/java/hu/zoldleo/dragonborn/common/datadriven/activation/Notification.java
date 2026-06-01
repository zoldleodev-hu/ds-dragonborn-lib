package hu.zoldleo.dragonborn.common.datadriven.activation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public record Notification(String notEnoughMana, Optional<String> usageBlocked) {
    public static final String NO_MANA = "dragonsurvival.gui.ability.no_mana";
    public static final Component NO_MANA_MESSAGE = Component.translatable(NO_MANA).withStyle(ChatFormatting.RED);

    public static Codec<Notification> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("not_enough_mana", NO_MANA).forGetter(Notification::notEnoughMana),
            Codec.STRING.optionalFieldOf("usage_blocked").forGetter(Notification::usageBlocked)
    ).apply(instance, Notification::new));

    public Component notEnoughManaComponent() {
        return notEnoughMana.isEmpty() ? Component.empty() : Component.translatable(notEnoughMana).withStyle(ChatFormatting.RED);
    }

    public Component usageBlockedComponent() {
        return usageBlocked.map(s -> Component.translatable(s).withStyle(ChatFormatting.RED)).orElse(Component.empty());
    }

    public static final Notification DEFAULT = new Notification(NO_MANA, Optional.empty());
    public static final Notification NONE = new Notification("", Optional.empty());
}