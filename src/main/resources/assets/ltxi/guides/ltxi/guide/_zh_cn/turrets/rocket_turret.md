---
navigation:
    title: Atmos
    icon: rocket_turret
    parent: turrets/index.md
    position: 1
item_ids:
  - rocket_turret
---

# *Atmos* A/DS Turret

<GameScene zoom={4} interactive={false}>
<Block id="mesh_block" y="0" />
<Block id="rocket_turret" y="-1" />
<BlockAnnotation x="0" y="-1" z="0" color="#8bd1f0">
Items and Energy can only be connected to this block!
</BlockAnnotation>
</GameScene>

## Stats
- Horizontal search radius: 50.0
- Vertical search radius: 75.0
- Target search interval: 5 sec
- Max targets per volley: 4
- Rocket damage: 40.0

## Default targets

Entities tagged `#ltxi:targets/flying`, e.g. Phantom, Ghast, Blaze, Breeze, Ender Dragon, Vex, Wither