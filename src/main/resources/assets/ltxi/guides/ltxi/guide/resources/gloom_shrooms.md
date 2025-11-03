---
navigation:
    title: Gloom Shroom
    position: 1
    icon: gloom_shroom
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
<ImportStructure src="gloom_shroom_farm.nbt" />
<Block id="gloom_shroom" y="1" />
</GameScene>

These life-hungry shrooms will never grow or spread naturally, and bone meal is ineffective. To produce more, first gather
a Sculk Catalyst and some Sculk, then plant your initial shrooms close to the Catalyst. When the Sculk Catalyst activates
and a sculk charge passes through a Gloom Shroom, there is a 33% chance it will spread 1 new shroom to a *3x3x3 area*
around it. The space must be a replaceable block like air or flowing water.

## Usage

You can process Gloom Shrooms in the [ElectroCentrifuge](../machines/gpm/electrocentrifuge.md) with a little bit of
[Viridic Acid](vitriol_berries.md) solvent. This will net you a bottle of Echo Serum and, rarely, a vial of
the shroom's macabre Neuro-Suppressant. 

### Echo Serum

<ItemImage id="sculk_chemical" scale="2" />

Echo Serum is the refined essence of Sculk absorbed by the Gloom Shroom over centuries. Conveniently extracted,
purified, and bottled by your machines. It is used for crafting Sculk-attuned technology as well as the
paradoxical [Real Virtuality Circuit](circuits.md).

### Neuro-Suppressant

<ItemImage id="neuro_chemical" scale="2" />

The Gloom Shroom's defense mechanism evokes the debilitating power of the Deep Dark's Warden. We'd call it a toxin,
but it does not fit any known criteria to be classified as such. It can be used to craft the
[Neuro Gas](../weapons/hanabi.md#neuro) shells for the *Hanabi*.

Killing a Warden with *Hanabi*'s [Acid Shells](../weapons/hanabi.md#acid) **OR** while it is affected by the
[Corrosive](../fundamentals/mob_effects.md#corroding) mob effect guarantees 1 Neuro-Suppressant vial drop.