package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
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
        registerFluid(helper, HYDROGEN_TYPE, HYDROGEN, FLOWING_HYDROGEN, properties -> properties.bucket(LTXIItems.HYDROGEN_BUCKET));
        registerFluid(helper, NITROGEN_TYPE, NITROGEN, FLOWING_NITROGEN, properties -> properties.bucket(LTXIItems.NITROGEN_BUCKET));
        registerFluid(helper, OXYGEN_TYPE, OXYGEN, FLOWING_OXYGEN, properties -> properties.bucket(LTXIItems.OXYGEN_BUCKET));
        registerFluid(helper, CHLORINE_TYPE, CHLORINE, FLOWING_CHLORINE, properties -> properties.bucket(LTXIItems.CHLORINE_BUCKET));
        registerFluid(helper, ARGON_TYPE, ARGON, FLOWING_ARGON, properties -> properties.bucket(LTXIItems.ARGON_BUCKET));
        registerFluid(helper, SEA_WATER_TYPE, SEA_WATER, FLOWING_SEA_WATER, properties -> properties.bucket(LTXIItems.SEA_WATER_BUCKET));
        registerFluid(helper, VIRIDIC_ACID_TYPE, VIRIDIC_ACID, FLOWING_VIRIDIC_ACID, properties -> properties.block(LTXIBlocks.VIRIDIC_ACID_BLOCK).bucket(LTXIItems.VIRIDIC_ACID_BUCKET));
    }

    // Light levels
    public static final int VIRIDIC_ACID_LIGHT = 7;

    //#region Fluid Types
    public static final DeferredHolder<FluidType, FluidType> HYDROGEN_TYPE = registerType("hydrogen", UnaryOperator.identity());
    public static final DeferredHolder<FluidType, FluidType> NITROGEN_TYPE = registerType("nitrogen", UnaryOperator.identity());
    public static final DeferredHolder<FluidType, FluidType> OXYGEN_TYPE = registerType("oxygen", UnaryOperator.identity());
    public static final DeferredHolder<FluidType, FluidType> CHLORINE_TYPE = registerType("chlorine", UnaryOperator.identity());
    public static final DeferredHolder<FluidType, FluidType> ARGON_TYPE = registerType("argon", UnaryOperator.identity());
    public static final DeferredHolder<FluidType, FluidType> SEA_WATER_TYPE = registerType("sea_water", UnaryOperator.identity());
    public static final DeferredHolder<FluidType, FluidType> VIRIDIC_ACID_TYPE = registerType("viridic_acid", properties -> properties.lightLevel(VIRIDIC_ACID_LIGHT));
    //#endregion

    //#region Fluids
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> HYDROGEN = source(HYDROGEN_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_HYDROGEN = flowing(HYDROGEN_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> NITROGEN = source(NITROGEN_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_NITROGEN = flowing(NITROGEN_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> OXYGEN = source(OXYGEN_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_OXYGEN = flowing(OXYGEN_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CHLORINE = source(CHLORINE_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_CHLORINE = flowing(CHLORINE_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> ARGON = source(ARGON_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_ARGON = flowing(ARGON_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SEA_WATER = source(SEA_WATER_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_SEA_WATER = flowing(SEA_WATER_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> VIRIDIC_ACID = source(VIRIDIC_ACID_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_VIRIDIC_ACID = flowing(VIRIDIC_ACID_TYPE);
    //#endregion

    private static DeferredHolder<Fluid, BaseFlowingFluid.Source> source(DeferredHolder<FluidType, ?> typeHolder)
    {
        return DeferredHolder.create(Registries.FLUID, typeHolder.getId());
    }

    private static DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing(DeferredHolder<FluidType, ?> typeHolder)
    {
        return DeferredHolder.create(Registries.FLUID, typeHolder.getId().withPrefix("flowing_"));
    }

    private static DeferredHolder<FluidType, FluidType> registerType(String name, UnaryOperator<FluidType.Properties> propertiesOp)
    {
        return TYPES.register(name, () -> new FluidType(propertiesOp.apply(FluidType.Properties.create()
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY))));
    }

    private static void registerFluid(RegisterEvent.RegisterHelper<Fluid> helper, Supplier<FluidType> typeSupplier,
                                      DeferredHolder<Fluid, BaseFlowingFluid.Source> sourceHolder, DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowingHolder, UnaryOperator<BaseFlowingFluid.Properties> propertiesOp)
    {
        BaseFlowingFluid.Properties properties = propertiesOp.apply(new BaseFlowingFluid.Properties(typeSupplier, sourceHolder, flowingHolder));
        helper.register(sourceHolder.getId(), new BaseFlowingFluid.Source(properties));
        helper.register(flowingHolder.getId(), new BaseFlowingFluid.Flowing(properties));
    }
}