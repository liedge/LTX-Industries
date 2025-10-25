package liedge.ltxindustries.menu.layout;

import liedge.limacore.blockentity.BlockContentsType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public record LayoutSlot(int x, int y, Type type)
{
    public static final ResourceLocation ITEM_SLOT_SPRITE = RESOURCES.location("slot/empty");
    public static final ResourceLocation FLUID_SLOT_SPRITE = RESOURCES.location("slot/fluid");
    public static final ResourceLocation MODE_SLOT_SPRITE = RESOURCES.location("slot/mode");

    public enum Type
    {
        ITEM_INPUT(ITEM_SLOT_SPRITE, BlockContentsType.INPUT),
        FLUID_INPUT(FLUID_SLOT_SPRITE, BlockContentsType.INPUT),
        ITEM_OUTPUT(ITEM_SLOT_SPRITE, BlockContentsType.OUTPUT),
        FLUID_OUTPUT(FLUID_SLOT_SPRITE, BlockContentsType.OUTPUT),
        RECIPE_MODE(MODE_SLOT_SPRITE, null);

        private final ResourceLocation sprite;
        private final @Nullable BlockContentsType contentsType;

        Type(ResourceLocation sprite, @Nullable BlockContentsType contentsType)
        {
            this.sprite = sprite;
            this.contentsType = contentsType;
        }

        public ResourceLocation getSprite()
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