package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXIClientRecipes;
import liedge.ltxindustries.client.gui.widget.*;
import liedge.ltxindustries.menu.FabricatorMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

import static liedge.limacore.client.gui.LimaGuiUtil.isMouseWithinArea;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.client.LTXILangKeys.FABRICATOR_SELECTED_RECIPE_TOOLTIP;
import static liedge.ltxindustries.client.LTXILangKeys.INLINE_ENERGY_REQUIRED_TOOLTIP;

public class FabricatorScreen extends LTXIMachineScreen<FabricatorMenu> implements ScrollableGUIElement
{
    private static final int SELECTOR_GRID_WIDTH = 5;
    private static final int SELECTOR_GRID_HEIGHT = 4;
    private static final int SELECTOR_GRID_SIZE = SELECTOR_GRID_WIDTH * SELECTOR_GRID_HEIGHT;

    private static final ResourceLocation BLUEPRINT_SLOT_SPRITE = RESOURCES.location("slot/blank_blueprint");
    private static final ResourceLocation SELECTOR_SPRITE = RESOURCES.location("widget/fabricator_selector");
    private static final ResourceLocation SELECTOR_FOCUSED_SPRITE = RESOURCES.location("widget/fabricator_selector_focus");
    private static final ResourceLocation SELECTOR_ACTIVE_SPRITE = RESOURCES.location("widget/fabricator_selector_active");

    private final int recipeRows;
    private final int scrollWheelDelta;
    private final List<RecipeHolder<FabricatingRecipe>> recipes;

    private int currentScrollRow;
    private int selectedRecipeIndex = -1;

    private @Nullable ScrollbarWidget scrollbar;

    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 200);

        // Read recipes and validate against recipe book
        this.recipes = LTXIClientRecipes.getUnlockedFabricatingRecipes((LocalPlayer) inventory.player);

        // Screen setup
        this.recipeRows = LimaCollectionsUtil.splitCollectionToSegments(recipes, SELECTOR_GRID_WIDTH);
        this.scrollWheelDelta = 59 / recipeRows;
        this.inventoryLabelX = 14;
        this.inventoryLabelY = 107;
    }

    private int selectorLeft()
    {
        return leftPos + 76;
    }

    private int selectorTop()
    {
        return topPos + 32;
    }

    private boolean isMouseOverSelector(double mouseX, double mouseY)
    {
        return isMouseWithinArea(mouseX, mouseY, selectorLeft(), selectorTop(), 90, 72);
    }

    @Override
    public boolean canScroll()
    {
        return recipeRows > 4;
    }

    @Override
    public void scrollUpdated(int scrollPosition)
    {
        int newScrollRow = Math.min(scrollPosition / scrollWheelDelta, recipeRows - 1);
        if (newScrollRow != currentScrollRow)
        {
            currentScrollRow = newScrollRow;
            selectedRecipeIndex = -1;
        }
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new FabricatorProgressWidget(leftPos + 61, topPos + 83, menu.menuContext()));
        this.scrollbar = addRenderableWidget(new ScrollbarWidget(leftPos + 168, topPos + 32, 72, this));
        scrollbar.reset(); // Always reset scrollbar after reinitializing
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTick, mouseX, mouseY);

        // Background sprites
        blitInventoryAndHotbar(graphics, 14, 117);
        blitPowerInSlot(graphics, 7, 52);
        blitSlotSprite(graphics, BLUEPRINT_SLOT_SPRITE, 42, 60);
        blitOutputSlot(graphics, 39, 83);
        blitDarkPanel(graphics, 75, 31, 92, 74);
        blitLightPanel(graphics, 167, 31, 10, 74);

        // Render recipe selector grid
        int min = currentScrollRow * SELECTOR_GRID_WIDTH;
        int max = Math.min(min + SELECTOR_GRID_SIZE, recipes.size());

        for (int i = min; i < max; i++)
        {
            int gridIndex = i - min;
            int rx = selectorLeft() + (gridIndex % SELECTOR_GRID_WIDTH) * 18;
            int ry = selectorTop() + (gridIndex / SELECTOR_GRID_WIDTH) * 18;

            FabricatingRecipe gridRecipe = recipes.get(i).value();

            ResourceLocation sprite;
            if (menu.menuContext().isCrafting() && menu.menuContext().getRecipeCheck().getLastUsedRecipe(Minecraft.getInstance().level).map(r -> gridRecipe == r.value()).orElse(false))
            {
                sprite = SELECTOR_ACTIVE_SPRITE;
            }
            else if (gridIndex == selectedRecipeIndex)
            {
                sprite = LayoutSlot.ITEM_SLOT_SPRITE;
            }
            else if (isMouseWithinArea(mouseX, mouseY, rx, ry, 18, 18))
            {
                sprite = SELECTOR_FOCUSED_SPRITE;
            }
            else
            {
                sprite = SELECTOR_SPRITE;
            }
            graphics.blitSprite(sprite, rx, ry, 18, 18);

            ItemStack resultStack = gridRecipe.getFabricatingResultItem();
            graphics.renderFakeItem(resultStack, rx + 1, ry + 1);
            graphics.renderItemDecorations(font, resultStack, rx + 1, ry + 1);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y)
    {
        if (menu.getCarried().isEmpty() && isMouseOverSelector(x, y))
        {
            int min = currentScrollRow * SELECTOR_GRID_WIDTH;
            int max = Math.min(min + SELECTOR_GRID_SIZE, recipes.size());

            for (int i = min; i < max; i++)
            {
                int gridIndex = i - min;
                int rx = selectorLeft() + (gridIndex % SELECTOR_GRID_WIDTH) * 18;
                int ry = selectorTop() + (gridIndex / SELECTOR_GRID_WIDTH) * 18;

                if (isMouseWithinArea(x, y, rx, ry, 18, 18))
                {
                    RecipeHolder<FabricatingRecipe> holder = recipes.get(i);
                    List<Component> lines = getTooltipFromItem(Minecraft.getInstance(), holder.value().getFabricatingResultItem());

                    if (gridIndex == selectedRecipeIndex) lines.add(FABRICATOR_SELECTED_RECIPE_TOOLTIP.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()));

                    lines.add(INLINE_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaEnergyUtil.toEnergyString(holder.value().getEnergyRequired())).withStyle(LTXIConstants.REM_BLUE.chatStyle()));
                    graphics.renderTooltip(font, lines, Optional.of(holder.value().createIngredientTooltip()), x, y);

                    return;
                }
            }
        }
        else
        {
            super.renderTooltip(graphics, x, y);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if (isMouseOverSelector(mouseX, mouseY) && scrollbar != null)
        {
            int delta = scrollWheelDelta * (int) -Math.signum(scrollY);
            scrollbar.moveScrollbar(delta);
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (isMouseOverSelector(mouseX, mouseY) && menu.getCarried().isEmpty() && (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT || mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT))
        {
            int min = currentScrollRow * SELECTOR_GRID_WIDTH;
            int max = Math.min(min + SELECTOR_GRID_SIZE, recipes.size());

            for (int i = min; i < max; i++)
            {
                int gridIndex = i - min;
                int rx = selectorLeft() + (gridIndex % SELECTOR_GRID_WIDTH) * 18;
                int ry = selectorTop() + (gridIndex / SELECTOR_GRID_WIDTH) * 18;

                if (isMouseWithinArea(mouseX, mouseY, rx, ry, 18, 18))
                {
                    // Left click handling
                    if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT)
                    {
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                        if (selectedRecipeIndex != gridIndex)
                        {
                            selectedRecipeIndex = gridIndex;
                        }
                        else
                        {
                            sendCustomButtonData(FabricatorMenu.CRAFT_BUTTON_ID, recipes.get(i).id(), LimaCoreNetworkSerializers.RESOURCE_LOCATION);
                        }
                    }
                    else if (selectedRecipeIndex == gridIndex) // Right click handling
                    {
                        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                        sendCustomButtonData(FabricatorMenu.ENCODE_BLUEPRINT_BUTTON_ID, recipes.get(i).id(), LimaCoreNetworkSerializers.RESOURCE_LOCATION);
                    }

                    return true;
                }
            }

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}