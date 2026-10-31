package liedge.ltxindustries.integration.jei;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.item.UpgradeModuleItem;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.upgrades.UpgradeUsers;
import liedge.ltxindustries.registry.LTXIRegistries;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawablesView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.Objects;

final class UpgradeUsersCategory extends AbstractRecipeCategory<UpgradeUsersCategory.Entry>
{
    static List<Entry> getAllRecipes()
    {
        Level level = Objects.requireNonNull(Minecraft.getInstance().level);

        IdMap<Holder<Upgrade>> registry = level.registryAccess().lookupOrThrow(LTXIRegistries.Keys.UPGRADES).asHolderIdMap();

        Multimap<Holder<?>, Holder<Upgrade>> userMap = MultimapBuilder.hashKeys().arrayListValues().build();

        for (Holder<Upgrade> holder : registry)
        {
            UpgradeUsers users = holder.value().users();

            users.items().forEach(key -> userMap.put(key, holder));
            users.blockEntities().forEach(key -> userMap.put(key, holder));
        }

        List<Entry> recipes = new ObjectArrayList<>();

        for (Holder<?> key : userMap.keySet())
        {
            List<Holder<Upgrade>> upgrades = List.copyOf(userMap.get(key));

            if (key.value() instanceof Item itemKey)
            {
                recipes.add(new Entry(itemKey.getDefaultInstance(), upgrades));
            }
            else if (key.value() instanceof BlockEntityType<?> beKey)
            {
                beKey.getValidBlocks().stream().map(b -> b.asItem().getDefaultInstance()).forEach(icon -> recipes.add(new Entry(icon, upgrades)));
            }
        }

        return recipes;
    }

    UpgradeUsersCategory(IRecipeType<Entry> recipeType, IDrawable icon)
    {
        super(recipeType, LTXILangKeys.GUI_UPGRADES.translate(), icon, 142, 110);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Entry recipe, IFocusGroup focuses)
    {
        builder.addInputSlot().add(recipe.item).setStandardSlotBackground();

        for (Holder<Upgrade> upgrade : recipe.upgrades)
        {
            builder.addOutputSlot().add(UpgradeModuleItem.get(upgrade, upgrade.value().maxRank()));
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, Entry recipe, IFocusGroup focuses)
    {
        Component name = recipe.item.getItemName();
        builder.addText(name, getWidth() - 22, 20)
                .setPosition(22, 0)
                .setColor(0xff505050)
                .setLineSpacing(0)
                .setTextAlignment(VerticalAlignment.CENTER)
                .setTextAlignment(HorizontalAlignment.CENTER);

        IRecipeSlotDrawablesView slots = builder.getRecipeSlots();
        List<IRecipeSlotDrawable> outputSlots = slots.getSlots(RecipeIngredientRole.OUTPUT);

        IScrollGridWidget scrollGrid = builder.addScrollGridWidget(outputSlots, 7, 5);
        scrollGrid.setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);

        IRecipeSlotDrawable inputSlot = slots.getSlots(RecipeIngredientRole.INPUT).getFirst();
        inputSlot.setPosition(scrollGrid.getScreenRectangle().position().x() + 1, 1);
    }

    @Override
    public Identifier getIdentifier(Entry recipe)
    {
        return LimaRegistryUtil.getItemId(recipe.item.getItem());
    }

    record Entry(ItemStack item, List<Holder<Upgrade>> upgrades)
    { }
}