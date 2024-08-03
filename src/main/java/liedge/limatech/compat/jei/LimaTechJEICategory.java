package liedge.limatech.compat.jei;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public abstract class LimaTechJEICategory<R extends LimaCustomRecipe<?>> implements IRecipeCategory<R>
{
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;

    protected LimaTechJEICategory(IGuiHelper helper, LimaRecipeType<R> limaRecipeType, ResourceLocation backgroundTexture, int backgroundU, int backgroundV, int width, int height)
    {
        this.background = helper.createDrawable(backgroundTexture, backgroundU, backgroundV, width, height);
        this.icon = helper.createDrawableItemStack(new ItemStack(categoryIconItem()));
        this.title = limaRecipeType.translate();
    }

    protected abstract ItemLike categoryIconItem();

    protected final RegistryAccess localRegistryAccess()
    {
        return Objects.requireNonNull(Minecraft.getInstance().level, "Minecraft local level is null").registryAccess();
    }

    @Override
    public Component getTitle()
    {
        return title;
    }

    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public IDrawable getIcon()
    {
        return icon;
    }
}