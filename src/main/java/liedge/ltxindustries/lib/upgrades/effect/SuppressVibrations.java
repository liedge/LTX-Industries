package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

public record SuppressVibrations(EquipmentSlotGroup slots, HolderSet<GameEvent> vibrations) implements UpgradeTooltipsProvider, BiPredicate<EquipmentSlot, Holder<GameEvent>>
{
    public static final Codec<SuppressVibrations> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(SuppressVibrations::slots),
            RegistryCodecs.homogeneousList(Registries.GAME_EVENT).fieldOf("target").forGetter(SuppressVibrations::vibrations))
            .apply(instance, SuppressVibrations::new));

    public static SuppressVibrations forSlot(EquipmentSlotGroup slots, HolderSet<GameEvent> vibrations)
    {
        return new SuppressVibrations(slots, vibrations);
    }

    public static SuppressVibrations mainHand(HolderSet<GameEvent> vibrations)
    {
        return forSlot(EquipmentSlotGroup.MAINHAND, vibrations);
    }

    @Override
    public boolean test(EquipmentSlot slot, Holder<GameEvent> event)
    {
        return slots.test(slot) && (vibrations.size() == 0 || vibrations.contains(event));
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        MutableComponent slotTooltip = Component.translatable("item.modifiers." + slots.getSerializedName()).append(CommonComponents.SPACE);
        slotTooltip.append(LTXILangKeys.SUPPRESS_VIBRATIONS_EFFECT.translateArgs(LTXITooltipUtil.translateHolderSet(vibrations)));
        lines.accept(slotTooltip.withStyle(ChatFormatting.DARK_AQUA));
    }
}