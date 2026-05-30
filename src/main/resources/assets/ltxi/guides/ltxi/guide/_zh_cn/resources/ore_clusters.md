---
navigation:
    title: 矿簇
    position: 0
    icon: raw_titanium_cluster
    parent: resources/index.md
---

# 矿簇

## 概述

有时矿物的沉积程度会相当高，足够产生嵌入固体方块表面的大块粗矿石。产生矿簇的条件比产生普通矿石的要严苛很多，并且其生成位置与实际情况可能完全不匹配。

## 自然生成

<GameScene zoom={2} interactive={true}>
<Block id="minecraft:basalt" x="0" />
<Block id="minecraft:purpur_block" x="1" />
<Block id="raw_titanium_cluster" x="0" y="1" />
<Block id="raw_niobium_cluster" x="1" y="1" />

</GameScene>

不太方便的一点是，相较于其[普通版本](ores.md)，钛矿簇和铌矿簇会生成在危险得多的地方。

### 钛矿簇

- 生成位置：下界的玄武岩三角洲
- 高度范围：任意
- 限制：只能生成在方块的*坚实顶面*

### 铌矿簇

- 生成位置：末地城
- 高度范围：Y=50-150
- 限制：任何坚实的方块面

## 挖掘

矿簇的挖掘要求与其普通版本一致，挖掘后掉落 3-5 个粗矿，**不受**时运影响。使用带有<Color id="light_purple">精准采集</Color>的工具挖掘矿簇时会掉落本身，可在[粉碎机](../machines/gpm/grinder.md)中处理，并总是产出 5 个粗矿。