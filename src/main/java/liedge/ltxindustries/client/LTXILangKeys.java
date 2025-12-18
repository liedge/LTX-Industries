package liedge.ltxindustries.client;

import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXILangKeys
{
    private LTXILangKeys() {}

    public static final String UPGRADE_EFFECT_PREFIX = "upgrade_effect";

    // Death messages
    public static final Translatable INVALID_WEAPON_DEATH_MESSAGE = RESOURCES.translationHolder("death.attack.{}.unknown_weapon");
    public static final Translatable STRAY_PROJECTILE_DEATH_MESSAGE = RESOURCES.translationHolder("death.attack.{}.stray_projectile");

    // GUI Tooltips
    public static final Translatable NONE_UNIVERSAL_TOOLTIP = tooltip("none");
    public static final Translatable BACK_BUTTON_LABEL = tooltip("back_button");
    public static final Translatable AUTO_OUTPUT_OFF_TOOLTIP = tooltip("auto_output_off");
    public static final Translatable AUTO_OUTPUT_ON_TOOLTIP = tooltip("auto_output_on");
    public static final Translatable AUTO_INPUT_OFF_TOOLTIP = tooltip("auto_input_off");
    public static final Translatable AUTO_INPUT_ON_TOOLTIP = tooltip("auto_input_on");
    public static final Translatable MACHINE_UPGRADES_SIDEBAR_TOOLTIP = tooltip("machine_upgrades");
    public static final Translatable RECIPE_MODES_TITLE_OR_TOOLTIP = tooltip("recipe_mode");
    public static final Translatable RECIPE_MODE_CURRENT_MODE = tooltip("recipe_mode.current");
    public static final Translatable JEI_RECIPE_MODE_NEEDED = tooltip("jei.mode.needed");
    public static final Translatable JEI_NO_RECIPE_MODE_NEEDED = tooltip("jei.mode.not_needed");

    public static final Translatable INLINE_ENERGY = tooltip("energy");
    public static final Translatable INLINE_ENERGY_TRANSFER_RATE = tooltip("energy_transfer_rate");
    public static final Translatable INLINE_ENERGY_USAGE = tooltip("energy_usage");
    public static final Translatable INLINE_ENERGY_REQUIRED_TOOLTIP = tooltip("energy_required");
    public static final Translatable INLINE_NO_OWNER_TOOLTIP = tooltip("no_owner");
    public static final Translatable INLINE_OWNER_TOOLTIP = tooltip("owner");
    public static final Translatable ENERGY_OVERCHARGE_TOOLTIP = tooltip("energy_overcharge");

    public static final Translatable BLUEPRINT_TOAST_MESSAGE = suffixOnlyKey("bp_toast_title");
    public static final Translatable MACHINE_TICKS_PER_OP_TOOLTIP = tooltip("ticks_per_op");
    public static final Translatable EMPTY_ITEM_INVENTORY_TOOLTIP = tooltip("empty_item_inventory_hint");
    public static final Translatable ITEM_INVENTORY_TOOLTIP = tooltip("item_inventory_hint");
    public static final Translatable FABRICATOR_SELECTED_RECIPE_TOOLTIP = tooltip("fabricator_selected");
    public static final Translatable WORKING_PROGRESS_TOOLTIP = tooltip("work_progress");
    public static final Translatable CRAFTING_PROGRESS_TOOLTIP = tooltip("craft_progress");
    public static final Translatable JEI_CRAFTING_TIME_TOOLTIP = tooltip("jei_craft_time");

    public static final Translatable INPUT_NOT_CONSUMED_TOOLTIP = tooltip("input.no_consume");
    public static final Translatable INPUT_CONSUME_CHANCE_TOOLTIP = tooltip("input.use_chance");
    public static final Translatable OUTPUT_CHANCE_TOOLTIP = tooltip("output.chance");
    public static final Translatable OUTPUT_VARIABLE_COUNT_TOOLTIP = tooltip("output.variable");
    public static final Translatable OUTPUT_NON_PRIMARY_TOOLTIP = tooltip("output.non_primary");

    // System Messages
    public static final Translatable UPGRADE_INSTALL_SUCCESS = sysMsg("upgrade_success");
    public static final Translatable UPGRADE_INSTALL_FAIL = sysMsg("upgrade_fail");
    public static final Translatable IO_CARD_CLEARED = sysMsg("io_card.cleared");
    public static final Translatable IO_CARD_COPIED = sysMsg("io_card.copied");
    public static final Translatable IO_CARD_PASTED = sysMsg("io_card.pasted");
    public static final Translatable IO_CARD_SAME_CONFIG = sysMsg("io_card.same_config");
    public static final Translatable IO_CARD_INVALID_SETUP = sysMsg("io_card.invalid_setup");
    public static final Translatable IO_CARD_INVALID_TYPE = sysMsg("io_card.invalid_type");

    // Item hints
    public static final Translatable INVALID_UPGRADE_HINT = itemHint("invalid_upgrade");
    public static final Translatable INVALID_BLUEPRINT_HINT = itemHint("invalid_blueprint");
    public static final Translatable EMPTY_IO_CARD_HINT = itemHint("io_card.empty");
    public static final Translatable ENCODED_IO_CARD_HINT = itemHint("io_card.encoded");

    // Static upgrade upgrade titles
    public static final Translatable TOOL_DEFAULT_UPGRADE_TITLE = prefixKey("equipment_upgrade", "tool_default");

    // Upgrades tooltips
    public static final Translatable UPGRADE_REMOVE_HINT = tooltip("upgrade_remove_hint");
    public static final Translatable UPGRADE_RANK_TOOLTIP = suffixOnlyKey("upgrade_rank");
    public static final Translatable UPGRADE_COMPATIBILITY_TOOLTIP = tooltip("upgrade_compatibility");
    public static final Translatable EQUIPMENT_UPGRADE_MODULE_TOOLTIP = tooltip("equipment_upgrade_module");
    public static final Translatable MACHINE_UPGRADE_MODULE_TOOLTIP = tooltip("machine_upgrade_module");
    public static final Translatable DAMAGE_ATTRIBUTES_EFFECT_PREFIX = tooltip("damage_attributes_prefix");

    // Upgrade effects
    public static final Translatable ENERGY_CAPACITY_UPGRADE = upgradeEffect("energy_capacity");
    public static final Translatable ENERGY_TRANSFER_UPGRADE = upgradeEffect("energy_transfer");
    public static final Translatable ENERGY_USAGE_UPGRADE = upgradeEffect("energy_usage");
    public static final Translatable PARALLEL_OPERATIONS_UPGRADE = upgradeEffect("parallel");
    public static final Translatable MACHINE_SPEED_UPGRADE = upgradeEffect("machine_speed");
    public static final Translatable ENERGY_PER_RECIPE_UPGRADE = upgradeEffect("energy_per_recipe");
    public static final Translatable INSTANT_PROCESSING_UPGRADE = upgradeEffect("instant_process");
    public static final Translatable PROJECTILE_SPEED_UPGRADE = upgradeEffect("projectile_speed");
    public static final Translatable ATTRIBUTE_SCALED_DAMAGE_UPGRADE = upgradeEffect("attribute_scaled_damage");

    // Auto-generated upgrade effects
    public static final Translatable MINIMUM_MACHINE_SPEED_EFFECT = upgradeEffect("min_machine_speed");
    public static final Translatable MINING_EFFECTIVE_BLOCKS_EFFECT = upgradeEffect("mining.effective");
    public static final Translatable MINING_BASE_SPEED_EFFECT = upgradeEffect("mining.speed");
    public static final Translatable DYNAMIC_DAMAGE_TAG_EFFECT = upgradeEffect("dynamic_tag");
    public static final Translatable REDUCTION_MODIFIER_EFFECT = upgradeEffect("reduction_mod");
    public static final Translatable BUBBLE_SHIELD_EFFECT = upgradeEffect("bubble_shield");
    public static final Translatable MOB_EFFECT_UPGRADE_EFFECT = upgradeEffect("mob_effect");
    public static final Translatable DIRECT_BLOCK_DROPS_EFFECT = upgradeEffect("direct_drops.block");
    public static final Translatable DIRECT_ENTITY_DROPS_EFFECT = upgradeEffect("direct_drops.entity");
    public static final Translatable SUPPRESS_VIBRATIONS_EFFECT = upgradeEffect("suppress_vibrations");
    public static final Translatable ENCHANTMENT_UPGRADE_EFFECT = upgradeEffect("enchantment");
    public static final Translatable CAPPED_ENCHANTMENT_UPGRADE_EFFECT = upgradeEffect("enchantment.capped");
    public static final Translatable GRENADE_UNLOCK_EFFECT = upgradeEffect("grenade_unlock");

    public static String namedDamageTagKey(TagKey<DamageType> tagKey)
    {
        return ModResources.prefixedIdLangKey("lima.damage_tag", tagKey.location());
    }

    private static Translatable prefixKey(String prefix, String key)
    {
        return RESOURCES.translationHolder(prefix, "{}", key);
    }

    private static Translatable suffixOnlyKey(String suffix)
    {
        return RESOURCES.translationHolder("{}", suffix);
    }

    private static Translatable sysMsg(String key)
    {
        return prefixKey("msg", key);
    }

    private static Translatable itemHint(String key)
    {
        return prefixKey("hint", key);
    }

    public static Translatable tooltip(String key)
    {
        return prefixKey("tooltip", key);
    }

    public static Translatable upgradeEffect(String key)
    {
        return prefixKey(UPGRADE_EFFECT_PREFIX, key);
    }
}