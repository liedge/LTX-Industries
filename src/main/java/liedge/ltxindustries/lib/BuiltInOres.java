package liedge.ltxindustries.lib;

import net.minecraft.util.StringRepresentable;

public enum BuiltInOres implements StringRepresentable
{
    COAL("coal"),
    COPPER("copper"),
    IRON("iron"),
    LAPIS("lapis"),
    REDSTONE("redstone"),
    GOLD("gold"),
    DIAMOND("diamond"),
    EMERALD("emerald"),
    QUARTZ("quartz"),
    TITANIUM("titanium"),
    SILVER("silver"),
    NIOBIUM("niobium");

    private final String name;

    BuiltInOres(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}