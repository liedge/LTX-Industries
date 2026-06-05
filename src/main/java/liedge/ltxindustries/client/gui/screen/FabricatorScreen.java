package liedge.ltxindustries.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.LimaCoreClient;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.LimaEnergyUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.client.gui.widget.BaseScrollGridRenderable;
import liedge.ltxindustries.client.gui.widget.FabricatorProgressWidget;
import liedge.ltxindustries.client.gui.widget.ScrollbarWidget;
import liedge.ltxindustries.menu.FabricatorMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.tooltip.FabricatingInputsTooltip;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.client.LTXILangKeys.FABRICATOR_SELECTED_RECIPE_TOOLTIP;
import static liedge.ltxindustries.client.LTXILangKeys.INLINE_ENERGY_REQUIRED_TOOLTIP;

public class FabricatorScreen extends MachineBaseScreen<FabricatorMenu>
{
    private static final Identifier BLUEPRINT_SLOT_SPRITE = RESOURCES.id("slot/blank_blueprint");

    private final SearchTree<RecipeHolder<FabricatingRecipe>> searchTree;
    private final List<RecipeHolder<FabricatingRecipe>> recipes = new ObjectArrayList<>();

    private @Nullable SelectorGrid selectorGrid;
    private @Nullable ScrollbarWidget scrollbar;
    private @Nullable EditBox searchBox;

    public FabricatorScreen(FabricatorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 190, 200);

        // Read recipes
        Item.TooltipContext tooltipContext = Item.TooltipContext.of(inventory.player.level(), inventory.player);
        this.searchTree = new FullTextSearchTree<>(
                holder -> getTooltipLines(holder, tooltipContext),
                holder -> holder.value().getResultPreview().typeHolder().unwrapKey().stream().map(ResourceKey::identifier),
                LimaCoreClient.getClientRecipes().byType(LTXIRecipeTypes.FABRICATING).stream().sorted(FabricatingRecipe.GROUP_AND_NAME_COMPARATOR).toList());

        // Screen setup
        this.inventoryLabelX = 14;
    }

    private Stream<String> getTooltipLines(RecipeHolder<FabricatingRecipe> holder, Item.TooltipContext context)
    {
        ItemStack stack = holder.value().getResultPreview();
        return stack.getTooltipLines(context, null, TooltipFlag.NORMAL).stream()
                .map(txt -> ChatFormatting.stripFormatting(txt.getString()).trim().toLowerCase(Locale.ROOT));
    }

    private void refreshRecipes()
    {
        recipes.clear();

        String searchText = searchBox != null ? searchBox.getValue() : "";
        recipes.addAll(searchTree.search(searchText));

        if (selectorGrid != null) selectorGrid.reset();
        if (scrollbar != null) scrollbar.reset();
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new FabricatorProgressWidget(leftPos + 61, topPos + 83, menu.menuContext()));
        this.selectorGrid = addRenderableOnly(new SelectorGrid(leftPos + 76, topPos + 32, this));

        this.scrollbar = addRenderableWidget(new ScrollbarWidget(leftPos + 168, topPos + 32, 72, selectorGrid));
        this.searchBox = addRenderableWidget(new EditBox(font, leftPos + 75, topPos + 18, 102, 12, searchBox, Component.empty()));

        setInitialFocus(searchBox);
        refreshRecipes();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        // Background sprites
        blitInventoryAndHotbar(graphics, 14, 117);
        blitPowerInSlot(graphics, 7, 52);
        blitSlotSprite(graphics, BLUEPRINT_SLOT_SPRITE, 42, 60);
        blitOutputSlot(graphics, 39, 83);
        blitDarkPanel(graphics, 75, 31, 92, 74);
        blitLightPanel(graphics, 167, 31, 10, 74);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int x, int y)
    {
        if (menu.getCarried().isEmpty() && selectorGrid != null)
        {
            if (selectorGrid.renderTooltips(graphics, x, y)) return;
        }

        super.extractTooltip(graphics, x, y);
    }

    @Override
    public boolean charTyped(CharacterEvent event)
    {
        if (searchBox != null)
        {
            String oldContents = searchBox.getValue();
            if (searchBox.charTyped(event))
            {
                if (!Objects.equals(oldContents, searchBox.getValue())) refreshRecipes();

                return true;
            }
        }

        return super.charTyped(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        if (searchBox != null && event.key() != InputConstants.KEY_TAB)
        {
            String oldContents = searchBox.getValue();
            if (searchBox.keyPressed(event))
            {
                if (!Objects.equals(oldContents, searchBox.getValue())) refreshRecipes();

                return true;
            }

            if (searchBox.canConsumeInput() && !event.isEscape()) return true;
        }
        return super.keyPressed(event);
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
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick)
    {
        if (menu.getCarried().isEmpty() && (event.button() == InputConstants.MOUSE_BUTTON_LEFT || event.button() == InputConstants.MOUSE_BUTTON_RIGHT) && selectorGrid != null)
        {
            if (selectorGrid.onGridClicked(event.x(), event.y(), event.button())) return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    private static class SelectorGrid extends BaseScrollGridRenderable<RecipeHolder<FabricatingRecipe>>
    {
        private final FabricatorScreen parent;
        private final BaseFabricatorBlockEntity blockEntity;

        private int selectedRecipe = -1;

        SelectorGrid(int x, int y, FabricatorScreen parent)
        {
            super(x, y, 18, 18, 5, 4);
            this.parent = parent;
            this.blockEntity = parent.menu.menuContext();
        }

        @Override
        protected void onScrollRowChanged(int newScrollRow)
        {
            super.onScrollRowChanged(newScrollRow);
            this.selectedRecipe = -1;
        }

        private boolean gridRecipeMatches(FabricatingRecipe gridRecipe)
        {
            ResourceKey<Recipe<?>> key = blockEntity.getRecipeCheck().getLastUsedRecipeKey();
            RecipeHolder<FabricatingRecipe> holder = LimaCoreClient.getClientRecipes().byKey(LTXIRecipeTypes.FABRICATING, key);

            return holder != null && holder.value() == gridRecipe;
        }

        @Override
        public List<RecipeHolder<FabricatingRecipe>> getElements()
        {
            return parent.recipes;
        }

        @Override
        public void renderElement(GuiGraphicsExtractor graphics, RecipeHolder<FabricatingRecipe> element, int posX, int posY, int gridIndex, int elementIndex, int mouseX, int mouseY)
        {
            FabricatingRecipe recipe = element.value();

            Identifier sprite;

            if (blockEntity.isCrafting() && gridRecipeMatches(recipe))
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
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, posX, posY, 18, 18);

            ItemStack resultStack = recipe.getResultPreview();
            graphics.fakeItem(resultStack, posX + 1, posY + 1);
            graphics.itemDecorations(Minecraft.getInstance().font, resultStack, posX + 1, posY + 1);
        }

        @Override
        public void renderElementTooltip(GuiGraphicsExtractor graphics, RecipeHolder<FabricatingRecipe> element, int mouseX, int mouseY, int gridIndex, int elementIndex)
        {
            FabricatingRecipe recipe = element.value();

            List<Component> lines = getTooltipFromItem(Minecraft.getInstance(), recipe.getResultPreview());

            if (gridIndex == selectedRecipe) lines.add(FABRICATOR_SELECTED_RECIPE_TOOLTIP.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()));

            lines.add(INLINE_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaEnergyUtil.toEnergyString(recipe.getEnergyRequired())).withStyle(LTXIConstants.REM_BLUE.chatStyle()));
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, lines, Optional.of(new FabricatingInputsTooltip(element.id())), ItemStack.EMPTY, mouseX, mouseY);
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
                    parent.sendCustomButtonData(FabricatorMenu.CRAFT_BUTTON_ID, element.id(), LimaCoreNetworkSerializers.RECIPE_KEY);
                }
            }
            else if (selectedRecipe == gridIndex)
            {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                parent.sendCustomButtonData(FabricatorMenu.ENCODE_BLUEPRINT_BUTTON_ID, element.id(), LimaCoreNetworkSerializers.RECIPE_KEY);
            }
        }
    }
}