package liedge.limatech.client;

import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTechConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.storage.loot.LootContext;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechLang
{
    private LimaTechLang() {}

    // Death messages
    public static final Translatable INVALID_WEAPON_DEATH_MESSAGE = RESOURCES.translationHolder("death.attack.{}.unknown_weapon");
    public static final Translatable STRAY_PROJECTILE_DEATH_MESSAGE = RESOURCES.translationHolder("death.attack.{}.stray_projectile");

    // GUI Tooltips
    public static final Translatable BACK_BUTTON_LABEL = tooltip("back_button");
    public static final Translatable AUTO_OUTPUT_OFF_TOOLTIP = tooltip("auto_output_off");
    public static final Translatable AUTO_OUTPUT_ON_TOOLTIP = tooltip("auto_output_on");
    public static final Translatable AUTO_INPUT_OFF_TOOLTIP = tooltip("auto_input_off");
    public static final Translatable AUTO_INPUT_ON_TOOLTIP = tooltip("auto_input_on");

    public static final Translatable INLINE_ENERGY_STORED = tooltip("energy_stored");
    public static final Translatable INLINE_ENERGY_CAPACITY = tooltip("energy_capacity");
    public static final Translatable INLINE_ENERGY_TRANSFER_RATE = tooltip("energy_transfer_rate");
    public static final Translatable INLINE_NO_OWNER_TOOLTIP = tooltip("no_owner");
    public static final Translatable INLINE_OWNER_TOOLTIP = tooltip("owner");
    public static final Translatable ENERGY_OVERCHARGE_TOOLTIP = tooltip("energy_overcharge");

    public static final Translatable EMPTY_ITEM_INVENTORY_TOOLTIP = tooltip("empty_item_inventory_hint");
    public static final Translatable ITEM_INVENTORY_TOOLTIP = tooltip("item_inventory_hint");
    public static final Translatable FABRICATOR_CLICK_TO_CRAFT_TOOLTIP = tooltip("fabricator_1");
    public static final Translatable FABRICATOR_ENERGY_REQUIRED_TOOLTIP = tooltip("fabricator_2");
    public static final Translatable CRAFTING_PROGRESS_TOOLTIP = tooltip("craft_progress");

    // Item hints
    public static final Translatable INVALID_UPGRADE_HINT = itemHint("invalid_upgrade");

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

    public static final Translatable WEAPON_KNOCKBACK_EFFECT = upgradeEffect("weapon_knockback");
    public static final Translatable DYNAMIC_DAMAGE_TAG_EFFECT = upgradeEffect("dynamic_tag");
    public static final Translatable SHIELD_UPGRADE_EFFECT = upgradeEffect("shield");
    public static final Translatable NO_SCULK_VIBRATIONS_EFFECT = upgradeEffect("no_vibration");
    public static final Translatable ENERGY_AMMO_EFFECT = upgradeEffect("energy_ammo");
    public static final Translatable INFINITE_AMMO_EFFECT = upgradeEffect("infinite_ammo");
    public static final Translatable ENCHANTMENT_UPGRADE_EFFECT = upgradeEffect("enchantment");
    public static final Translatable GRENADE_UNLOCK_EFFECT = upgradeEffect("grenade_unlock");
    public static final Translatable DIRECT_ITEM_TELEPORT_EFFECT = upgradeEffect("direct_item_teleport");

    public static Component makeEntityTargetComponent(LootContext.EntityTarget target)
    {
        return switch (target)
        {
            case THIS -> THIS_ENTITY_TARGET_TOOLTIP.translate().withStyle(LimaTechConstants.HOSTILE_ORANGE.chatStyle());
            case ATTACKER -> ATTACKER_ENTITY_TARGET_TOOLTIP.translate().withStyle(LimaTechConstants.LIME_GREEN.chatStyle());
            case DIRECT_ATTACKER -> DIRECT_ATTACKER_ENTITY_TARGET_TOOLTIP.translate();
            case ATTACKING_PLAYER -> LAST_ATTACKING_PLAYER_ENTITY_TARGET_TOOLTIP.translate();
        };
    }

    public static String namedDamageTagKey(TagKey<DamageType> tagKey)
    {
        return ModResources.prefixIdTranslationKey("lima.damage_tag", tagKey.location());
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