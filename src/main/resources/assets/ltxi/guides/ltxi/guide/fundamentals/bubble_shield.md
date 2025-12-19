---
navigation:
    title: Bubble Shield
    position: 3
    parent: fundamentals/index.md
---

# Bubble Shields

## About

The <Color id="shield">Bubble Shield</Color> is a personal force field usable by all living entities. It primarily
serves as a damage sink over health like Absorption. It will also block harmful potion effects, at the cost of some
shield health.

## Non-Player Entities

The general shield used by living entities acts as a basic health buffer. It does not have any internal cooldowns.
Any leftover damage upon breaking the shield will bleed through to the entity's health.

## Players

Player bubble shields are more advanced and are much more effective at protecting you from threats. This shield has an
internal cooldown of 1 second between shield health loss from either attacks or blocking potion effects. The bubble
shield automatically recharges to your **Shield Capacity** after 5 seconds of not taking damage or blocking effects.
Some [equipment upgrade](upgrade_system.md#equipment-upgrade-module) modules can overcharge your shield. Shield health
from these sources will not naturally regenerate.

### Shield gating

Player shields will always fully negate damage, even if it is greater than the shield health. When your shield breaks,
you will be invulnerable to damage for 3 seconds (barring any invulnerability-bypassing damage).