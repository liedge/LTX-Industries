---
navigation:
    title: "Mob Effects"
    parent: fundamentals/index.md
---

# Mob Effects (Potion Effects)

## Frostbite
Frostbite slows down targets. It also freezes them for the duration of the effect.
### Attribute modifiers
- Attack Speed: -25% x effect level.
- Movement Speed: -25% x effect level.
- Fly Speed: -25% x effect level.
- Dig Speed: -25% x effect level.

-------

## Corroding
Corroding weakens target armor.
### Attribute modifiers
- Armor: -33% x effect level.
- Armor Toughness: -33% x effect level.

-------

## Neuro-Suppressed
Neuro-Suppressed reduces *all* of a target's attack damage, as long as it can be traced back to it. In addition,
the affected target cannot apply any potion effects during the effect duration.

### Attribute modifiers
- `limacore:damage_multiplier`: -25% x effect level