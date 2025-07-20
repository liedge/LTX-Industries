---
navigation:
    title: "Molecular Reconstructor"
    icon: molecular_reconstructor
    parent: machines/index.md
---

# Molecular Reconstructor

<GameScene zoom={4} interactive={true}>
<Block id="molecular_reconstructor" p:half="upper" />
<Block id="molecular_reconstructor" p:half="lower" y="-1" />
</GameScene>

While our ε tool series uses state-of-the-art technology that lets it run forever on just power, the same can't
be said for your other tools and trinkets.

Enter the *Molecular Reconstructor*. This handy machine uses energy to repair the durability of items at the atomic level.
Place the item you want to repair in the input slot (left). Your item will be automatically moved to the output slot when
it is fully repaired.

*This is a [double block machine](double_block_machines.md)*.

## Compatibility
Items with the `#ltxi:repair_blacklist` tag cannot be repaired, nor any other item that uses non-standard durability
mechanics.