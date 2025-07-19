package liedge.ltxindustries.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.EffectTooltipProvider;
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
import org.jetbrains.annotations.Nullable;

public record PreventVibrationUpgradeEffect(EquipmentSlotGroup slotGroup, HolderSet<GameEvent> targetVibrations) implements EffectTooltipProvider.SingleLine
{
    public static final Codec<PreventVibrationUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(PreventVibrationUpgradeEffect::slotGroup),
            RegistryCodecs.homogeneousList(Registries.GAME_EVENT).fieldOf("target").forGetter(PreventVibrationUpgradeEffect::targetVibrations))
            .apply(instance, PreventVibrationUpgradeEffect::new));

    public static PreventVibrationUpgradeEffect of(EquipmentSlotGroup group, Holder<GameEvent> event)
    {
        return new PreventVibrationUpgradeEffect(group, HolderSet.direct(event));
    }

    public static PreventVibrationUpgradeEffect alwaysSuppress(HolderSet<GameEvent> targetVibrations)
    {
        return new PreventVibrationUpgradeEffect(EquipmentSlotGroup.ANY, targetVibrations);
    }

    public static PreventVibrationUpgradeEffect suppressMainHand(HolderSet<GameEvent> targetVibrations)
    {
        return new PreventVibrationUpgradeEffect(EquipmentSlotGroup.MAINHAND, targetVibrations);
    }

    public static PreventVibrationUpgradeEffect suppressHands(HolderSet<GameEvent> targetVibrations)
    {
        return new PreventVibrationUpgradeEffect(EquipmentSlotGroup.HAND, targetVibrations);
    }

    /**
     * Determines if the given {@link GameEvent} should be suppressed.
     * @param slot The slot containing the equipment. If {@code null} always succeeds slot validation.
     * @param event The game event being fired.
     * @return {@code true} if the game event will be cancelled.
     */
    public boolean apply(@Nullable EquipmentSlot slot, Holder<GameEvent> event)
    {
        if (slot != null && !slotGroup.test(slot)) return false;
        return targetVibrations.size() == 0 || targetVibrations.contains(event);
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        MutableComponent slotTooltip = Component.translatable("item.modifiers." + slotGroup.getSerializedName()).append(CommonComponents.SPACE);
        slotTooltip.append(LTXILangKeys.SUPPRESS_VIBRATIONS_EFFECT.translateArgs(LTXITooltipUtil.translateHolderSet(targetVibrations)));
        return slotTooltip.withStyle(ChatFormatting.DARK_AQUA);
    }
}