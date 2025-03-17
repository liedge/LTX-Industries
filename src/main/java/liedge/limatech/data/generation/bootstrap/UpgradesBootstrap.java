package liedge.limatech.data.generation.bootstrap;

import liedge.limacore.data.generation.RegistryBootstrapExtensions;
import liedge.limatech.lib.upgrades.UpgradeBase;
import liedge.limatech.lib.upgrades.UpgradeIcon;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.LimaTechItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import static liedge.limatech.LimaTech.RESOURCES;

interface UpgradesBootstrap<T extends UpgradeBase<?, ?>> extends RegistryBootstrapExtensions<T>
{
    default UpgradeIcon intrinsicTypeIcon(ItemLike item)
    {
        return itemWithSpriteOverlay(item, "generic", 10, 10, 0, 6);
    }

    default UpgradeIcon hanabiCoreIcon(GrenadeType grenadeType)
    {
        return itemWithSpriteOverlay(LimaTechItems.GRENADE_LAUNCHER.get().createDefaultStack(null, true, grenadeType), grenadeType.getSerializedName() + "_grenade_core", 10, 10, 0, 6);
    }

    default UpgradeIcon sprite(String spriteName)
    {
        return new UpgradeIcon.SpriteSheetIcon(RESOURCES.location(spriteName));
    }

    default UpgradeIcon itemIcon(ItemStack stack)
    {
        return new UpgradeIcon.ItemStackIcon(stack);
    }

    default UpgradeIcon itemIcon(ItemLike itemLike)
    {
        return itemIcon(new ItemStack(itemLike.asItem()));
    }

    default UpgradeIcon itemWithSpriteOverlay(ItemStack stack, String spriteName, int width, int height, int xOffset, int yOffset)
    {
        return new UpgradeIcon.ItemStackWithSpriteIcon(stack, RESOURCES.location(spriteName), width, height, xOffset, yOffset);
    }

    default UpgradeIcon itemWithSpriteOverlay(ItemLike itemLike, String spriteName, int width, int height, int xOffset, int yOffset)
    {
        return itemWithSpriteOverlay(new ItemStack(itemLike.asItem()), spriteName, width, height, xOffset, yOffset);
    }
}