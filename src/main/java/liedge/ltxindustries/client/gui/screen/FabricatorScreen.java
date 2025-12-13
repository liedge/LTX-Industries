package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.client.gui.widget.BaseScrollGridRenderable;
import liedge.ltxindustries.client.gui.widget.FabricatorProgressWidget;
import liedge.ltxindustries.client.gui.widget.ScrollbarWidget;
import liedge.ltxindustries.menu.FabricatorMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.client.LTXILangKeys.FABRICATOR_SELECTED_RECIPE_TOOLTIP;
import static liedge.ltxindustries.client.LTXILangKeys.INLINE_ENERGY_REQUIRED_TOOLTIP;

public class FabricatorScreen extends LTXIMachineScreen<FabricatorMenu>
{
    private static final ResourceLocation BLUEPRINT_SLOT_SPRITE = RESOURCES.location("slot/blank_blueprint");

    private final List<RecipeHolder<FabricatingRecipe>> recipes;

    private @Nullable SelectorGrid selectorGrid;
    private @Nullable ScrollbarWidget scrollbar;

    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 200);

        // Read recipes
        this.recipes = FabricatingRecipe.getSortedRecipes(inventory.player.level());

        // Screen setup
        this.inventoryLabelX = 14;
        this.inventoryLabelY = 107;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new FabricatorProgressWidget(leftPos + 61, topPos + 83, menu.menuContext()));
        this.selectorGrid = addRenderableOnly(new SelectorGrid(leftPos + 76, topPos + 32, this));
        this.scrollbar = addRenderableWidget(new ScrollbarWidget(leftPos + 168, topPos + 32, 72, selectorGrid));
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
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int x, int y)
    {
        if (menu.getCarried().isEmpty() && selectorGrid != null)
        {
            if (selectorGrid.renderTooltips(graphics, x, y)) return;
        }

        super.renderTooltip(graphics, x, y);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if (selectorGrid != null && scrollbar != null && selectorGrid.isMouseOver(mouseX, mouseY))
        {
            if (scrollbar.moveScrollBar(scrollY)) return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (menu.getCarried().isEmpty() && (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT || mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) && selectorGrid != null)
        {
            if (selectorGrid.onGridClicked(mouseX, mouseY, mouseButton)) return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private static class SelectorGrid extends BaseScrollGridRenderable.FixedElements<RecipeHolder<FabricatingRecipe>>
    {
        private final FabricatorScreen parent;
        private final BaseFabricatorBlockEntity blockEntity;

        private int selectedRecipe = -1;

        SelectorGrid(int x, int y, FabricatorScreen parent)
        {
            super(x, y, 18, 18, 5, 4, parent.recipes);
            this.parent = parent;
            this.blockEntity = parent.menu.menuContext();
        }

        @Override
        protected void onScrollRowChanged(int newScrollRow)
        {
            super.onScrollRowChanged(newScrollRow);
            this.selectedRecipe = -1;
        }

        @Override
        public void renderElement(GuiGraphics graphics, RecipeHolder<FabricatingRecipe> element, int posX, int posY, int gridIndex, int elementIndex, int mouseX, int mouseY)
        {
            FabricatingRecipe recipe = element.value();

            ResourceLocation sprite;
            if (blockEntity.isCrafting() && blockEntity.getRecipeCheck().getLastUsedRecipe(Minecraft.getInstance().level).map(r -> r.value() == recipe).orElse(false))
            {
                sprite = GRID_UNIT_SELECTED;
            }
            else if (gridIndex == selectedRecipe)
            {
                sprite = LayoutSlot.ITEM_SLOT_SPRITE;
            }
            else if (isMouseOverElement(mouseX, mouseY, posX, posY))
            {
                sprite = GRID_UNIT_FOCUSED;
            }
            else
            {
                sprite = GRID_UNIT;
            }
            graphics.blitSprite(sprite, posX, posY, 18, 18);

            ItemStack resultStack = recipe.getFabricatingResultItem();
            graphics.renderFakeItem(resultStack, posX + 1, posY + 1);
            graphics.renderItemDecorations(Minecraft.getInstance().font, resultStack, posX + 1, posY + 1);
        }

        @Override
        public void renderElementTooltip(GuiGraphics graphics, RecipeHolder<FabricatingRecipe> element, int mouseX, int mouseY, int gridIndex, int elementIndex)
        {
            FabricatingRecipe recipe = element.value();

            List<Component> lines = getTooltipFromItem(Minecraft.getInstance(), recipe.getFabricatingResultItem());

            if (gridIndex == selectedRecipe) lines.add(FABRICATOR_SELECTED_RECIPE_TOOLTIP.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()));

            lines.add(INLINE_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaEnergyUtil.toEnergyString(recipe.getEnergyRequired())).withStyle(LTXIConstants.REM_BLUE.chatStyle()));
            graphics.renderTooltip(Minecraft.getInstance().font, lines, Optional.of(recipe.createIngredientTooltip()), mouseX, mouseY);
        }

        @Override
        public void onElementClicked(RecipeHolder<FabricatingRecipe> element, double mouseX, double mouseY, int button, int gridIndex, int elementIndex)
        {
            // Left click handling
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));

                if (selectedRecipe != gridIndex)
                {
                    selectedRecipe = gridIndex;
                }
                else
                {
                    parent.sendCustomButtonData(FabricatorMenu.CRAFT_BUTTON_ID, element.id(), LimaCoreNetworkSerializers.RESOURCE_LOCATION);
                }
            }
            else if (selectedRecipe == gridIndex)
            {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                parent.sendCustomButtonData(FabricatorMenu.ENCODE_BLUEPRINT_BUTTON_ID, element.id(), LimaCoreNetworkSerializers.RESOURCE_LOCATION);
            }
        }
    }
}