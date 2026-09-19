---
navigation:
  title: 阴郁菇
  icon: gloom_shroom
  position: 12
  parent: resources/index.md
---

# 阴郁菇

## 概述

<ItemImage id="gloom_shroom" scale="2" />

神秘的深暗之域看上去不适合任何熟悉的生命形式生存，然而深入探索这些幽暗的洞穴之后，你可以在其中发现一些异常美丽的<Color id="blue">阴郁菇</Color>，吸收着幽匿方块中的灵魂能量肆意生长。

## 自然生成

阴郁菇在深暗之域生物群系中的幽匿块顶面分散生成，每个区块大约仅会生成 1-2 个。阴郁菇在深暗之域很显眼，其会产生生物荧光，然而不会照亮周围的环境。

## 种植

阴郁菇可以徒手瞬间破坏，无需特殊工具和魔咒即可掉落。

### 适宜的环境

阴郁菇只能在幽匿块上方种植。此外，阴郁菇不能暴露在阳光下，否则会被直接破坏。

### 增殖

<GameScene zoom={2} interactive={true}>
<ImportStructure src="../assets/gloom_shroom_farm.nbt" />
<Block id="gloom_shroom" y="1" />
</GameScene>

这些蘑菇渴望生命，它们并不会自然蔓延，使用骨粉也无济于事；要想让其增殖，需要先获取一个幽匿催发体和一些幽匿块，再将初始的蘑菇放置在催发体附近。当催发体激活且产生的能量波穿过阴郁菇时，阴郁菇有 33% 的概率在 _3x3x3 范围内_ 产生一株新的阴郁菇。产生新阴郁菇的位置必须是空气或流动水等可被替换的方块。

## 产物

### 回响浆液

<ItemImage id="sculk_chemical" scale="2" />

阴郁菇可在[搅拌机](../machines/processing/mixer.md)中用[硫酸](acids.md#sulfuric-acid)处理为回响浆液。

### 浓缩阴郁提取物

<ItemImage id="gloom_weapon_chemical" scale="2" />

使用[氨](elements_compound.md#ammonia)浓缩大量的回响浆液，可以得到浓缩阴郁提取物。这种物质在工业上用途不多，但可用于制造削弱型的武器。

浓缩阴郁提取物也可以通过战斗方式获取。用“花火”的[强酸弹](../weapons/hanabi.md#acid)击杀监守者，或在监守者具有[腐蚀](../fundamentals/mob_effects.md#corroding)状态效果时将其击杀，都可以掉落 1 个浓缩阴郁提取物。