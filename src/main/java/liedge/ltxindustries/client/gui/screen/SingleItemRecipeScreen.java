package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.SimpleRecipeMachineBlockEntity;
import liedge.ltxindustries.client.gui.widget.EnergyGaugeWidget;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.SidedUpgradableMachineMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.function.Supplier;

public class SingleItemRecipeScreen<CTX extends SimpleRecipeMachineBlockEntity<?, ?>, M extends SidedUpgradableMachineMenu<CTX>> extends SidedUpgradableMachineScreen<M>
{
    public static final ResourceLocation SCREEN_TEXTURE = LTXIndustries.RESOURCES.textureLocation("gui", "single_input_machine");

    @SuppressWarnings("RedundantTypeArguments")
    public static <CTX extends SimpleRecipeMachineBlockEntity<?, ?>, M extends SidedUpgradableMachineMenu<CTX>> void registerScreen(RegisterMenuScreensEvent event, Supplier<? extends MenuType<M>> menuTypeSupplier, RecipeScreenType recipeScreenType)
    {
        event.<M, SingleItemRecipeScreen<CTX, M>>register(menuTypeSupplier.get(), (menu, inventory, title) -> new SingleItemRecipeScreen<>(menu, inventory, title, recipeScreenType));
    }

    private final RecipeScreenType recipeScreenType;

    private SingleItemRecipeScreen(M menu, Inventory inventory, Component title, RecipeScreenType recipeScreenType)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.alignInventoryLabelRight = true;
        this.recipeScreenType = recipeScreenType;
    }

    public RecipeScreenType getRecipeScreenType()
    {
        return recipeScreenType;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new EnergyGaugeWidget(menu.menuContext().getEnergyStorage(), leftPos + 11, topPos + 10));
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + 75, topPos + 39));
    }

    @Override
    public ResourceLocation getBgTexture()
    {
        return SCREEN_TEXTURE;
    }
}