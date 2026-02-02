---
navigation:
    title: "Turrets"
    icon: arc_turret
    position: 21
---

# A/DS Turrets

The Automated Defense System product line, A/DS, specializes in keeping an area free of threats. All turrets are
[upgradeable machines](../fundamentals/upgrade_system.md).

## Available Turrets

- [*Ionos*](arc_turret.md)
- [*Atmos*](rocket_turret.md)
- [*Noctis*](railgun_turret.md)

## Operation

Turrets run solely off of <Color id="energy">Energy</Color>, no additional items required. The inventory inside the turrets
is an output inventory used only if a loot drop capture upgrade is installed.

## Targeting

Each turret has its own default target filter, with some being more selective than others. This target logic can be
overridden by upgrade modules.

## Turret states

Turrets have 5 distinct states they can be in at any time: **Inactive**, **Searching**, **Charging**, **Firing**, and
**Cooldown**. Each state carries out its own logic, with most being self-explanatory.

### Inactive

The state of a newly crafted turret or a turret that has run out of energy. Once per second, it will check its
energy buffer to see if it has enough energy for at least one operation. If it does, it will switch to Searching.

### Searching

Active turrets will spend most of their time in this stage. The turret will search for valid targets in its search area
at a time interval determined by the time of turret. If targets are found, they are loaded into the queue and the
turret will switch to the **Charging** state.

### Charging/Cooldown

These 2 states behave identically, the only difference being the time spent on this state. Consider this the wind up or
between shot cooldown. Each turret has its own charging/cooldown times.