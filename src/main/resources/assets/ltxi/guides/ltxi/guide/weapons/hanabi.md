---
navigation:
    title: Hanabi
    icon: grenade_launcher
    parent: weapons/index.md
    position: 12
---

# LTX 33/GL *Hanabi*
<ItemImage id="grenade_launcher" scale="3" />

## Stats
- Base Damage: Varies (see below)
- Projectile: Orb Grenade
- Projectile Speed: 30 m/s
- Trigger: Semi Auto
- Trigger Cooldown: 0.75 sec

## Shells

Hanabi can fire several types of shells. By default, only Explosive shells are available. Other types must be unlocked via
[Equipment Upgrade Modules](../fundamentals/upgrade_system.md).

### Explosive
Standard explosive shell. Boring, but reliable.

- Blast radius: 10.0
- Base damage: 30.0
- `#minecraft:is_explosion` damage tag.

### Flame
Deals high damage over time. Recommended against slow moving targets or ice-based enemies. Creates 9 flame entities in a
3x3 pattern. Note: Flame damages stack! 

- Blast radius: 9.0
- Base damage: 10.0
- Flame damage: 4.0 every 4 ticks (80.0 total)
- Flame duration: 4 sec
- `#minecraft:is_fire` damage tag, both impact and flames.
- 3x bonus damage against `#ltxi:weak_to_flame` enemies
  - Strays, Polar Bears, Snow Golems

### Cryo
Cryo shells blast enemies in a large area with ice. Recommended for crowd control or enemies weak to ice.

- Blast radius: 12.0
- Base damage: 4.0
- 8x bonus damage against `#ltxi:weak_to_cryo` enemies
  - By default, this tag is `#minecraft:freeze_hurts_extra`: Striders, Blazes, Magma Cubes
- Applies [*Frostbite III*](../fundamentals/mob_effects.md) for 20 seconds

### Electric
Electric shells' specialty is wet and underwater combat.

- Blast radius: 10.0, *20.0 if shell impacts in rain or water*
- Base damage: 30.0
- 3x bonus damage if either criteria met:
  - Target is in rain or water
  - Is tagged `#ltxi:weak_to_electric`: Turtles, Axolotls, Guardians, Elder Guardians, Fish, Dolphins, Squids, Glow Squids, Tadpoles

### Acid
Acid shells specialize against highly armored or high threat targets. Their blast radius is smaller, but
they deal increased damage to compensate.

- Blast radius: 5.0
- Base damage: 50.0
- Applies [*Corroding III*](../fundamentals/mob_effects.md) for 10 seconds

### Neuro
Neuro shells weaken enemy damage and abilities.

- Blast radius: 5.0
- Base damage: 4.0
- Applies [*Neuro-Suppressed III*](../fundamentals/mob_effects.md) for 30 seconds