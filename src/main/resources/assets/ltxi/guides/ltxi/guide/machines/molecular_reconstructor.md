---
navigation:
    title: "Molecular Reconstructor"
    icon: molecular_reconstructor
    parent: machines/index.md
    position: 31
item_ids:
    - molecular_reconstructor
---

# Molecular Reconstructor

<GameScene zoom={4} interactive={false}>
<Block id="mesh_block" y="0" />
<Block id="molecular_reconstructor" y="-1" />
<BlockAnnotation x="0" y="-1" z="0" color="#8bd1f0">
Items and Energy can only be connected to this block!
</BlockAnnotation>
</GameScene>

While our ε tool series uses state-of-the-art technology that lets it run forever on just power, the same can't
be said for your other tools and trinkets.

Enter the *Molecular Reconstructor*. This handy machine uses energy to repair the durability of items at the atomic level.
Place the item you want to repair in the input slot (left). Your item will be automatically moved to the output slot when
it is fully repaired.

## Compatibility
Items with the `#ltxi:repair_blacklist` tag cannot be repaired, nor any other item that uses non-standard durability
mechanics.