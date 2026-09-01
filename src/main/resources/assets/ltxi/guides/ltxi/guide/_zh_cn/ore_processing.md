---
navigation:
  title: 矿物处理
  icon: crushed_silver_ore
  position: 21
---

# 矿物处理

随着游戏进程的推进，你很可能会逐渐无法忍受冗长艰巨的采矿工作。 好消息是， 经过复杂的五步处理流程，模组中的机器可以大幅增产矿石，平均产量能够达到 **10.125** 倍！

即使不考虑倍增矿石，也至少需要通过处理钛、橄榄石和铜矿石来相应地获取钨、辉石和铼。

<ItemGrid>
<ItemIcon id="raw_silver" />
<ItemIcon id="silver_ore" />
</ItemGrid>

矿物处理流程从粗矿或矿石方块开始，经历粉碎、洗涤、切块、溶解和结晶五步，最终得到成品。

<ItemGrid>
<ItemIcon id="crushed_silver_ore" />
<ItemIcon id="washed_silver_ore" />
<ItemIcon id="silver_ore_chunk" />
<ItemIcon id="silver_ore_solution" />
<ItemIcon id="silver_ore_crystal" />
</ItemGrid>

任何一步的中间产物都可直接烧炼为成品 （如粉碎银矿石和银矿石溶液可以直接烧炼为银锭）。 第一步处理可使矿石翻倍，此后的每一步都有 50% 概率使矿石翻倍， 因此进行至第 2\~5 步时矿石&#x7684;_&#x5E73;&#x5747;_&#x589E;产倍率可以达到 3 倍、4.5 倍、6.75 倍或者 10.125 倍。

## 第 1 步

矿物处理的第一步为将粗矿或原矿石粉碎为粉碎矿石，在[粉碎机](machines/processing/grinder.md)中进行， 这也是矿物处理流程中唯一不需要升级和[配方模式](fundamentals/recipe_modes.md)的一步。 这一步在游戏相当早期就可以进行，所以前期可以尽快制作粉碎机。

## 第 2 步

<UpgradeEntry id="ore_process_2" />

粉碎矿石可在[水力筛](machines/processing/hydrosieve.md)中处理为洗净矿石。 从这一阶段开始，处理配方会需要“矿物处理”配方模式，而这种配方模式默认不能在机器中使用， 需要先制作相应的升级模块并安装到相应的机器中才能解锁这一配方模式。

## 第 3 步

<UpgradeEntry id="ore_process_3" />

洗净矿石可在[高压注入器](machines/processing/voltaic_injector.md)中处理为矿石块， 这一阶段需要的升级模块的制造成本会大幅提升。 为了避免瓶颈，此阶段开始建议制造机器供能模块升级。

### 辉石与钨

<ItemGrid>
<ItemIcon id="pyroxene" />
<ItemIcon id="tungsten_trioxide" />
</ItemGrid>

辉石和三氧化钨会分别作为橄榄石和钛的副产物产生。

## 第 4 步

<UpgradeEntry id="ore_process_4" />

矿石块可在[化工厂](machines/processing/chem_lab.md)中消耗少量[硫酸](resources/acids.md#sulfuric-acid)处理为小瓶的矿石溶液。 建议为此步骤搭建专用的硫酸产线。

## 第 5 步

<UpgradeEntry id="ore_process_5" />

矿石溶液可在[电解离心机](machines/processing/electrocentrifuge.md)中，使用[盐酸](resources/acids.md#hydrochloric-acid)处理为矿石晶体。 同样，也建议为此步骤搭建专用的盐酸产线，仅靠生产硅橡胶和氟聚合物的副产品不足以支撑这一步的消耗。

### 七氧化二铼

<ItemGrid>
<ItemIcon id="rhenium_7_oxide" />
</ItemGrid>

七氧化二铼于铜矿处理的最后一步产生， 如果产线到达了这一步之后发现盐酸的供应达到了瓶颈，那么建议优先处理铜矿，因为铼可用于制造最高级的升级和设备。