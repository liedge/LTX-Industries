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

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

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
        registerFluid(helper, VIRIDIC_ACID_TYPE, VIRIDIC_ACID, FLOWING_VIRIDIC_ACID, properties -> properties.block(LTXIBlocks.VIRIDIC_ACID_BLOCK).bucket(LTXIItems.VIRIDIC_ACID_BUCKET));
        registerFluid(helper, HYDROGEN_TYPE, HYDROGEN, FLOWING_HYDROGEN, properties -> properties.bucket(LTXIItems.HYDROGEN_BUCKET));
        registerFluid(helper, OXYGEN_TYPE, OXYGEN, FLOWING_OXYGEN, properties -> properties.bucket(LTXIItems.OXYGEN_BUCKET));
    }

    // Light levels
    public static final int VIRIDIC_ACID_LIGHT = 7;

    // Fluid Types
    public static final DeferredHolder<FluidType, FluidType> VIRIDIC_ACID_TYPE = TYPES.register("viridic_acid", () -> new FluidType(FluidType.Properties.create().lightLevel(VIRIDIC_ACID_LIGHT)));
    public static final DeferredHolder<FluidType, FluidType> HYDROGEN_TYPE = TYPES.register("hydrogen", () -> new FluidType(FluidType.Properties.create()));
    public static final DeferredHolder<FluidType, FluidType> OXYGEN_TYPE = TYPES.register("oxygen", () -> new FluidType(FluidType.Properties.create()));

    // Fluids
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> VIRIDIC_ACID = source(VIRIDIC_ACID_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_VIRIDIC_ACID = flowing(VIRIDIC_ACID_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> HYDROGEN = source(HYDROGEN_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_HYDROGEN = flowing(HYDROGEN_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> OXYGEN = source(OXYGEN_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_OXYGEN = flowing(OXYGEN_TYPE);

    private static DeferredHolder<Fluid, BaseFlowingFluid.Source> source(DeferredHolder<FluidType, ?> typeHolder)
    {
        return DeferredHolder.create(Registries.FLUID, typeHolder.getId());
    }

    private static DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing(DeferredHolder<FluidType, ?> typeHolder)
    {
        return DeferredHolder.create(Registries.FLUID, typeHolder.getId().withPrefix("flowing_"));
    }

    private static void registerFluid(RegisterEvent.RegisterHelper<Fluid> helper, Supplier<FluidType> typeSupplier,
                                      DeferredHolder<Fluid, BaseFlowingFluid.Source> sourceHolder, DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingHolder, UnaryOperator<BaseFlowingFluid.Properties> propertiesOp)
    {
        BaseFlowingFluid.Properties properties = propertiesOp.apply(new BaseFlowingFluid.Properties(typeSupplier, sourceHolder, flowingHolder));
        helper.register(sourceHolder.getId(), new BaseFlowingFluid.Source(properties));
        helper.register(flowingHolder.getId(), new BaseFlowingFluid.Flowing(properties));
    }
}