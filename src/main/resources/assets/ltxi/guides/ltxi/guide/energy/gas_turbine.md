---
navigation:
    title: Gas Turbine
    icon: gas_turbine
    parent: energy/index.md
    position: 12
item_ids:
  - gas_turbine
---

# Gas Turbine

<GameScene zoom={4} interactive={false}>
<Block id="gas_turbine" />
<Block id="mesh_block" x="1" z="1" />
<Block id="mesh_block" x="1" y="1" z="1" />
<Block id="mesh_block" x="-1" y="1" z="2" />

<BoxAnnotation min="1 0 1" max="2 2 2" color="#8bd1f0">
Fluids and energy can be connected to these 2 ports. Energy is automatically ejected every tick, fluids must be pushed
in manually.
</BoxAnnotation>
</GameScene>

Portable Generators and Solar Panels can get your outpost or starter base off the ground, but you'll eventually need to migrate
to a more permanent and stronger power source. [Methane](../resources/elements_compound.md#methane) contains a lot of energy,
but you'll need some serious tech to properly convert it to power.

The Gas Turbine is quite a bit more expensive than the smaller generators, needs to be [Fabricated](../machines/processing/fabricators.md),
and needs more space. But its energy generation and efficiency dwarf those of the more power solutions.