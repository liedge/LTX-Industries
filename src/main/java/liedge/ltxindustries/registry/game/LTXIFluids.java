package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;

public final class LTXIFluids
{
    private LTXIFluids() {}

    private static final DeferredRegister<FluidType> TYPES = LTXIndustries.RESOURCES.deferredRegister(NeoForgeRegistries.FLUID_TYPES);

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static void registerFluids(RegisterEvent.RegisterHelper<Fluid> helper)
    {
        BaseFlowingFluid.Properties viridicAcidProps = new BaseFlowingFluid.Properties(VIRIDIC_ACID_TYPE::value, VIRIDIC_ACID::value, FLOWING_VIRIDIC_ACID::value)
                .block(LTXIBlocks.VIRIDIC_ACID_BLOCK)
                .bucket(LTXIItems.VIRIDIC_ACID_BUCKET);
        helper.register(VIRIDIC_ACID.getId(), new BaseFlowingFluid.Source(viridicAcidProps));
        helper.register(FLOWING_VIRIDIC_ACID.getId(), new BaseFlowingFluid.Flowing(viridicAcidProps));
    }

    // Light levels
    public static final int VIRIDIC_ACID_LIGHT = 7;

    // Fluid Types
    public static final DeferredHolder<FluidType, FluidType> VIRIDIC_ACID_TYPE = TYPES.register("viridic_acid", () -> new FluidType(FluidType.Properties.create().lightLevel(VIRIDIC_ACID_LIGHT)));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> VIRIDIC_ACID = fluidHolder("viridic_acid");
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_VIRIDIC_ACID = fluidHolder("flowing_viridic_acid");

    private static <T extends BaseFlowingFluid> DeferredHolder<Fluid, T> fluidHolder(String name)
    {
        return DeferredHolder.create(Registries.FLUID, LTXIndustries.RESOURCES.location(name));
    }
}