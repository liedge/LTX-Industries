---
navigation:
    title: Ore Processing
    icon: crushed_silver_ore
    position: 21
---

# Ore Processing

As your facilities grow, likely so does your intolerance to long and arduous mining trips. Probably. In any case, our
machines are capable of processing ores through a complex, 5-stage chain that can multiply your ores by an average factor
of **10.125**! 

In case that doesn't sound appealing, you must still process at least Titanium, Olivine, and Copper ores to extract
Tungsten, Pyroxene, and Rhenium respectively. 

<ItemGrid>
<ItemIcon id="raw_silver" />
<ItemIcon id="silver_ore" />
</ItemGrid>

You will start with raw materials or ore blocks which will be processed through their Crushed, Washed, Chunk, Solution,
and Crystal forms.

<ItemGrid>
<ItemIcon id="crushed_silver_ore" />
<ItemIcon id="washed_silver_ore" />
<ItemIcon id="silver_ore_chunk" />
<ItemIcon id="silver_ore_solution" />
<ItemIcon id="silver_ore_crystal" />
</ItemGrid>

At any point you can stop processing and smelt the ore material into their respective final product forms (i.e. Crushed
Silver Ore or Silver Ore Solution into Silver Ingot). Stage 1 guarantees doubling of the ore, each subsequent stage
has a 50% of doubling each processed item. This means that on *average*, stages 2-5 have an average multiplier of 3x,
4.5x, 6.75x, and 10.125x.

## Stage 1

The first stage of ore processing is handled by the [Grinder](machines/processing/grinder.md) which will crush raw
materials/ore blocks into Crushed Ore. This is the only stage which requires no upgrades or the Ore Processing
[recipe mode](fundamentals/recipe_modes.md). It is available rather early, so prioritize getting a Grinder as soon
as possible.

## Stage 2

<UpgradeEntry id="ore_process_2" />

The [HydroSieve](machines/processing/hydrosieve.md) will process Crushed Ores into Washed Ores. At this stage you will start
needing the Ore Processing recipe mode, which is not available by default on any of the machines. You will need to
craft and install the above upgrade module onto your HydroSieve.

## Stage 3

<UpgradeEntry id="ore_process_3" />

The [Voltaic Injector](machines/processing/voltaic_injector.md) refines Washed Ores into Ore Chunks. The cost of upgrade
modules takes a sharp turn upwards around this point. Investment in power tier upgrade modules is heavily recommended
at this point to avoid bottlenecks.

### Pyroxene and Tungsten

<ItemGrid>
<ItemIcon id="pyroxene" />
<ItemIcon id="tungsten_trioxide" />
</ItemGrid>

Pyroxene and Tungsten Trioxide will appear as byproducts from Olivine and Titanium processing.

## Stage 4

<UpgradeEntry id="ore_process_4" />

The [Chem Lab](machines/processing/chem_lab.md) will dissolve Ore Chunks into vials of Ore Solution using a small
amount of [Sulfuric Acid](resources/acids.md#sulfuric-acid). A dedicated production line of Sulfuric Acid solely
for ore processing is recommended.

## Stage 5

<UpgradeEntry id="ore_process_5" />

The [ElectroCentrifuge](machines/processing/electrocentrifuge.md) will precipitate Ore Solution into Ore Crystals with
[Hydrochloric Acid](resources/acids.md#hydrochloric-acid). Once again, a dedicated supply line of acid is recommended
since the waste products from Silicone Rubber and Fluoropolymer production is not enough.

### Rhenium(VII) Oxide

<ItemGrid>
<ItemIcon id="rhenium_7_oxide" />
</ItemGrid>

Rhenium(VII) Oxide will appear at the very end of processing Copper ores. If acid supply is somehow a bottleneck
this late in the progression tree, prioritize copper ores as Rhenium is used for the best upgrades and equipment.