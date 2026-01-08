package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueFormat;
import liedge.ltxindustries.lib.upgrades.tooltip.ValueSentiment;
import liedge.ltxindustries.lib.upgrades.value.ContextlessValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Consumer;

public record CancelFall(ContextlessValue energyActions) implements UpgradeTooltipsProvider
{
    public static final Codec<CancelFall> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ContextlessValue.CODEC.fieldOf("energy_actions").forGetter(CancelFall::energyActions))
            .apply(instance, CancelFall::new));

    public static CancelFall cancelFalls(ContextlessValue energyActions)
    {
        return new CancelFall(energyActions);
    }

    public boolean apply(int upgradeRank, UpgradedEquipmentInUse equipmentInUse, Entity affected, float fallDistance)
    {
        double safeDistance = LimaEntityUtil.getAttributeValueSafe(affected, Attributes.SAFE_FALL_DISTANCE);
        if (fallDistance <= safeDistance) return true;

        return equipmentInUse.useEnergyActions(energyActions.calculateInt(upgradeRank));
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        Component energyStr = ValueFormat.FLAT_NUMBER.apply(energyActions.calculateInt(upgradeRank), ValueSentiment.NEGATIVE);
        lines.accept(LTXILangKeys.CANCEL_FALLS_EFFECT.translateArgs(LTXILangKeys.ENERGY_ACTIONS_TOOLTIP.translateArgs(energyStr).withStyle(LTXIConstants.REM_BLUE.chatStyle())));
    }
}