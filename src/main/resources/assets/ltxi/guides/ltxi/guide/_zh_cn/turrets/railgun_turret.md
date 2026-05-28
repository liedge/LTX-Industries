---
navigation:
    title: 夜神
    icon: railgun_turret
    parent: turrets/index.md
    position: 2
item_ids:
  - railgun_turret
---

# *“夜神”* A/DS 轨道炮塔

<GameScene zoom={4} interactive={false}>
<Block id="mesh_block" y="0" />
<Block id="railgun_turret" y="-1" />
<BlockAnnotation x="0" y="-1" z="0" color="#8bd1f0">
物品和能量仅可通过此方块交互！
</BlockAnnotation>
</GameScene>

## Stats
- 水平索敌半径：25.0
- 垂直索敌半径：25.0
- 目标搜寻时间间隔：4 秒
- 单体攻击
- 轨道炮弹伤害：200.0

## 默认目标

具有 `#ltxi:targets/high_threat` 标签的实体，如远古守卫者、末影龙、凋灵、监守者。