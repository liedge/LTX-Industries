package liedge.limatech.client;

import liedge.limacore.lib.Translatable;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechLangKeys
{
    private LimaTechLangKeys() {}

    public static final Translatable ENERGY_TOOLTIP = tooltip("energy");
    public static final Translatable ITEM_ENERGY_TOOLTIP = tooltip("item_energy_hint");
    public static final Translatable ITEM_INVENTORY_TOOLTIP = tooltip("item_inventory_int");
    public static final Translatable FABRICATOR_CLICK_TO_CRAFT_TOOLTIP = tooltip("fabricator_1");
    public static final Translatable FABRICATOR_ENERGY_REQUIRED_TOOLTIP = tooltip("fabricator_2");
    public static final Translatable CRAFTING_PROGRESS_TOOLTIP = tooltip("craft_progress");

    private static Translatable tooltip(String key)
    {
        return RESOURCES.translationHolder("tooltip", "{}", key);
    }
}