---
navigation:
    title: "Ammunition Basics"
    parent: weapons/index.md
    position: 0
---

# Ammunition Mechanics

All signature-series weapons function off of their internal magazine feed system. There are **3** weapon reload sources:
Items, <Color id="energy">Energy</Color>, and <Color id="light_purple">Infinite</Color>. Reload sources can be changed
via [upgrade modules](../fundamentals/upgrade_system.md).

> Warning: Any remaining charge in the magazine is **lost** upon a reload. Avoid unnecessary reloads, particularly on
> Explosive or Heavy weapons!

## Reload Source: Items

<ItemGrid>
<ItemIcon id="lightweight_weapon_energy" />
<ItemIcon id="specialist_weapon_energy" />
<ItemIcon id="explosives_weapon_energy" />
<ItemIcon id="heavy_weapon_energy" />
</ItemGrid>

By default, weapons use a specific Item to recharge. Weapon energy items have a 25% chance to drop from mobs
of <Color id="red">Neutral-Enemy</Color> hostility or greater. **Looting** adds 2.5% per level to the drop rate.

### Weapon energy distribution

| Type            | Probability |
|-----------------|-------------|
| **Lightweight** | 79.2%       |
| **Specialist**  | 14.9%       |
| **Explosives**  | 4.9%        |
| **Heavy**       | 1.0%        |

The [Ammo Scavenger](../enchantments/ammo_scavenger.md) enchantment affects the drop chance distribution, as well as
the amount of items that can drop. See that page for a more detailed breakdown.

## Reload Source: Energy

The weapon will consume <Color id="energy">Common Energy</Color> to **recharge the magazine**. It will not power the weapon
directly. The amount consumed to reload is determined by the equipment's **Energy Usage** stat.

## Reload Source: Infinite

The weapon gains an infinite magazine with this reload source. When equipped, the weapon can *always* fire, even if the
magazine is 'empty'.