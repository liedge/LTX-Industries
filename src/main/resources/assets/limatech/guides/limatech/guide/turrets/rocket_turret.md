---
navigation:
    title: Atmos
    icon: rocket_turret
    parent: turrets/index.md
---

# LTX A/DU *Atmos*

<GameScene zoom={4} interactive={true}>
<Block id="rocket_turret" p:half="upper" />
<Block id="rocket_turret" p:half="lower" y="-1" />
</GameScene>

## Stats
- Targeting area: 100x90x100. **XZ is centered, Y extends 20 below, and 70 above**
- Target Scan Interval: 5 sec
- Max targets per volley: 4
- Volley charge time: 2 sec
- Rocket damage: 40.0

## Targets
- Entity type tag: `#limatech:targets/flying`
  - Phantom, Ghast, Blaze, Breeze, Ender Dragon, Vex, Wither