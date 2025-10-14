package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record UpgradeDisplayInfo(Component title, Component description, List<UpgradeTooltip> tooltips, UpgradeIcon icon, String category) implements Comparable<UpgradeDisplayInfo>
{
    public static final String NO_CATEGORY = "";

    public static final Codec<UpgradeDisplayInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("title").forGetter(UpgradeDisplayInfo::title),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(UpgradeDisplayInfo::description),
            UpgradeTooltip.CODEC.listOf(1, 10).optionalFieldOf("tooltips", List.of()).forGetter(UpgradeDisplayInfo::tooltips),
            UpgradeIcon.CODEC.optionalFieldOf("icon", UpgradeIcon.noRenderIcon()).forGetter(UpgradeDisplayInfo::icon),
            Codec.STRING.optionalFieldOf("category", NO_CATEGORY).forGetter(UpgradeDisplayInfo::category))
            .apply(instance, UpgradeDisplayInfo::new));

    @Override
    public int compareTo(@NotNull UpgradeDisplayInfo o)
    {
        return this.category.compareTo(o.category);
    }
}