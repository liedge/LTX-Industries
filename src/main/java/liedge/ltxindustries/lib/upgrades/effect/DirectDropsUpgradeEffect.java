package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public record DirectDropsUpgradeEffect(HolderSet<Item> items, Type type) implements UpgradeTooltipsProvider
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
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        Translatable tooltip = type == Type.BLOCK_DROPS ? LTXILangKeys.DIRECT_BLOCK_DROPS_EFFECT : LTXILangKeys.DIRECT_ENTITY_DROPS_EFFECT;
        lines.accept(tooltip.translateArgs(LTXITooltipUtil.translateHolderSet(items)).withStyle(LTXIConstants.LIME_GREEN.chatStyle()));
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