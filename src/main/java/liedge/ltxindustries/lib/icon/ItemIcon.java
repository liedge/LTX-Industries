package liedge.ltxindustries.lib.icon;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public final class ItemIcon implements ItemLikeIcon
{
    public static final MapCodec<ItemIcon> CODEC = ItemStackTemplate.MAP_CODEC.xmap(ItemIcon::new, ItemIcon::getTemplate);

    public static ItemIcon of(ItemStackTemplate template)
    {
        return new ItemIcon(template);
    }

    public static ItemIcon of(ItemLike itemLike)
    {
        return of(new ItemStackTemplate(itemLike.asItem()));
    }

    private final ItemStackTemplate template;
    private @Nullable ItemStack renderStack;

    public ItemIcon(ItemStackTemplate template)
    {
        this.template = template;
    }

    public ItemStackTemplate getTemplate()
    {
        return template;
    }

    @ApiStatus.Internal
    public ItemStack getRenderStack()
    {
        if (renderStack == null) renderStack = template.create();

        return renderStack;
    }

    @Override
    public ItemLikeIconType getType()
    {
        return ItemLikeIconType.ITEM;
    }
}