package liedge.ltxindustries.menu;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.network.sync.SimpleValueTracker;
import liedge.limacore.network.sync.ValueTracker;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.List;

public class RecipeModeMenu extends BlockEntityMenu<RecipeModeHolderBlockEntity>
{
    public static final int MODE_SWITCH_BUTTON_ID = 1;

    private final List<Holder<RecipeMode>> remoteModes = new ObjectArrayList<>();
    private boolean screenUpdate;

    public RecipeModeMenu(MenuType<?> type, int containerId, Inventory inventory, RecipeModeHolderBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);
        addDefaultPlayerInventoryAndHotbar();

        addDataWatcher(menuContext.keepRecipeModeSynced());

        ValueTracker<HolderSet<RecipeMode>> tracker = SimpleValueTracker.create(LTXINetworkSerializers.RECIPE_MODES, menuContext::getAvailableRecipeModes, holders -> {
            this.remoteModes.clear();
            holders.forEach(this.remoteModes::add);
            screenUpdate = true;
        });
        addDataWatcher(tracker);

        handleUnitButton(SharedMenuButtons.EXIT_SUB_MENU, menuContext::returnToPrimaryMenuScreen);
        handleButton(MODE_SWITCH_BUTTON_ID, LTXINetworkSerializers.RECIPE_MODE, (_, optional) -> {
            Holder<RecipeMode> mode = optional.orElse(null);
            if (mode == null || menuContext.getAvailableRecipeModes().contains(mode)) menuContext.setMode(mode);
        });
    }

    public List<Holder<RecipeMode>> getRemoteModes()
    {
        return remoteModes;
    }

    public boolean shouldUpdateScreen()
    {
        if (screenUpdate)
        {
            this.screenUpdate = false;
            return true;
        }

        return false;
    }
}