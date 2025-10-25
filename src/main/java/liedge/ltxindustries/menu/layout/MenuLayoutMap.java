package liedge.ltxindustries.menu.layout;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

record MenuLayoutMap(int progressBarX, int progressBarY, Map<LayoutSlot.Type, List<LayoutSlot>> map) implements RecipeLayout
{
    @Override
    public List<LayoutSlot> getSlotsForType(LayoutSlot.Type type)
    {
        return map.get(type);
    }

    @Override
    public Stream<LayoutSlot> streamSlots()
    {
        return map.values().stream().flatMap(List::stream);
    }
}