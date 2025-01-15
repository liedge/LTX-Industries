package liedge.limatech.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.weapons.GrenadeType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record GrenadeUnlockUpgradeEffect(GrenadeType grenadeType) implements UpgradeEffect
{
    public static final Codec<GrenadeUnlockUpgradeEffect> CODEC = GrenadeType.CODEC.xmap(GrenadeUnlockUpgradeEffect::new, GrenadeUnlockUpgradeEffect::grenadeType);

    @Override
    public Component defaultEffectTooltip(int upgradeRank)
    {
        return LimaTechLang.GRENADE_UNLOCK_EFFECT.translateArgs(grenadeType.translate());
    }

    public record DataType(ResourceLocation id) implements UpgradeEffectDataType<List<GrenadeUnlockUpgradeEffect>>
    {
        @Override
        public Codec<List<GrenadeUnlockUpgradeEffect>> codec()
        {
            return GrenadeUnlockUpgradeEffect.CODEC.listOf();
        }

        @Override
        public void appendTooltipLines(List<GrenadeUnlockUpgradeEffect> effectData, int upgradeRank, List<Component> lines)
        {
            Component baseComponent = ComponentUtils.formatList(effectData, effect -> effect.grenadeType.translate());
            lines.add(LimaTechLang.GRENADE_UNLOCK_EFFECT.translateArgs(baseComponent));
        }
    }
}