package liedge.ltxindustries.lib.icon;

public final class EmptyIcon implements ItemLikeIcon
{
    public static final EmptyIcon INSTANCE = new EmptyIcon();

    private EmptyIcon() { }

    @Override
    public ItemLikeIconType getType()
    {
        return ItemLikeIconType.NONE;
    }
}