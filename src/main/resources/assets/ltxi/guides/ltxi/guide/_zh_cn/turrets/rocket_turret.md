---
navigation:
    title: 御风
    icon: rocket_turret
    parent: turrets/index.md
    position: 1
item_ids:
  - rocket_turret
---

# *“御风”* A/DS 防空火箭炮塔

<GameScene zoom={4} interactive={false}>
<Block id="mesh_block" y="0" />
<Block id="rocket_turret" y="-1" />
<BlockAnnotation x="0" y="-1" z="0" color="#8bd1f0">
物品和能量仅可通过此方块交互！
</BlockAnnotation>
</GameScene>

## 属性
- 水平索敌半径：50.0
- 垂直索敌半径：75.0
- 目标搜寻时间间隔：5 秒
- 每次齐射的最大目标数量：4
- 火箭弹伤害：40.0

## 默认目标

具有 `#ltxi:targets/flying` 标签的实体，如幻翼、恶魂、烈焰人、末影龙、恼鬼、凋灵。