package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgrades.effect.EffectTooltipProvider;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

public record DirectDropsUpgradeEffect(HolderSet<Item> items, Type type) implements EffectTooltipProvider.SingleLine
{
    public static final Codec<DirectDropsUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(DirectDropsUpgradeEffect::items),
            Type.CODEC.fieldOf("type").forGetter(DirectDropsUpgradeEffect::type))
            .apply(instance, DirectDropsUpgradeEffect::new));

    public static DirectDropsUpgradeEffect blocksOnly(HolderSet<Item> items)
    {
        return new DirectDropsUpgradeEffect(items, Type.BLOCK_DROPS);
    }

    public static DirectDropsUpgradeEffect entityDrops(HolderSet<Item> items)
    {
        return new DirectDropsUpgradeEffect(items, Type.ENTITY_DROPS);
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        Translatable tooltip = type == Type.BLOCK_DROPS ? LimaTechLang.DIRECT_BLOCK_DROPS_EFFECT : LimaTechLang.DIRECT_ENTITY_DROPS_EFFECT;
        return tooltip.translateArgs(LimaTechTooltipUtil.translateHolderSet(items)).withStyle(LimaTechConstants.LIME_GREEN.chatStyle());
    }

    public enum Type implements StringRepresentable
    {
        BLOCK_DROPS("block"),
        ENTITY_DROPS("entity");

        private static final Codec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;

        Type(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }
}