---
navigation:
    title: Atmos
    icon: rocket_turret
    parent: turrets/index.md
    position: 0
---

# LTX A/DU *Atmos*

<GameScene zoom={4} interactive={false}>
<Block id="mesh_block" y="0" />
<Block id="rocket_turret" y="-1" />
<BlockAnnotation x="0" y="-1" z="0" color="#8bd1f0">
Items and Energy can only be connected to this block!
</BlockAnnotation>
</GameScene>

## Stats
- Targeting area: 100x90x100. **XZ is centered, Y extends 20 below, and 70 above**
- Target Scan Interval: 5 sec
- Max targets per volley: 4
- Volley charge time: 2 sec
- Rocket damage: 40.0

## Targets
- Entity type tag: `#ltxi:targets/flying`
  - Phantom, Ghast, Blaze, Breeze, Ender Dragon, Vex, Wither