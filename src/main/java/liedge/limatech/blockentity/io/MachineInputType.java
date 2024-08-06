package liedge.limatech.blockentity.io;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import liedge.limatech.menu.MachineIOControlMenu;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum MachineInputType implements Translatable
{
    ITEMS("items"),
    ENERGY("energy"),
    FLUIDS("fluids");

    public static final StreamCodec<FriendlyByteBuf, MachineInputType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(MachineInputType.class);

    private final String _descriptionId;

    MachineInputType(String name)
    {
        this._descriptionId = LimaTech.RESOURCES.translationKey("input_type", "{}", name);
    }

    public LimaMenuType<SidedMachineIOHolder, MachineIOControlMenu> getControlMenuType()
    {
        return switch (this)
        {
            case ITEMS -> LimaTechMenus.ITEM_IO_CONTROL.get();
            case ENERGY -> LimaTechMenus.ENERGY_IO_CONTROL.get();
            case FLUIDS -> LimaTechMenus.FLUID_IO_CONTROL.get();
        };
    }

    @Override
    public String descriptionId()
    {
        return _descriptionId;
    }
}