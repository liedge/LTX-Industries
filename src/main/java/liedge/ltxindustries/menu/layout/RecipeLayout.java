package liedge.ltxindustries.menu.layout;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public sealed interface RecipeLayout permits MenuLayoutMap
{
    static Builder builder()
    {
        return new Builder();
    }

    int progressBarX();

    int progressBarY();

    List<LayoutSlot> getSlotsForType(LayoutSlot.Type type);

    Stream<LayoutSlot> streamSlots();

    final class Builder
    {
        private final Map<LayoutSlot.Type, ObjectList<LayoutSlot>> map = new EnumMap<>(LayoutSlot.Type.class);

        private Builder() {}

        public Builder itemIn(int x, int y)
        {
            return put(LayoutSlot.Type.ITEM_INPUT, x,  y);
        }

        public Builder itemOut(int x, int y)
        {
            return put(LayoutSlot.Type.ITEM_OUTPUT, x, y);
        }

        public Builder fluidIn(int x, int y)
        {
            return put(LayoutSlot.Type.FLUID_INPUT, x, y);
        }

        public Builder fluidOut(int x, int y)
        {
            return put(LayoutSlot.Type.FLUID_OUTPUT, x, y);
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
            return map.computeIfAbsent(type, $ -> new ObjectArrayList<>());
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

            return new MenuLayoutMap(progressBarX, progressBarY, ImmutableMap.copyOf(out));
        }
    }
}