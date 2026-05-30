---
navigation:
    title: 电浆果
    position: 1
    icon: spark_fruit
    parent: resources/index.md
---

# 电浆果

## 概述

<ItemImage id="spark_fruit" scale="2" />

热带雨林是植物的“避雷针”，这使其成为<Color id="electric">天然电现象</Color>的热门地点；相关的一种奇异现象是树冠上生长的黄绿色**电浆果**。

## 自然生成

电浆果自然生成于丛林树叶的底面。完全成熟的电浆果会产生亮度等级为 7 的光照，因此在夜间探索丛林会更容易找到电浆果。

<GameScene zoom={2} interactive={true}>
<ImportStructure src="spark_fruit_tree.nbt" />
</GameScene>

## 种植

电浆果可徒手或用任意工具破坏。电浆果只能种植在丛林树叶的地面，且完全成熟前会经过 2 个生长阶段。

- 完全成熟的植株会掉落 3 个电浆果，掉落数量会受到<Color id="light_purple">时运</Color>加成。
- 未完全成熟的植株会掉落自身。

## 用途

### 电解质浆液

<ItemImage id="electric_chemical" scale="2" />

使用[粉碎机](../machines/gpm/grinder.md)可以提取出具有导电性和电容性的电浆果浆液。这种成分是制造高级电力组件及升级的必需品。