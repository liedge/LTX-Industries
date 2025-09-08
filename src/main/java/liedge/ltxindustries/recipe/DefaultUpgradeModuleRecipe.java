package liedge.ltxindustries.recipe;

import liedge.ltxindustries.item.EquipmentUpgradeModuleItem;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
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
                    // Always check for duplicates
                    if (foundEquipment) return true;

                    Registry<EquipmentUpgrade> registry = level.registryAccess().registryOrThrow(LTXIRegistries.Keys.EQUIPMENT_UPGRADES);
                    ResourceKey<EquipmentUpgrade> defaultKey = equipmentItem.getDefaultUpgradeKey();

                    if (defaultKey == null || !registry.containsKey(defaultKey))
                        return false;

                    foundEquipment = true;
                }
                else if (stack.is(LTXIItems.EMPTY_UPGRADE_MODULE))
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
                    Optional<Holder<EquipmentUpgrade>> holder = Optional.ofNullable(equipmentItem.getDefaultUpgradeKey()).flatMap(registries::holder);

                    if (holder.isPresent())
                    {
                        if (defaultUpgrade == null) defaultUpgrade = holder.get();
                        else return ItemStack.EMPTY;
                    }
                }
                else if (stack.is(LTXIItems.EMPTY_UPGRADE_MODULE))
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
        return LTXIRecipeSerializers.DEFAULT_UPGRADE_MODULE.get();
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