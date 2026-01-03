package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.entity.TargetPredicate;
import liedge.ltxindustries.lib.upgrades.UpgradeContexts;
import liedge.ltxindustries.lib.upgrades.effect.*;
import liedge.ltxindustries.lib.upgrades.effect.entity.EntityUpgradeEffect;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

public final class LTXIUpgradeEffectComponents
{
    private static final DeferredRegister COMPONENTS = new DeferredRegister();

    private LTXIUpgradeEffectComponents() {}

    public static void register(IEventBus bus)
    {
        COMPONENTS.register(bus);
    }

    //#region Common effects
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> ENERGY_CAPACITY = COMPONENTS.registerValue("energy_capacity");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> ENERGY_TRANSFER_RATE = COMPONENTS.registerValue("energy_transfer_rate");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> ENERGY_USAGE = COMPONENTS.registerValue("energy_usage");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetableEffect<EntityUpgradeEffect>>>> PRE_ATTACK = COMPONENTS.registerTargetableEntity("pre_attack", UpgradeContexts.UPGRADED_DAMAGE);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetableEffect<EntityUpgradeEffect>>>> ENTITY_KILLED = COMPONENTS.registerTargetableEntity("entity_killed", LootContextParamSets.ENTITY);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CaptureMobDrops>> CAPTURE_MOB_DROPS = COMPONENTS.register("capture_mob_drops", () -> UpgradeDataComponentType.create(CaptureMobDrops.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<TagKey<DamageType>>>> EXTRA_DAMAGE_TAGS = COMPONENTS.register("extra_damage_tags", () -> UpgradeDataComponentType.customList(TagKey.codec(Registries.DAMAGE_TYPE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<AddDamageAttributes>>> ADD_DAMAGE_ATTRIBUTES = COMPONENTS.register("add_damage_attributes", () -> UpgradeDataComponentType.createList(AddDamageAttributes.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionEffect<BreachDamageReduction>>>> REDUCTION_BREACH = COMPONENTS.register("reduction_breach", () -> UpgradeDataComponentType.createConditional(BreachDamageReduction.CODEC, UpgradeContexts.UPGRADED_DAMAGE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<LootItemCondition>>> TARGET_CONDITIONS = COMPONENTS.register("target_conditions", () -> UpgradeDataComponentType.customList(TargetPredicate.CONDITIONS_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<AddEnchantmentLevels>>> ENCHANTMENT_LEVELS = COMPONENTS.register("enchantment_levels", () -> UpgradeDataComponentType.createList(AddEnchantmentLevels.CODEC));
    //#endregion

    //#region Equipment effects
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionEffect<EntityUpgradeEffect>>>> EQUIPMENT_TICK = COMPONENTS.registerConditionEntity("tick", UpgradeContexts.UPGRADED_ENTITY);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionEffect<Unit>>>> DAMAGE_IMMUNITY = COMPONENTS.register("damage_immunity", () -> UpgradeDataComponentType.customConditional(Unit.CODEC, LootContextParamSets.ENTITY));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<MobEffectImmunity>>> MOB_EFFECT_IMMUNITY = COMPONENTS.register("mob_effect_immunity", () -> UpgradeDataComponentType.customList(MobEffectImmunity.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<AddItemAttributes>>> ADD_ITEM_ATTRIBUTES = COMPONENTS.register("add_item_attributes", () -> UpgradeDataComponentType.createList(AddItemAttributes.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ModularTool>>> MODULAR_TOOL = COMPONENTS.register("modular_tool", () -> UpgradeDataComponentType.customList(ModularTool.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<VeinMine>>> VEIN_MINE = COMPONENTS.register("vein_mine", () -> UpgradeDataComponentType.customList(VeinMine.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<CaptureBlockDrops>>> CAPTURE_BLOCK_DROPS = COMPONENTS.register("capture_block_drops", () -> UpgradeDataComponentType.createList(CaptureBlockDrops.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<SuppressVibrations>>> SUPPRESS_VIBRATIONS = COMPONENTS.register("suppress_vibrations", () -> UpgradeDataComponentType.createList(SuppressVibrations.CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionEffect<ValueOperation>>>> EQUIPMENT_DAMAGE = COMPONENTS.registerConditionalValue("equipment_damage", LootContextParamSets.ENTITY);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionEffect<ValueOperation>>>> DAMAGE_REDUCTION = COMPONENTS.registerConditionalValue("damage_reduction", UpgradeContexts.UPGRADED_DAMAGE);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> MAGAZINE_CAPACITY = COMPONENTS.registerValue("magazine_capacity");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> WEAPON_RANGE = COMPONENTS.registerValue("weapon_range");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> RELOAD_SPEED = COMPONENTS.registerValue("reload_speed");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponReloadSource>> RELOAD_SOURCE = COMPONENTS.register("reload_source", () -> UpgradeDataComponentType.create(WeaponReloadSource.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> MAX_HITS = COMPONENTS.registerValue("max_hits");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> BLOCK_PIERCE_DISTANCE = COMPONENTS.registerValue("punch_through");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GrenadeType>>> GRENADE_UNLOCK = COMPONENTS.register("grenade_unlock", () -> UpgradeDataComponentType.createList(GrenadeType.CODEC));
    //#endregion

    //#region Machine effects
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> MACHINE_ENERGY_PRODUCTION = COMPONENTS.registerValue("energy_production");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MinimumMachineSpeed>> MINIMUM_MACHINE_SPEED = COMPONENTS.register("minimum_speed", () -> UpgradeDataComponentType.create(MinimumMachineSpeed.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> TICKS_PER_OPERATION = COMPONENTS.registerValue("ticks_per_operation");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> PARALLEL_OPERATIONS = COMPONENTS.registerValue("parallel_operations");
    //#endregion

    private static class DeferredRegister extends net.neoforged.neoforge.registries.DeferredRegister.DataComponents
    {
        private DeferredRegister()
        {
            super(LTXIRegistries.Keys.UPGRADE_COMPONENT_TYPES, LTXIndustries.MODID);
        }

        public DeferredHolder<DataComponentType<?>, DataComponentType<List<ValueOperation>>> registerValue(String name)
        {
            return register(name, () -> UpgradeDataComponentType.customList(ValueOperation.CONTEXTLESS_CODEC));
        }

        public DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionEffect<ValueOperation>>>> registerConditionalValue(String name, LootContextParamSet params)
        {
            return register(name, () -> UpgradeDataComponentType.customConditional(ValueOperation.codec(params), params));
        }

        public DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionEffect<EntityUpgradeEffect>>>> registerConditionEntity(String name, LootContextParamSet params)
        {
            return register(name, () -> UpgradeDataComponentType.customConditional(EntityUpgradeEffect.codec(params), params));
        }

        public DeferredHolder<DataComponentType<?>, DataComponentType<List<TargetableEffect<EntityUpgradeEffect>>>> registerTargetableEntity(String name, LootContextParamSet params)
        {
            return register(name, () -> UpgradeDataComponentType.customTargetable(EntityUpgradeEffect.codec(params), params));
        }
    }
}