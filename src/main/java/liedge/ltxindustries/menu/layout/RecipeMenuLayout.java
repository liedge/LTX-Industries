package liedge.ltxindustries.menu.layout;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.List;

public record RecipeMenuLayout(int progressBarX, int progressBarY,
                               List<LayoutSlot> itemInputSlots,
                               List<LayoutSlot> itemOutputSlots,
                               List<LayoutSlot> fluidInputSlots,
                               List<LayoutSlot> fluidOutputSlots)
{
    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ObjectList<LayoutSlot> itemInputs = new ObjectArrayList<>();
        private final ObjectList<LayoutSlot> itemOutputs = new ObjectArrayList<>();
        private final ObjectList<LayoutSlot> fluidInputs = new ObjectArrayList<>();
        private final ObjectList<LayoutSlot> fluidOutputs = new ObjectArrayList<>();

        private Builder() {}

        public Builder itemIn(int x, int y)
        {
            itemInputs.add(LayoutSlot.itemSlot(x, y));
            return this;
        }

        public Builder itemOut(int x, int y)
        {
            itemOutputs.add(LayoutSlot.itemSlot(x, y));
            return this;
        }

        public Builder fluidIn(int x, int y)
        {
            fluidInputs.add(LayoutSlot.fluidSlot(x, y));
            return this;
        }

        public Builder fluidOut(int x, int y)
        {
            fluidOutputs.add(LayoutSlot.fluidSlot(x, y));
            return this;
        }

        public RecipeMenuLayout build(int progressBarX, int progressBarY)
        {
            return new RecipeMenuLayout(progressBarX, progressBarY, ObjectLists.unmodifiable(itemInputs), ObjectLists.unmodifiable(itemOutputs), ObjectLists.unmodifiable(fluidInputs), ObjectLists.unmodifiable(fluidOutputs));
        }
    }
}