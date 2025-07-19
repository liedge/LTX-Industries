package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import org.jetbrains.annotations.NotNull;

public record UpgradeDisplayInfo(Component title, Component description, UpgradeIcon icon, String category) implements Comparable<UpgradeDisplayInfo>
{
    public static final String NO_CATEGORY = "";

    public static final Codec<UpgradeDisplayInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("title").forGetter(UpgradeDisplayInfo::title),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(UpgradeDisplayInfo::description),
            UpgradeIcon.CODEC.optionalFieldOf("icon", UpgradeIcon.noRenderIcon()).forGetter(UpgradeDisplayInfo::icon),
            Codec.STRING.optionalFieldOf("category", NO_CATEGORY).forGetter(UpgradeDisplayInfo::category))
            .apply(instance, UpgradeDisplayInfo::new));

    @Override
    public int compareTo(@NotNull UpgradeDisplayInfo o)
    {
        return this.category.compareTo(o.category);
    }
}