package liedge.limatech.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import liedge.limatech.client.gui.BlueprintToast;
import liedge.limatech.recipe.FabricatingRecipe;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeToast.class)
public abstract class RecipeToastMixin
{
    @WrapMethod(method = "addOrUpdate")
    private static void interceptRecipeToast(ToastComponent toastComponent, RecipeHolder<?> recipe, Operation<Void> original)
    {
        if (recipe.value() instanceof FabricatingRecipe fabricatingRecipe)
        {
            toastComponent.addToast(new BlueprintToast(fabricatingRecipe));
        }
        else
        {
            original.call(toastComponent, recipe);
        }
    }
}