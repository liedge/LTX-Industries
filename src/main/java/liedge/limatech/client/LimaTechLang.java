package liedge.limatech.client;

import liedge.limacore.lib.Translatable;

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

    public static final Translatable MACHINE_TIER_TOOLTIP = tooltip("machine_tier");
    public static final Translatable INLINE_ENERGY_STORED = tooltip("energy_stored");
    public static final Translatable INLINE_ENERGY_CAPACITY = tooltip("energy_capacity");
    public static final Translatable INLINE_ENERGY_TRANSFER_RATE = tooltip("energy_transfer_rate");

    public static final Translatable EMPTY_ITEM_INVENTORY_TOOLTIP = tooltip("empty_item_inventory_hint");
    public static final Translatable ITEM_INVENTORY_TOOLTIP = tooltip("item_inventory_hint");
    public static final Translatable FABRICATOR_CLICK_TO_CRAFT_TOOLTIP = tooltip("fabricator_1");
    public static final Translatable FABRICATOR_ENERGY_REQUIRED_TOOLTIP = tooltip("fabricator_2");
    public static final Translatable CRAFTING_PROGRESS_TOOLTIP = tooltip("craft_progress");

    // Equipment upgrades tooltips
    public static final Translatable UPGRADE_REMOVE_HINT = tooltip("upgrade_remove_hint");
    public static final Translatable EQUIPMENT_UPGRADE_RANK = tooltip("upgrade_rank");
    public static final Translatable UPGRADE_COMPATIBLE_ALL_WEAPONS = tooltip("upgrade_compatibility.all");
    public static final Translatable UPGRADE_COMPATIBLE_SPECIFIC_WEAPONS = tooltip("upgrade_compatibility.specific");
    public static final Translatable EQUIPMENT_UPGRADE_ITEM_CUSTOM_NAME = RESOURCES.translationHolder("item.{}.equipment_upgrade.custom");
    public static final Translatable INVALID_EQUIPMENT_UPGRADE_ITEM = RESOURCES.translationHolder("item.{}.invalid_equipment_upgrade");

    private static Translatable tooltip(String key)
    {
        return RESOURCES.translationHolder("tooltip", "{}", key);
    }
}