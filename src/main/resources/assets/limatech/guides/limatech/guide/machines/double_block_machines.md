ee---
navigation:
    title: "Double Block Machines"
    parent: machines/index.md
---

# Double Block Machinery

Certain machinery is too large to fit in one block, and *occupy 2 vertical blocks of space*. The lower block is the **base**
and the upper block is the **auxiliary** block. Like doors, breaking either half will harvest the entire machine.

## Automation/IO Configuration

Only the **base** contains a ticking block entity. As such, only the base can have its IO configured and accept
connections from energy cables, hoppers, item or fluid pipes, etc.

The auxiliary block's presence does mean that only IO for the *front, rear, left, right, and bottom* sides will be
available. You won't be able to use the base's *top* side.