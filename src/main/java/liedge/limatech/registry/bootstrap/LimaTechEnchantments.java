package liedge.limatech.registry.bootstrap;

import liedge.limatech.LimaTech;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import static liedge.limacore.data.generation.LimaBootstrapUtil.registerEnchantment;

public final class LimaTechEnchantments
{
    private LimaTechEnchantments() {}

    public static final ResourceKey<Enchantment> RAZOR = LimaTech.RESOURCES.resourceKey(Registries.ENCHANTMENT, "razor");
    public static final ResourceKey<Enchantment> AMMO_SCAVENGER = LimaTech.RESOURCES.resourceKey(Registries.ENCHANTMENT, "ammo_scavenger");

    public static void bootstrap(BootstrapContext<Enchantment> context)
    {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        Enchantment.Builder razor = Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                        1,
                        3,
                        Enchantment.dynamicCost(10, 9),
                        Enchantment.dynamicCost(60, 9),
                        3,
                        EquipmentSlotGroup.MAINHAND));

        Enchantment.Builder ammoScavenger = Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.SHARP_WEAPON_ENCHANTABLE),
                1,
                3,
                Enchantment.dynamicCost(10, 9),
                Enchantment.dynamicCost(60, 9),
                2,
                EquipmentSlotGroup.MAINHAND));

        registerEnchantment(context, RAZOR, razor);
        registerEnchantment(context, AMMO_SCAVENGER, ammoScavenger);
    }
}