package liedge.ltxindustries.lib;

import net.minecraft.util.StringRepresentable;

public enum BuiltInOres implements StringRepresentable
{
    // MC
    COAL("coal"),
    COPPER("copper"),
    IRON("iron"),
    LAPIS("lapis"),
    REDSTONE("redstone"),
    GOLD("gold"),
    DIAMOND("diamond"),
    EMERALD("emerald"),
    QUARTZ("quartz"),
    // LTXI
    TITANIUM("titanium"),
    SILVER("silver"),
    OLIVINE("olivine"),
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