---
navigation:
    title: Ore Clusters
    position: 0
    icon: raw_titanium_cluster
    parent: resources/index.md
---

# Ore Clusters

## About

Sometimes, mineral deposition levels are high enough to create large chunks of raw ore stuck to solid surfaces. The
conditions for this to occur are much stricter, and the location profile of the mineral chunks can even be
entirely mismatched.

## Natural Generation

<GameScene zoom={2} interactive={true}>
<Block id="minecraft:basalt" x="0" />
<Block id="minecraft:purpur_block" x="1" />
<Block id="raw_titanium_cluster" x="0" y="1" />
<Block id="raw_niobium_cluster" x="1" y="1" />

</GameScene>

Inconveniently, Titanium and Niobium raw ore chunks spawn in much more dangerous locations than their[normal counterpart](ores.md).

### Titanium

- Location: Basalt Deltas, Nether
- Height Range: Any
- Restrictions: Only on sturdy *top* block faces

### Niobium

- Location: End Cities
- Height Range: Y=50-150
- Restrictions: Any sturdy block face

## Mining

Ore clusters follow the same mining requirements of their normal counterpart. Clusters drop 3-5 raw ore items, which
are **not** affected by Fortune. Clusters will drop themselves if mined with <Color id="light_purple">Silk Touch</Color>.
Ore Cluster items can be processed in the [Grinder](../machines/gpm/grinder.md), which will always produce 5 raw ore items.