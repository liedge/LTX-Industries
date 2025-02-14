package liedge.limatech.client;

import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechLang
{
    private LimaTechLang() {}

    // Death messages
    public static final Translatable INVALID_WEAPON_DEATH_MESSAGE = RESOURCES.translationHolder("death.attack.{}.unknown_weapon");
    public static final Translatable STRAY_PROJECTILE_DEATH_MESSAGE = RESOURCES.translationHolder("death.attack.{}.stray_projectile");

    // GUI Tooltips
    public static final Translatable TOP_IO_LABEL = tooltip("io_top");
    public static final Translatable BOTTOM_IO_LABEL = tooltip("io_bottom");
    public static final Translatable FRONT_IO_LABEL = tooltip("io_front");
    public static final Translatable REAR_IO_LABEL = tooltip("io_rear");
    public static final Translatable LEFT_IO_LABEL = tooltip("io_left");
    public static final Translatable RIGHT_IO_LABEL = tooltip("io_right");
    public static final Translatable BACK_BUTTON_LABEL = tooltip("back_button");
    public static final Translatable AUTO_OUTPUT_OFF_TOOLTIP = tooltip("auto_output_off");
    public static final Translatable AUTO_OUTPUT_ON_TOOLTIP = tooltip("auto_output_on");
    public static final Translatable AUTO_INPUT_OFF_TOOLTIP = tooltip("auto_input_off");
    public static final Translatable AUTO_INPUT_ON_TOOLTIP = tooltip("auto_input_on");

    public static final Translatable INLINE_ENERGY_STORED = tooltip("energy_stored");
    public static final Translatable INLINE_ENERGY_CAPACITY = tooltip("energy_capacity");
    public static final Translatable INLINE_ENERGY_TRANSFER_RATE = tooltip("energy_transfer_rate");
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

    public static final Translatable TARGET_ATTRIBUTE_VALUE_EFFECT = upgradeEffect("target_attribute");
    public static final Translatable PLAYER_ATTRIBUTE_VALUE_EFFECT = upgradeEffect("player_attribute");

    public static final Translatable WEAPON_KNOCKBACK_EFFECT = upgradeEffect("weapon_knockback");
    public static final Translatable DYNAMIC_DAMAGE_TAG_EFFECT = upgradeEffect("dynamic_tag");
    public static final Translatable SHIELD_UPGRADE_EFFECT = upgradeEffect("shield");
    public static final Translatable NO_SCULK_VIBRATIONS_EFFECT = upgradeEffect("no_vibration");
    public static final Translatable ENERGY_AMMO_EFFECT = upgradeEffect("energy_ammo");
    public static final Translatable INFINITE_AMMO_EFFECT = upgradeEffect("infinite_ammo");
    public static final Translatable ENCHANTMENT_UPGRADE_EFFECT = upgradeEffect("enchantment");
    public static final Translatable GRENADE_UNLOCK_EFFECT = upgradeEffect("grenade_unlock");

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