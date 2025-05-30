package liedge.limatech.util.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class LimaTechMachinesConfig
{
    private LimaTechMachinesConfig() {}

    public static final ModConfigSpec.IntValue ESA_BASE_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue ESA_BASE_TRANSFER_RATE;

    public static final ModConfigSpec.IntValue DIGITAL_FURNACE_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue DIGITAL_FURNACE_ENERGY_USAGE;
    public static final ModConfigSpec.IntValue DIGITAL_FURNACE_CRAFTING_TIME;

    public static final ModConfigSpec.IntValue DIGITAL_SMOKER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue DIGITAL_SMOKER_ENERGY_USAGE;
    public static final ModConfigSpec.IntValue DIGITAL_SMOKER_CRAFTING_TIME;

    public static final ModConfigSpec.IntValue DIGITAL_BLAST_FURNACE_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue DIGITAL_BLAST_FURNACE_ENERGY_USAGE;
    public static final ModConfigSpec.IntValue DIGITAL_BLAST_FURNACE_CRAFTING_TIME;

    public static final ModConfigSpec.IntValue GRINDER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue GRINDER_ENERGY_USAGE;
    public static final ModConfigSpec.IntValue GRINDER_CRAFTING_TIME;

    public static final ModConfigSpec.IntValue RECOMPOSER_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue RECOMPOSER_ENERGY_USAGE;
    public static final ModConfigSpec.IntValue RECOMPOSER_CRAFTING_TIME;

    public static final ModConfigSpec.IntValue MFC_ENERGY_CAPACITY;
    public static final ModConfigSpec.IntValue MFC_ENERGY_USAGE;
    public static final ModConfigSpec.IntValue MFC_CRAFTING_TIME;

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

        // Energy Storage Array (ESA)
        builder.push("energy_storage_array");
        ESA_BASE_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Energy Storage Array")
                        .defineInRange("energy_capacity", 1_000_000, 1, Integer.MAX_VALUE);
        ESA_BASE_TRANSFER_RATE = builder.comment("Base transfer rate (per tick) of the Energy Storage Array")
                        .defineInRange("transfer_rate", 10_000, 1, Integer.MAX_VALUE);
        builder.pop();

        // Digital Furnace
        builder.push("digital_furnace");
        DIGITAL_FURNACE_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Digital Furnace")
                .defineInRange("energy_capacity", 250_000, 1, Integer.MAX_VALUE);
        DIGITAL_FURNACE_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Digital Furnace")
                .defineInRange("energy_usage", 60, 1, Integer.MAX_VALUE);
        DIGITAL_FURNACE_CRAFTING_TIME = builder.comment("Base crafting time (in ticks) of the Digital Furnace")
                .defineInRange("crafting_time", 160, 1, Integer.MAX_VALUE);
        builder.pop();

        // Digital Smoker
        builder.push("digital_smoker");
        DIGITAL_SMOKER_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Digital Smoker")
                .defineInRange("energy_capacity", 250_000, 1, Integer.MAX_VALUE);
        DIGITAL_SMOKER_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Digital Smoker")
                .defineInRange("energy_usage", 20, 1, Integer.MAX_VALUE);
        DIGITAL_SMOKER_CRAFTING_TIME = builder.comment("Base crafting time (in ticks) of the Digital Smoker")
                .defineInRange("crafting_time", 100, 1, Integer.MAX_VALUE);
        builder.pop();

        // Digital Blast Furnace
        builder.push("digital_blast_furnace");
        DIGITAL_BLAST_FURNACE_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Digital Blast Furnace")
                .defineInRange("energy_capacity", 250_000, 1, Integer.MAX_VALUE);
        DIGITAL_BLAST_FURNACE_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Digital Blast Furnace")
                .defineInRange("energy_usage", 30, 1, Integer.MAX_VALUE);
        DIGITAL_BLAST_FURNACE_CRAFTING_TIME = builder.comment("Base crafting time (in ticks) of the Digital Blast Furnace")
                .defineInRange("crafting_time", 100, 1, Integer.MAX_VALUE);
        builder.pop();

        // Grinder
        builder.push("grinder");
        GRINDER_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Grinder")
                .defineInRange("energy_capacity", 250_000, 1, Integer.MAX_VALUE);
        GRINDER_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Grinder")
                .defineInRange("energy_usage", 80, 1, Integer.MAX_VALUE);
        GRINDER_CRAFTING_TIME = builder.comment("Base crafting time (in ticks) of the Grinder")
                .defineInRange("crafting_time", 160, 1, Integer.MAX_VALUE);
        builder.pop();

        // Recomposer
        builder.push("recomposer");
        RECOMPOSER_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Recomposer")
                .defineInRange("energy_capacity", 250_000, 1, Integer.MAX_VALUE);
        RECOMPOSER_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Recomposer")
                .defineInRange("energy_usage", 120, 1, Integer.MAX_VALUE);
        RECOMPOSER_CRAFTING_TIME = builder.comment("Base crafting time (in ticks) of the Recomposer")
                .defineInRange("crafting_time", 160, 1, Integer.MAX_VALUE);
        builder.pop();

        // Material Fusing Chamber
        builder.push("material_fusing_chamber");
        MFC_ENERGY_CAPACITY = builder.comment("Base energy capacity of the Material Fusing Chamber")
                .defineInRange("energy_capacity", 250_000, 1, Integer.MAX_VALUE);
        MFC_ENERGY_USAGE = builder.comment("Base energy usage per operation tick of the Material Fusing Chamber")
                .defineInRange("energy_usage", 120, 1, Integer.MAX_VALUE);
        MFC_CRAFTING_TIME = builder.comment("Base crafting time (in ticks) of the Material Fusing Chamber")
                .defineInRange("crafting_time", 160, 1, Integer.MAX_VALUE);
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
                .defineInRange("energy_usage", 160, 1, Integer.MAX_VALUE);
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