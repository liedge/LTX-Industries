package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
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
        registerSimpleFluid(helper, HYDROGEN_TYPE, HYDROGEN, FLOWING_HYDROGEN, LTXIItems.HYDROGEN_BUCKET);
        registerSimpleFluid(helper, NITROGEN_TYPE, NITROGEN, FLOWING_NITROGEN, LTXIItems.NITROGEN_BUCKET);
        registerSimpleFluid(helper, OXYGEN_TYPE, OXYGEN, FLOWING_OXYGEN, LTXIItems.OXYGEN_BUCKET);
        registerSimpleFluid(helper, CHLORINE_TYPE, CHLORINE, FLOWING_CHLORINE, LTXIItems.CHLORINE_BUCKET);
        registerSimpleFluid(helper, ARGON_TYPE, ARGON, FLOWING_ARGON, LTXIItems.ARGON_BUCKET);
        registerSimpleFluid(helper, METHANE_TYPE, METHANE, FLOWING_METHANE, LTXIItems.METHANE_BUCKET);
        registerSimpleFluid(helper, SULPHURINE_TYPE, SULPHURINE, FLOWING_SULPHURINE, LTXIItems.SULPHURINE_BUCKET);
        registerSimpleFluid(helper, SEA_WATER_TYPE, SEA_WATER, FLOWING_SEA_WATER, LTXIItems.SEA_WATER_BUCKET);
        registerSimpleFluid(helper, AMMONIA_TYPE, AMMONIA, FLOWING_AMMONIA, LTXIItems.AMMONIA_BUCKET);
        registerSimpleFluid(helper, HYDROCHLORIC_ACID_TYPE, HYDROCHLORIC_ACID, FLOWING_HYDROCHLORIC_ACID, LTXIItems.HYDROCHLORIC_ACID_BUCKET);
        registerFluid(helper, SULFURIC_ACID_TYPE, SULFURIC_ACID, FLOWING_SULFURIC_ACID, properties -> properties.block(LTXIBlocks.SULFURIC_ACID_BLOCK).bucket(LTXIItems.SULFURIC_ACID_BUCKET));
        registerSimpleFluid(helper, LIQUID_SILICONE_TYPE, LIQUID_SILICONE, FLOWING_LIQUID_SILICONE, LTXIItems.LIQUID_SILICONE_BUCKET);
    }

    // Light levels
    public static final int SULFURIC_ACID_LIGHT = 7;

    //#region Fluid Types
    public static final DeferredHolder<FluidType, FluidType> HYDROGEN_TYPE = simpleType(LTXIIdentifiers.ID_HYDROGEN);
    public static final DeferredHolder<FluidType, FluidType> NITROGEN_TYPE = simpleType(LTXIIdentifiers.ID_NITROGEN);
    public static final DeferredHolder<FluidType, FluidType> OXYGEN_TYPE = simpleType(LTXIIdentifiers.ID_OXYGEN);
    public static final DeferredHolder<FluidType, FluidType> CHLORINE_TYPE = simpleType(LTXIIdentifiers.ID_CHLORINE);
    public static final DeferredHolder<FluidType, FluidType> ARGON_TYPE = simpleType(LTXIIdentifiers.ID_ARGON);
    public static final DeferredHolder<FluidType, FluidType> METHANE_TYPE = simpleType(LTXIIdentifiers.ID_METHANE);
    public static final DeferredHolder<FluidType, FluidType> SULPHURINE_TYPE = simpleType(LTXIIdentifiers.ID_SULPHURINE);
    public static final DeferredHolder<FluidType, FluidType> SEA_WATER_TYPE = simpleType(LTXIIdentifiers.ID_SEA_WATER);
    public static final DeferredHolder<FluidType, FluidType> AMMONIA_TYPE = simpleType(LTXIIdentifiers.ID_AMMONIA);
    public static final DeferredHolder<FluidType, FluidType> HYDROCHLORIC_ACID_TYPE = simpleType(LTXIIdentifiers.ID_HYDROCHLORIC_ACID);
    public static final DeferredHolder<FluidType, FluidType> SULFURIC_ACID_TYPE = registerType(LTXIIdentifiers.ID_SULFURIC_ACID, properties -> properties.lightLevel(SULFURIC_ACID_LIGHT));
    public static final DeferredHolder<FluidType, FluidType> LIQUID_SILICONE_TYPE = simpleType(LTXIIdentifiers.ID_LIQUID_SILICONE);
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
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> METHANE = source(METHANE_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_METHANE = flowing(METHANE_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SULPHURINE = source(SULPHURINE_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_SULPHURINE = flowing(SULPHURINE_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SEA_WATER = source(SEA_WATER_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_SEA_WATER = flowing(SEA_WATER_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> AMMONIA = source(AMMONIA_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_AMMONIA = flowing(AMMONIA_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> HYDROCHLORIC_ACID = source(HYDROCHLORIC_ACID_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_HYDROCHLORIC_ACID = flowing(HYDROCHLORIC_ACID_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SULFURIC_ACID = source(SULFURIC_ACID_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_SULFURIC_ACID = flowing(SULFURIC_ACID_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LIQUID_SILICONE = source(LIQUID_SILICONE_TYPE);
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_LIQUID_SILICONE = flowing(LIQUID_SILICONE_TYPE);
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

    private static DeferredHolder<FluidType, FluidType> simpleType(String name)
    {
        return registerType(name, UnaryOperator.identity());
    }

    private static void registerFluid(RegisterEvent.RegisterHelper<Fluid> helper, Supplier<FluidType> typeSupplier, DeferredHolder<Fluid, ?> source, DeferredHolder<Fluid, ?> flowing, UnaryOperator<BaseFlowingFluid.Properties> op)
    {
        BaseFlowingFluid.Properties properties = op.apply(new BaseFlowingFluid.Properties(typeSupplier, source, flowing));
        helper.register(source.getId(), new BaseFlowingFluid.Source(properties));
        helper.register(flowing.getId(), new BaseFlowingFluid.Flowing(properties));
    }

    private static void registerSimpleFluid(RegisterEvent.RegisterHelper<Fluid> helper, Supplier<FluidType> typeSupplier, DeferredHolder<Fluid, ?> source, DeferredHolder<Fluid, ?> flowing, Supplier<? extends Item> bucket)
    {
        registerFluid(helper, typeSupplier, source, flowing, properties -> properties.bucket(bucket));
    }
}