package liedge.ltxindustries.util.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LTXIMachinesConfig
{
    private LTXIMachinesConfig() {}

    public static final ModConfigSpec.IntValue ECA_BASE_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ECA_BASE_TRANSFER_RATE;

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

    public static final ModConfigSpec.IntValue DIGITAL_GARDEN_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue DIGITAL_GARDEN_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue FABRICATOR_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue FABRICATOR_ENERGY_USAGE;

    public static final ModConfigSpec.IntValue REPAIRER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue REPAIRER_ENERGY_USAGE;
    public static final ModConfigSpec.IntValue REPAIRER_OPERATION_TIME;

    public static final ModConfigSpec.IntValue ATMOS_TURRET_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ATMOS_TURRET_ENERGY_PER_TARGET;
    public static final ModConfigSpec.DoubleValue ATMOS_TURRET_ROCKET_DAMAGE;

    public static final ModConfigSpec.IntValue NOCTIS_TURRET_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue NOCTIS_TURRET_ENERGY_PER_TARGET;
    public static final ModConfigSpec.DoubleValue NOCTIS_TURRET_DAMAGE;

    public static final ModConfigSpec MACHINES_CONFIG_SPEC;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // Energy Cell Array (ECA)
        builder.push("energy_cell_array");
        ECA_BASE_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Energy Cell Array")
                        .defineInRange("energy_capacity", 1_000_000, 1, Integer.MAX_VALUE);
        ECA_BASE_TRANSFER_RATE = builder.comment("Base transfer rate (per tick) of the Energy Cell Array")
                        .defineInRange("transfer_rate", 10_000, 1, Integer.MAX_VALUE);
        builder.pop();

        // Digital Furnace
        builder.push("digital_furnace");
        DIGITAL_FURNACE_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Digital Furnace")
                .defineInRange("energy_capacity", 100_000, 1, Integer.MAX_VALUE);
        DIGITAL_FURNACE_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Digital Furnace")
                .defineInRange("energy_usage", 25, 1, Integer.MAX_VALUE);
        builder.pop();

        // Digital Smoker
        builder.push("digital_smoker");
        DIGITAL_SMOKER_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Digital Smoker")
                .defineInRange("energy_capacity", 100_000, 1, Integer.MAX_VALUE);
        DIGITAL_SMOKER_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Digital Smoker")
                .defineInRange("energy_usage", 10, 1, Integer.MAX_VALUE);
        builder.pop();

        // Digital Blast Furnace
        builder.push("digital_blast_furnace");
        DIGITAL_BLAST_FURNACE_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Digital Blast Furnace")
                .defineInRange("energy_capacity", 100_000, 1, Integer.MAX_VALUE);
        DIGITAL_BLAST_FURNACE_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Digital Blast Furnace")
                .defineInRange("energy_usage", 15, 1, Integer.MAX_VALUE);
        builder.pop();

        // Grinder
        builder.push("grinder");
        GRINDER_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Grinder")
                .defineInRange("energy_capacity", 100_000, 1, Integer.MAX_VALUE);
        GRINDER_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Grinder")
                .defineInRange("energy_usage", 30, 1, Integer.MAX_VALUE);
        builder.pop();

        // Material Fusing Chamber
        builder.push("material_fusing_chamber");
        MFC_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Material Fusing Chamber")
                .defineInRange("energy_capacity", 100_000, 1, Integer.MAX_VALUE);
        MFC_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Material Fusing Chamber")
                .defineInRange("energy_usage", 30, 1, Integer.MAX_VALUE);
        builder.pop();

        // ElectroCentrifuge
        builder.push("electrocentrifuge");
        ELECTROCENTRIFUGE_ENERGY_CAPACITY = builder.comment("Base energy capacity of the ElectroCentrifuge")
                .defineInRange("energy_capacity", 100_000, 1, Integer.MAX_VALUE);
        ELECTROCENTRIFUGE_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the ElectroCentrifuge")
                .defineInRange("energy_usage", 40, 1, Integer.MAX_VALUE);
        builder.pop();

        // Mixer
        builder.push("mixer");
        MIXER_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Mixer")
                .defineInRange("energy_capacity", 100_000, 1, Integer.MAX_VALUE);
        MIXER_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Mixer")
                .defineInRange("energy_usage", 30, 1, Integer.MAX_VALUE);
        builder.pop();

        // Voltaic Injector
        builder.push("voltaic_injector");
        VOLTAIC_INJECTOR_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Voltaic Injector")
                .defineInRange("energy_capacity", 100_000, 1, Integer.MAX_VALUE);
        VOLTAIC_INJECTOR_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Voltaic Injector")
                .defineInRange("energy_usage", 25, 1, Integer.MAX_VALUE);
        builder.pop();

        // Chem Lab
        builder.push("chem_lab");
        CHEM_LAB_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Chem Lab")
                .defineInRange("energy_capacity", 250_000, 1, Integer.MAX_VALUE);
        CHEM_LAB_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Chem Lab")
                .defineInRange("energy_usage", 80, 1, Integer.MAX_VALUE);
        builder.pop();

        // Digital Garden
        builder.push("digital_garden");
        DIGITAL_GARDEN_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Bio/ARU Garden")
                .defineInRange("energy_capacity", 250_000, 1, Integer.MAX_VALUE);
        DIGITAL_GARDEN_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Bio/ARU Garden")
                .defineInRange("energy_usage", 100, 1, Integer.MAX_VALUE);
        builder.pop();

        // Fabricator
        builder.push("fabricator");
        FABRICATOR_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Fabricator.")
                .defineInRange("energy_capacity", 250_000, 1, Integer.MAX_VALUE);
        FABRICATOR_ENERGY_USAGE = builder.comment("The energy usage of the Fabricator. Affects how quickly the fabricator completes recipes.")
                .defineInRange("energy_usage", 2500, 1, Integer.MAX_VALUE);
        builder.pop();

        // Molecular Reconstructor
        builder.push("molecular_reconstructor");
        REPAIRER_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Molecular Reconstructor.")
                .defineInRange("energy_capacity", 500_000, 1, Integer.MAX_VALUE);
        REPAIRER_ENERGY_USAGE = builder.comment("Base energy usage per tick of the Molecular Reconstructor.")
                .defineInRange("energy_usage", 80, 1, Integer.MAX_VALUE);
        REPAIRER_OPERATION_TIME = builder.comment("Base operation time (in ticks) of the Molecular Reconstructor.")
                .defineInRange("operation_time", 20, 1, Integer.MAX_VALUE);
        builder.pop();

        // Rocket turret
        builder.push("atmos_turret");
        ATMOS_TURRET_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Atmos turret.")
                .defineInRange("energy_capacity", 200_000, 1, Integer.MAX_VALUE);
        ATMOS_TURRET_ENERGY_PER_TARGET = builder.comment("Energy usage per target acquisition.")
                .defineInRange("energy_per_target", 10_000, 1, Integer.MAX_VALUE);
        ATMOS_TURRET_ROCKET_DAMAGE = builder.comment("Base damage dealt by rockets from the Atmos turret.")
                .defineInRange("rocket_damage", 40d, 1d, Double.MAX_VALUE);
        builder.pop();

        // Noctis turret
        builder.push("noctis_turret");
        NOCTIS_TURRET_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Noctis turret.")
                .defineInRange("energy_capacity", 1_200_000, 1, Integer.MAX_VALUE);
        NOCTIS_TURRET_ENERGY_PER_TARGET = builder.comment("Energy usage per target acquisition.")
                .defineInRange("energy_per_target", 400_000, 1, Integer.MAX_VALUE);
        NOCTIS_TURRET_DAMAGE = builder.comment("Base damage per shot of the Noctis turret.")
                .defineInRange("base_damage", 200d, 1d, Double.MAX_VALUE);
        builder.pop();

        MACHINES_CONFIG_SPEC = builder.build();
    }
}