---
navigation:
    title: "弹药机制"
    icon: lightweight_weapon_energy
    parent: weapons/index.md
    position: 0
item_ids:
  - lightweight_weapon_energy
  - specialist_weapon_energy
  - explosives_weapon_energy
  - heavy_weapon_energy
---

# 弹药机制

所有的信号系列武器均通过其内置弹夹装填系统工作。弹夹装填来源共有 **3** 种：物品、<Color id="energy">能量</Color>、<Color id="light_purple">无限弹夹</Color>。装填来源可通过[升级模块](../fundamentals/upgrade_system.md)更换。
> 注意：在换弹时，弹夹中剩余的充能会**直接消失**。避免无必要的换弹，尤其是使用爆破型和重型武器时！

## 装填来源：物品

<ItemGrid>
<ItemIcon id="lightweight_weapon_energy" />
<ItemIcon id="specialist_weapon_energy" />
<ItemIcon id="explosives_weapon_energy" />
<ItemIcon id="heavy_weapon_energy" />
</ItemGrid>

默认状态下武器会使用对应类别的武器能量进行装填。武器能量物品在击杀敌对等级达到会超过<Color id="red">中立敌人</Color>的生物时有 25% 概率掉落，每级**抢夺**魔咒提升 2.5% 的掉落概率。

### 各级武器能量掉落概率

| 种类      | 概率    |
|---------|-------|
| **轻型**  | 79.2% |
| **专业型** | 14.9% |
| **爆破型** | 4.9%  |
| **重型**  | 1.0%  |

[弹药猎手](../enchantments/ammo_scavenger.md)魔咒会影响各种武器能量的掉落概率，以及能够掉落的物品数量，详见该魔咒的手册页面。 

## 装填来源：能量

武器会消耗 <Color id="energy">CE能量</Color> **进行装填**。CE能量并不会直接给武器充能。消耗的能量取决于装备的**能量消耗**属性。

## 装填来源：无限弹药

武器可通过此种装填来源获得无限弹药。装备时，武器可*一直*开火，即使内部弹夹是“空的”。