---
navigation:
    title: Noctis
    icon: railgun_turret
    parent: turrets/index.md
    position: 2
item_ids:
  - railgun_turret
---

# *Noctis* A/DS Turret

<GameScene zoom={4} interactive={false}>
<Block id="mesh_block" y="0" />
<Block id="railgun_turret" y="-1" />
<BlockAnnotation x="0" y="-1" z="0" color="#8bd1f0">
Items and Energy can only be connected to this block!
</BlockAnnotation>
</GameScene>

## Stats
- Horizontal search radius: 25.0
- Vertical search radius: 25.0
- Target search interval: 4 sec
- Single target only
- Railgun bolt damage: 200.0

## Default targets

Entities tagged `#ltxi:targets/high_threat`, e.g. Elder Guardian, Ender Dragon, Wither, Warden