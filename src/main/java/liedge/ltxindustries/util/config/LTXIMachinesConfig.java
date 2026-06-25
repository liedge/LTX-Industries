package liedge.ltxindustries.util.config;

import liedge.limacore.LimaCommonConstants;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class LTXIMachinesConfig
{
    private LTXIMachinesConfig() {}

    public static final ModConfigSpec.IntValue ECA_BASE_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ECA_BASE_TRANSFER_RATE;

    public static final ModConfigSpec.IntValue PORTABLE_TANK_CAPACITY;

    public static final ModConfigSpec.IntValue DIGITAL_FURNACE_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue DIGITAL_FURNACE_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue DIGITAL_SMOKER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue DIGITAL_SMOKER_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue DIGITAL_BLAST_FURNACE_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue DIGITAL_BLAST_FURNACE_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue GRINDER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue GRINDER_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue MFC_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue MFC_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue ELECTROCENTRIFUGE_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ELECTROCENTRIFUGE_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue MIXER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue MIXER_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue VOLTAIC_INJECTOR_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue VOLTAIC_INJECTOR_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue CHEM_LAB_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue CHEM_LAB_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue ASSEMBLER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ASSEMBLER_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue GEO_SYNTHESIZER_CAPACITY;
    public static final ModConfigSpec.IntValue GEO_SYNTHESIZER_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue DIGITAL_GARDEN_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue DIGITAL_GARDEN_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue FABRICATOR_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue FABRICATOR_ENERGY_USAGE;
    public static final ModConfigSpec.BooleanValue FABRICATOR_AE2_AUTO_RECONFIGURE_IO;

    public static final ModConfigSpec.IntValue PORTABLE_GENERATOR_CAPACITY;
    public static final ModConfigSpec.IntValue PORTABLE_GENERATOR_GENERATION;
    public static final ModConfigSpec.IntValue PORTABLE_GENERATOR_ENERGY_PER_FUEL;

    public static final ModConfigSpec.IntValue SOLAR_PANEL_CAPACITY;
    public static final ModConfigSpec.IntValue SOLAR_PANEL_GENERATION;

    public static final ModConfigSpec.IntValue REPAIR_STATION_CAPACITY;
    public static final ModConfigSpec.IntValue REPAIR_STATION_ENERGY_USAGE;
    public static final ModConfigSpec.IntValue REPAIR_STATION_BASE_SPEED;

    public static final ModConfigSpec.IntValue ARC_TURRET_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ARC_TURRET_ENERGY_USAGE;
    public static final ModConfigSpec.DoubleValue ARC_TURRET_DAMAGE;

    public static final ModConfigSpec.IntValue ROCKET_TURRET_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ROCKET_TURRET_ENERGY_PER_TARGET;
    public static final ModConfigSpec.DoubleValue ROCKET_TURRET_ROCKET_DAMAGE;

    public static final ModConfigSpec.IntValue RAILGUN_TURRET_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue RAILGUN_TURRET_ENERGY_PER_TARGET;
    public static final ModConfigSpec.DoubleValue RAILGUN_TURRET_DAMAGE;

    public static final ModConfigSpec MACHINES_CONFIG_SPEC;

    public static int getPortableTankCapacity()
    {
        return ConfigUtil.getIntValueOrZero(MACHINES_CONFIG_SPEC, PORTABLE_TANK_CAPACITY);
    }

    public static int getECAEnergyCapacity()
    {
        return ConfigUtil.getIntValueOrZero(MACHINES_CONFIG_SPEC, ECA_BASE_ENERGY_CAPACITY);
    }

    public static int getECATransferRate()
    {
        return ConfigUtil.getIntValueOrZero(MACHINES_CONFIG_SPEC, ECA_BASE_TRANSFER_RATE);
    }

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Energy Cell Array").push("energy_cell_array");
        ECA_BASE_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 1_000_000);
        ECA_BASE_TRANSFER_RATE = ConfigUtil.energyTransfer(builder, 10_000);
        builder.pop();

        builder.comment("Portable Tank").push("portable_tank");
        PORTABLE_TANK_CAPACITY = ConfigUtil.positiveInt(builder, LimaCommonConstants.KEY_FLUID_CAPACITY, "Base fluid capacity of the Portable Tank in mB", 16_000);
        builder.pop();

        builder.comment("Digital Furnace").push("digital_furnace");
        DIGITAL_FURNACE_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        DIGITAL_FURNACE_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 25);
        builder.pop();

        builder.comment("Digital Smoker").push("digital_smoker");
        DIGITAL_SMOKER_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        DIGITAL_SMOKER_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 10);
        builder.pop();

        builder.comment("Digital Blast Furnace").push("digital_blast_furnace");
        DIGITAL_BLAST_FURNACE_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        DIGITAL_BLAST_FURNACE_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 15);
        builder.pop();

        builder.comment("Grinder").push("grinder");
        GRINDER_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        GRINDER_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 30);
        builder.pop();

        builder.comment("Material Fusing Chamber").push("material_fusing_chamber");
        MFC_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        MFC_ENERGY_USAGE =  ConfigUtil.energyUsagePerTick(builder, 30);
        builder.pop();

        builder.comment("ElectroCentrifuge").push("electrocentrifuge");
        ELECTROCENTRIFUGE_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        ELECTROCENTRIFUGE_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 40);
        builder.pop();

        builder.comment("Mixer").push("mixer");
        MIXER_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        MIXER_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 30);
        builder.pop();

        builder.comment("Voltaic Injector").push("voltaic_injector");
        VOLTAIC_INJECTOR_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        VOLTAIC_INJECTOR_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 25);
        builder.pop();

        builder.comment("Chem Lab").push("chem_lab");
        CHEM_LAB_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 250_000);
        CHEM_LAB_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 80);
        builder.pop();

        builder.comment("Assembler").push("assembler");
        ASSEMBLER_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 250_000);
        ASSEMBLER_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 80);
        builder.pop();

        builder.comment("Geo Synthesizer").push("geo_synthesizer");
        GEO_SYNTHESIZER_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        GEO_SYNTHESIZER_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 10);
        builder.pop();

        builder.comment("Bio/ARU Garden").push("digital_garden");
        DIGITAL_GARDEN_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 250_000);
        DIGITAL_GARDEN_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 80);
        builder.pop();

        builder.comment("Fabricator and Auto Fabricator").push("fabricators");
        FABRICATOR_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 250_000);
        FABRICATOR_ENERGY_USAGE = ConfigUtil.customEnergyUsage(builder, "Base energy usage per tick of the Fabricator. Affects how quickly Fabrication recipes are completed.", 2500);
        FABRICATOR_AE2_AUTO_RECONFIGURE_IO = builder.comment("If set to true, AE2 Pattern Providers will automatically re-configure the Auto Fabricator's IO item configuration when inserting a pattern.")
                .define("ae2_pattern_auto_reconfigure", true);
        builder.pop();

        builder.comment("Portable Generator").push("portable_generator");
        PORTABLE_GENERATOR_CAPACITY = ConfigUtil.energyCapacity(builder, 100_000);
        PORTABLE_GENERATOR_GENERATION = ConfigUtil.positiveInt(builder, "energy_generation", "Energy generated per tick", 40);
        PORTABLE_GENERATOR_ENERGY_PER_FUEL = ConfigUtil.positiveInt(builder, "energy_per_fuel", "How much energy a fuel unit produces", 2500);
        builder.pop();

        builder.comment("Solar Panel").push("solar_panel");
        SOLAR_PANEL_CAPACITY = ConfigUtil.energyCapacity(builder, 80_000);
        SOLAR_PANEL_GENERATION = ConfigUtil.positiveInt(builder, "energy_generation", "Energy generated per tick", 25);
        builder.pop();

        builder.comment("Repair Station").push("repair_station");
        REPAIR_STATION_CAPACITY = ConfigUtil.energyCapacity(builder, 250_000);
        REPAIR_STATION_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 80);
        REPAIR_STATION_BASE_SPEED = ConfigUtil.positiveInt(builder, "ticks_per_operation", "Base ticks needed for one Repair Station operation", 40);
        builder.pop();

        builder.comment("Ionos Turret").push("arc_turret");
        ARC_TURRET_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 200_000);
        ARC_TURRET_ENERGY_USAGE = ConfigUtil.energyUsagePerTick(builder, 200);
        ARC_TURRET_DAMAGE = ConfigUtil.baseDamage(builder, "Base damage per tick", 2d);
        builder.pop();

        builder.comment("Atmos Turret").push("rocket_turret");
        ROCKET_TURRET_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 200_000);
        ROCKET_TURRET_ENERGY_PER_TARGET = ConfigUtil.customEnergyUsage(builder, "Base energy usage per rocket fired", 10_000);
        ROCKET_TURRET_ROCKET_DAMAGE = ConfigUtil.baseDamage(builder, "Base damage per rocket", 40d);
        builder.pop();

        builder.comment("Noctis Turret").push("railgun_turret");
        RAILGUN_TURRET_ENERGY_CAPACITY = ConfigUtil.energyCapacity(builder, 2_000_000);
        RAILGUN_TURRET_ENERGY_PER_TARGET = ConfigUtil.customEnergyUsage(builder, "Base energy usage per shot fired", 500_000);
        RAILGUN_TURRET_DAMAGE = ConfigUtil.baseDamage(builder, "Base damage per shot", 200d);
        builder.pop();

        MACHINES_CONFIG_SPEC = builder.build();
    }
}