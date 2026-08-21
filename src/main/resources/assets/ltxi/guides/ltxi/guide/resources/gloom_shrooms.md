---
navigation:
    title: Gloom Shroom
    icon: gloom_shroom
    position: 12
    parent: resources/index.md
---

# Gloom Shroom

## About

<ItemImage id="gloom_shroom" scale="2" />

The eerie Deep Dark seems like it's incompatible with all life you're familiar with. Yet, if you venture into these
gloomy caves you'll find the strangely beautiful <Color id="blue">Gloom Shrooms</Color> growing and feeding off the soul
energy in the Sculk.

## Natural Generation

Gloom Shrooms generate sparsely in the Deep Dark biome on top of Sculk. Only about 1 to 2 will spawn per chunk. You'll
know when you see them. They are bioluminescent, yet don't emit any light to their surroundings.

## Farming

You can instantly break Gloom Shrooms by hand. No special tools or enchantments are needed.

### Suitable environment

Gloom shrooms can only be planted on top of Sculk. In addition, they cannot be exposed to skylight, or they will break.

### Propagation

<GameScene zoom={2} interactive={true}>
<ImportStructure src="../assets/gloom_shroom_farm.nbt" />
<Block id="gloom_shroom" y="1" />
</GameScene>

These life-hungry shrooms will never grow or spread naturally, and bone meal is ineffective. To produce more, first gather
a Sculk Catalyst and some Sculk, then plant your initial shrooms close to the Catalyst. When the Sculk Catalyst activates
and a sculk charge passes through a Gloom Shroom, there is a 33% chance it will spread 1 new shroom to a *3x3x3 area*
around it. The space must be a replaceable block like air or flowing water.

## Products

### Echo Serum

<ItemImage id="sculk_chemical" scale="2" />

Dissolving Gloom Shrooms in [Sulfuric Acid](acids.md#sulfuric-acid) using the [Mixer](../machines/processing/mixer.md)
to produce Echo Serum.

### Concentrated Gloom Essence
 
<ItemImage id="gloom_weapon_chemical" scale="2" />

Taking a large amount of Echo Serum and reducing with [Ammonia](elements_compound.md#ammonia) will produce Concentrated
Gloom Essence. This substance is not very useful industrially, but is useful for creating debilitation weaponry.

For those who prefer a more combat-oriented approach, killing the Warden with a final blow from *Hanabi*'s
[Acid Shells](../weapons/hanabi.md#acid) **OR** while it is affected by the [Corroding](../fundamentals/mob_effects.md#corroding)
status effect guarantees 1 Concentrated Gloom Essence drop.