package liedge.ltxindustries.menu.layout;

import liedge.limacore.blockentity.BlockContentsType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public record LayoutSlot(int x, int y, Type type)
{
    public static final Identifier ITEM_SLOT_SPRITE = RESOURCES.id("slot/empty");
    public static final Identifier FLUID_SLOT_SPRITE = RESOURCES.id("slot/fluid");
    public static final Identifier MODE_SLOT_SPRITE = RESOURCES.id("slot/mode");

    public enum Type
    {
        ITEM_INPUT(ITEM_SLOT_SPRITE, BlockContentsType.INPUT),
        FLUID_INPUT(FLUID_SLOT_SPRITE, BlockContentsType.INPUT),
        ITEM_OUTPUT(ITEM_SLOT_SPRITE, BlockContentsType.OUTPUT),
        FLUID_OUTPUT(FLUID_SLOT_SPRITE, BlockContentsType.OUTPUT),
        RECIPE_MODE(MODE_SLOT_SPRITE, null);

        private final Identifier sprite;
        private final @Nullable BlockContentsType contentsType;

        Type(Identifier sprite, @Nullable BlockContentsType contentsType)
        {
            this.sprite = sprite;
            this.contentsType = contentsType;
        }

        public Identifier getSprite()
        {
            return sprite;
        }

        @Nullable
        public BlockContentsType getContentsType()
        {
            return contentsType;
        }
    }
}