---
navigation:
    title: 阴郁菇
    position: 1
    icon: gloom_shroom
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
<ImportStructure src="gloom_shroom_farm.nbt" />
<Block id="gloom_shroom" y="1" />
</GameScene>

这些蘑菇渴望生命，它们并不会自然蔓延，使用骨粉也无济于事；要想让其增殖，需要先获取一个幽匿催发体和一些幽匿块，再将初始的蘑菇放置在催发体附近，当催发体激活且产生的能量波穿过阴郁菇时，阴郁菇有 33% 的概率在 *3x3x3 范围内* 产生一株新的阴郁菇。产生新阴郁菇的位置必须是空气或流动水等可被替换的方块。

## 用途

阴郁菇可在[电解离心机](../machines/gpm/electrocentrifuge.md)中用少量[魔能酸](vitriol_berries.md)溶液处理，产物为一瓶回响浆液，有时也会得到一小罐浓缩阴郁提取物。

### 回响浆液

<ItemImage id="sculk_chemical" scale="2" />

回响浆液是阴郁菇数个世纪以来吸收的幽匿成分的精炼产品，在机器中经过简易的提取、纯化和装瓶步骤之后，其可以用于制造幽匿类的科技物品，以及悖论般的[现实虚拟处理器](circuits.md).

### 浓缩阴郁提取物

<ItemImage id="gloom_chemical" scale="2" />

阴郁菇的防御机制是释放出来自监守者的削弱力量，这种力量类似某种毒剂，但根据已知的分类标准，其并不属于任何一种毒剂。浓缩阴郁提取物可用于制造用于 *“花火”* 的[阴郁气体](../weapons/hanabi.md#neuro)弹。

使用*花火*的[强酸弹](../weapons/hanabi.md#acid)**或**在其具有[腐蚀](../fundamentals/mob_effects.md#corroding)效果时击杀监守者必定会额外掉落 1 个浓缩阴郁提取物。