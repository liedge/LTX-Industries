---
navigation:
    title: "Portable Tank"
    position: 2
    parent: machines/storage/index.md
    icon: portable_tank
item_ids:
  - portable_tank
  - infinite_water_tank
  - infinite_lava_tank
---

# Portable Tank

<BlockImage id="portable_tank" scale="4" />

**Portable Tanks** will be your general purpose fluid storage option. They have configurable IO and can be
[upgraded](../../fundamentals/upgrade_system.md). Tanks will retain their contents when broken and have **fluid handling capability**
as both items and block entities. As items, they have no transfer limit. As block entities, their transfer limit is 1/10th
of their total capacity.

> Be careful when removing capacity-providing upgrades! If the stored fluid exceeds the new capacity, any excess fluid will be destroyed.

## Special Infinite Variants

<Row>
<BlockImage id="infinite_water_tank" scale="2" />
<BlockImage id="infinite_lava_tank" scale="2" />
</Row>

These portable tanks have a unique name and appearance. They provide a functionally infinite (`Integer.MAX_VALUE` if we need
to be exact) source and transfer limit of fluid. Their auto-output is also faster than the normal variant at 5 ticks vs 20.