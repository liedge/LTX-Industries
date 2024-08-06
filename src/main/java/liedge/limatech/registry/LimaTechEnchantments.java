package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class LimaTechEnchantments
{
    private LimaTechEnchantments() {}

    public static final ResourceKey<Enchantment> RAZOR = LimaTech.RESOURCES.resourceKey(Registries.ENCHANTMENT, "razor");
    public static final ResourceKey<Enchantment> AMMO_SCAVENGER = LimaTech.RESOURCES.resourceKey(Registries.ENCHANTMENT, "ammo_scavenger");
}