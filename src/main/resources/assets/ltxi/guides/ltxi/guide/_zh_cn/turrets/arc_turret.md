---
navigation:
    title: 解离
    icon: arc_turret
    parent: turrets/index.md
    position: 0
item_ids:
  - arc_turret
---

# *“解离”* A/DS 电弧炮塔

<GameScene zoom={4} interactive={false}>
<Block id="mesh_block" y="0" />
<Block id="arc_turret" y="-1" />
<BlockAnnotation x="0" y="-1" z="0" color="#8bd1f0">
物品和能量仅可通过此方块交互！
</BlockAnnotation>
</GameScene>

## 属性
- 水平索敌半径：30.0
- 垂直索敌半径：20.0
- 目标搜寻时间间隔：1.5 秒
- 基础伤害：2.0 / tick

## 默认目标

攻击性达到**中立敌人**或更高的所有实体。

## 概述

*“解离”* 炮塔会在**搜索**操作时选择至多 16 个目标。在此过程中炮塔会将最近的实体设定为主要目标，并且会持续攻击该目标，直到目标死亡或离开攻击范围；此后炮塔会继续选择队列中的下一个合法目标，若没有目标则会回到**搜索**模式。