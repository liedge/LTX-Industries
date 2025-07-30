package liedge.ltxindustries.client;

import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.LTXIConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.storage.loot.LootContext;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXILangKeys
{
    private LTXILangKeys() {}

    // Death messages
    public static final Translatable INVALID_WEAPON_DEATH_MESSAGE = RESOURCES.translationHolder("death.attack.{}.unknown_weapon");
    public static final Translatable STRAY_PROJECTILE_DEATH_MESSAGE = RESOURCES.translationHolder("death.attack.{}.stray_projectile");

    // GUI Tooltips
    public static final Translatable BACK_BUTTON_LABEL = tooltip("back_button");
    public static final Translatable AUTO_OUTPUT_OFF_TOOLTIP = tooltip("auto_output_off");
    public static final Translatable AUTO_OUTPUT_ON_TOOLTIP = tooltip("auto_output_on");
    public static final Translatable AUTO_INPUT_OFF_TOOLTIP = tooltip("auto_input_off");
    public static final Translatable AUTO_INPUT_ON_TOOLTIP = tooltip("auto_input_on");

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

    public static final Translatable OUTPUT_CHANCE_TOOLTIP = tooltip("output_chance");
    public static final Translatable ADVANCEMENT_LOCKED_TOOLTIP = tooltip("advancement_locked");

    // System Messages
    public static final Translatable UPGRADE_INSTALL_SUCCESS = prefixKey("msg", "upgrade_success");
    public static final Translatable UPGRADE_INSTALL_FAIL = prefixKey("msg", "upgrade_fail");

    // Item hints
    public static final Translatable INVALID_UPGRADE_HINT = itemHint("invalid_upgrade");
    public static final Translatable INVALID_BLUEPRINT_HINT = itemHint("invalid_blueprint");

    // Static upgrade upgrade titles
    public static final Translatable TOOL_DEFAULT_UPGRADE_TITLE = prefixKey("equipment_upgrade", "tool_default");

    // Upgrades tooltips
    public static final Translatable UPGRADE_REMOVE_HINT = tooltip("upgrade_remove_hint");
    public static final Translatable UPGRADE_RANK_TOOLTIP = suffixOnlyKey("upgrade_rank");
    public static final Translatable UPGRADE_COMPATIBILITY_TOOLTIP = tooltip("upgrade_compatibility");
    public static final Translatable EQUIPMENT_UPGRADE_MODULE_TOOLTIP = tooltip("equipment_upgrade_module");
    public static final Translatable MACHINE_UPGRADE_MODULE_TOOLTIP = tooltip("machine_upgrade_module");

    public static final Translatable THIS_ENTITY_TARGET_TOOLTIP = tooltip("entity_target.this");
    public static final Translatable ATTACKER_ENTITY_TARGET_TOOLTIP = tooltip("entity_target.attacker");
    public static final Translatable DIRECT_ATTACKER_ENTITY_TARGET_TOOLTIP = tooltip("entity_target.direct_attacker");
    public static final Translatable LAST_ATTACKING_PLAYER_ENTITY_TARGET_TOOLTIP = tooltip("entity_target.last_player");
    public static final Translatable ATTRIBUTE_AMOUNT_VALUE_TOOLTIP = prefixKey("complex_value", "attribute_amount");

    public static final Translatable MINING_EFFECTIVE_BLOCKS_EFFECT = upgradeEffect("mining.effective");
    public static final Translatable MINING_BASE_SPEED_EFFECT = upgradeEffect("mining.speed");
    public static final Translatable WEAPON_KNOCKBACK_EFFECT = upgradeEffect("weapon_knockback");
    public static final Translatable DYNAMIC_DAMAGE_TAG_EFFECT = upgradeEffect("dynamic_tag");
    public static final Translatable REDUCTION_MODIFIER_EFFECT = upgradeEffect("reduction_mod");
    public static final Translatable BUBBLE_SHIELD_EFFECT = upgradeEffect("bubble_shield");
    public static final Translatable MOB_EFFECT_UPGRADE_EFFECT = upgradeEffect("mob_effect");
    public static final Translatable DIRECT_BLOCK_DROPS_EFFECT = upgradeEffect("direct_drops.block");
    public static final Translatable DIRECT_ENTITY_DROPS_EFFECT = upgradeEffect("direct_drops.entity");
    public static final Translatable SUPPRESS_VIBRATIONS_EFFECT = upgradeEffect("suppress_vibrations");
    public static final Translatable ENERGY_AMMO_EFFECT = upgradeEffect("energy_ammo");
    public static final Translatable INFINITE_AMMO_EFFECT = upgradeEffect("infinite_ammo");
    public static final Translatable ENCHANTMENT_UPGRADE_EFFECT = upgradeEffect("enchantment");
    public static final Translatable GRENADE_UNLOCK_EFFECT = upgradeEffect("grenade_unlock");

    public static Component makeEntityTargetComponent(LootContext.EntityTarget target)
    {
        return switch (target)
        {
            case THIS -> THIS_ENTITY_TARGET_TOOLTIP.translate().withStyle(LTXIConstants.HOSTILE_ORANGE.chatStyle());
            case ATTACKER -> ATTACKER_ENTITY_TARGET_TOOLTIP.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle());
            case DIRECT_ATTACKER -> DIRECT_ATTACKER_ENTITY_TARGET_TOOLTIP.translate();
            case ATTACKING_PLAYER -> LAST_ATTACKING_PLAYER_ENTITY_TARGET_TOOLTIP.translate();
        };
    }

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

    private static Translatable itemHint(String key)
    {
        return prefixKey("hint", key);
    }

    private static Translatable tooltip(String key)
    {
        return prefixKey("tooltip", key);
    }

    private static Translatable upgradeEffect(String key)
    {
        return prefixKey("upgrade_effect", key);
    }
}