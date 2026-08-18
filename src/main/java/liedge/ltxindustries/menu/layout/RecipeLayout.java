package liedge.ltxindustries.menu.layout;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class RecipeLayout
{
    static final int DEFAULT_WIDTH = 176;
    static final int DEFAULT_HEIGHT = 166;

    static Builder builder(int width, int height)
    {
        return new Builder(width, height);
    }

    static Builder builder()
    {
        return builder(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private final int width;
    private final int height;
    private final Map<LayoutSlot.Type, List<LayoutSlot>> slots;
    private final int progressBarX;
    private final int progressBarY;

    private RecipeLayout(int width, int height, Map<LayoutSlot.Type, List<LayoutSlot>> slots, int progressBarX, int progressBarY)
    {
        this.width = width;
        this.height = height;
        this.slots = slots;
        this.progressBarX = progressBarX;
        this.progressBarY = progressBarY;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getProgressBarX()
    {
        return progressBarX;
    }

    public int getProgressBarY()
    {
        return progressBarY;
    }

    public List<LayoutSlot> getSlotsForType(LayoutSlot.Type type)
    {
        return slots.get(type);
    }

    public Stream<LayoutSlot> streamSlots()
    {
        return slots.values().stream().flatMap(List::stream);
    }

    static final class Builder
    {
        private final Map<LayoutSlot.Type, ObjectList<LayoutSlot>> map = new EnumMap<>(LayoutSlot.Type.class);
        private final int width;
        private final int height;

        private Builder(int width, int height)
        {
            this.width = width;
            this.height = height;
        }

        public Builder slotGrid(int x0, int y0, int width, int height, LayoutSlot.Type slotType)
        {
            Preconditions.checkArgument(width > 0 && height > 0);

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    put(slotType, x0 + x * 18, y0 + y * 18);
                }
            }

            return this;
        }

        public Builder itemIn(int x, int y)
        {
            return put(LayoutSlot.Type.ITEM_INPUT, x, y);
        }

        public Builder itemsIn(int x, int y, int width, int height)
        {
            return slotGrid(x, y, width, height, LayoutSlot.Type.ITEM_INPUT);
        }

        public Builder itemOut(int x, int y)
        {
            return put(LayoutSlot.Type.ITEM_OUTPUT, x, y);
        }

        public Builder itemsOut(int x, int y, int width, int height)
        {
            return slotGrid(x, y, width, height, LayoutSlot.Type.ITEM_OUTPUT);
        }

        public Builder fluidIn(int x, int y)
        {
            return put(LayoutSlot.Type.FLUID_INPUT, x, y);
        }

        public Builder fluidsIn(int x, int y, int width, int height)
        {
            return slotGrid(x, y, width, height, LayoutSlot.Type.FLUID_INPUT);
        }

        public Builder fluidOut(int x, int y)
        {
            return put(LayoutSlot.Type.FLUID_OUTPUT, x, y);
        }

        public Builder fluidsOut(int x, int y, int width, int height)
        {
            return slotGrid(x, y, width, height, LayoutSlot.Type.FLUID_OUTPUT);
        }

        public Builder modeSlot(int x, int y)
        {
            return put(LayoutSlot.Type.RECIPE_MODE, x, y);
        }

        private Builder put(LayoutSlot.Type type, int x, int y)
        {
            getList(type).add(new LayoutSlot(x, y, type));
            return this;
        }

        private List<LayoutSlot> getList(LayoutSlot.Type type)
        {
            return map.computeIfAbsent(type, _ -> new ObjectArrayList<>());
        }

        public RecipeLayout build(int progressBarX, int progressBarY)
        {
            Map<LayoutSlot.Type, List<LayoutSlot>> out = new EnumMap<>(LayoutSlot.Type.class);

            for (LayoutSlot.Type type : LayoutSlot.Type.values())
            {
                ObjectList<LayoutSlot> builder = map.get(type);
                List<LayoutSlot> l = builder != null ? ObjectLists.unmodifiable(builder) : List.of();
                out.put(type, l);
            }

            return new RecipeLayout(width, height, ImmutableMap.copyOf(out), progressBarX, progressBarY);
        }
    }
}