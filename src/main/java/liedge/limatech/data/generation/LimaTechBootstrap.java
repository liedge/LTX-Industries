package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaBootstrapUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.lib.upgrades.UpgradeIcon;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.bootstrap.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechBootstrap
{
    private LimaTechBootstrap() {}
    
    public static DatapackBuiltinEntriesProvider create(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> baseRegistries)
    {
        return LimaBootstrapUtil.createDataPackProvider(packOutput, baseRegistries, LimaTech.MODID, builder -> builder
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, LimaTechBiomeModifiers::bootstrap)
                .add(Registries.CONFIGURED_FEATURE, LimaTechConfiguredFeatures::bootstrap)
                .add(Registries.DAMAGE_TYPE, LimaTechDamageTypes::bootstrap)
                .add(Registries.ENCHANTMENT, LimaTechEnchantments::bootstrap)
                .add(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, LimaTechEquipmentUpgrades::bootstrap)
                .add(LimaTechRegistries.Keys.MACHINE_UPGRADES, LimaTechMachineUpgrades::bootstrap)
                .add(Registries.PLACED_FEATURE, LimaTechPlacedFeatures::bootstrap));
    }

    public static UpgradeIcon intrinsicTypeIcon(ItemLike item)
    {
        return itemWithSpriteOverlay(item, "generic", 10, 10, 0, 6);
    }

    public static UpgradeIcon hanabiCoreIcon(GrenadeType grenadeType)
    {
        return itemWithSpriteOverlay(LimaTechItems.GRENADE_LAUNCHER.get().createDefaultStack(null, true, grenadeType), grenadeType.getSerializedName() + "_grenade_core", 10, 10, 0, 6);
    }

    public static UpgradeIcon sprite(String spriteName)
    {
        return new UpgradeIcon.SpriteSheetIcon(RESOURCES.location(spriteName));
    }

    public static UpgradeIcon itemIcon(ItemStack stack)
    {
        return new UpgradeIcon.ItemStackIcon(stack);
    }

    public static UpgradeIcon itemIcon(ItemLike itemLike)
    {
        return itemIcon(new ItemStack(itemLike.asItem()));
    }

    public static UpgradeIcon itemWithSpriteOverlay(ItemStack stack, String spriteName, int width, int height, int xOffset, int yOffset)
    {
        return new UpgradeIcon.ItemStackWithSpriteIcon(stack, RESOURCES.location(spriteName), width, height, xOffset, yOffset);
    }

    public static UpgradeIcon itemWithSpriteOverlay(ItemLike itemLike, String spriteName, int width, int height, int xOffset, int yOffset)
    {
        return itemWithSpriteOverlay(new ItemStack(itemLike.asItem()), spriteName, width, height, xOffset, yOffset);
    }
}