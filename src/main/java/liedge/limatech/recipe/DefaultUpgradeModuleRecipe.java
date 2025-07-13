package liedge.limatech.recipe;

import liedge.limatech.item.EquipmentUpgradeModuleItem;
import liedge.limatech.item.UpgradableEquipmentItem;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.registry.game.LimaTechRecipeSerializers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class DefaultUpgradeModuleRecipe extends CustomRecipe
{
    public DefaultUpgradeModuleRecipe(CraftingBookCategory category)
    {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level)
    {
        boolean foundEquipment = false;
        boolean foundBlankModule = false;

        for (int i = 0; i < input.size(); i++)
        {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
                {
                    Registry<EquipmentUpgrade> registry = level.registryAccess().registryOrThrow(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES);
                    ResourceKey<EquipmentUpgrade> defaultKey = equipmentItem.getDefaultUpgradeKey();

                    if (registry.containsKey(defaultKey))
                    {
                        if (!foundEquipment)
                            foundEquipment = true;
                        else return false;
                    }
                }
                else if (stack.is(LimaTechItems.EMPTY_UPGRADE_MODULE))
                {
                    if (!foundBlankModule)
                        foundBlankModule = true;
                    else return false;
                }
                else
                {
                    return false;
                }
            }
        }

        return foundEquipment && foundBlankModule;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        Holder<EquipmentUpgrade> defaultUpgrade = null;
        boolean foundEmptyModule = false;

        for (int i = 0; i < input.size(); i++)
        {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
                {
                    ResourceKey<EquipmentUpgrade> defaultKey = equipmentItem.getDefaultUpgradeKey();
                    Optional<Holder.Reference<EquipmentUpgrade>> holder = registries.holder(defaultKey);

                    if (holder.isPresent())
                    {
                        if (defaultUpgrade == null) defaultUpgrade = holder.get();
                        else return ItemStack.EMPTY;
                    }
                }
                else if (stack.is(LimaTechItems.EMPTY_UPGRADE_MODULE))
                {
                    if (!foundEmptyModule) foundEmptyModule = true;
                    else return ItemStack.EMPTY;
                }
                else
                {
                    return ItemStack.EMPTY;
                }
            }
        }

        return (foundEmptyModule && defaultUpgrade != null) ? EquipmentUpgradeModuleItem.createStack(defaultUpgrade, 1) : ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height)
    {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechRecipeSerializers.DEFAULT_UPGRADE_MODULE.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input)
    {
        NonNullList<ItemStack> remainder = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < input.size(); i++)
        {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof UpgradableEquipmentItem) remainder.set(i, stack.copy());
        }

        return remainder;
    }
}