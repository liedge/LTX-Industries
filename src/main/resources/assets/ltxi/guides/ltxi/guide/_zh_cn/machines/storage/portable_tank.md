---
navigation:
    title: 便携储罐
    position: 2
    parent: machines/storage/index.md
    icon: portable_tank
item_ids:
  - portable_tank
  - infinite_water_tank
  - infinite_lava_tank
---

# 便携储罐

<BlockImage id="portable_tank" scale="4" />

**便携储罐**是大多数情况下的流体储存选择，其具有可配置的输入输出，并且可以[升级](../../fundamentals/upgrade_system.md)。便携储罐在被破坏时会保留内容物，并且其物品形式和方块形式均具有**流体交互能力**。其物品形式没有传输速率限制，而其方块形式的传输速率限制为最大容量的 1/10。

> 移除容量升级时请注意：若储罐内的流体储量超过移除升级后的最大容量，则超出容量的流体会直接消失。

## 特殊无限变种

<Row>
<BlockImage id="infinite_water_tank" scale="2" />
<BlockImage id="infinite_lava_tank" scale="2" />
</Row>

这两种便携储罐具有特殊的名称和外观，且能够提供无限量（准确地说是 `Integer.MAX_VALUE`）的流体源与传输速率。两种无限储罐的自动输出速率（5 tick 一次）也高于普通储罐的速率（20 tick 一次）。
