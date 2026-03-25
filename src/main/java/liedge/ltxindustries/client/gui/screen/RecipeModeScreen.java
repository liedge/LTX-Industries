package liedge.ltxindustries.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.widget.BaseGridRenderable;
import liedge.ltxindustries.client.gui.widget.SubMenuBackButton;
import liedge.ltxindustries.menu.RecipeModeMenu;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RecipeModeScreen extends LTXIScreen<RecipeModeMenu>
{
    private final List<SelectorOption> options;
    private @Nullable SelectorGrid selectorGrid;

    public RecipeModeScreen(RecipeModeMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT, 18, 0, 0);

        // Options init
        this.options = new ObjectArrayList<>();
        options.add(new SelectorOption(Items.BARRIER.getDefaultInstance(), null));

        final Holder<RecipeType<?>> machineRecipeType = menu.menuContext().getRecipeTypeHolder();

        IdMap<Holder<RecipeMode>> modeRegistry = inventory.player.level().registryAccess().lookupOrThrow(LTXIRegistries.Keys.RECIPE_MODES).asHolderIdMap();
        for (Holder<RecipeMode> mode : modeRegistry)
        {
            if (options.size() <= 23)
            {
                if (mode.value().recipeTypes().contains(machineRecipeType))
                {
                    options.add(new SelectorOption(mode.value().displayItem().create(), mode));
                }
            }
            else
            {
                break;
            }
        }
    }

    @Override
    protected void addWidgets()
    {
        addRenderableWidget(new SubMenuBackButton(leftPos - leftPadding, topPos + 3, this, RecipeModeMenu.BACK_BUTTON_ID));
        this.selectorGrid = addRenderableOnly(new SelectorGrid(leftPos + 15, topPos + 12, this));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitInventoryAndHotbar(graphics, 7, 83);
        blitDarkPanel(graphics, 14, 11, 146, 56);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick)
    {
        if (menu.getCarried().isEmpty() && event.button() == InputConstants.MOUSE_BUTTON_LEFT && selectorGrid != null)
        {
            if (selectorGrid.onGridClicked(event.x(), event.y(), 0)) return true;
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int x, int y)
    {
        if (menu.getCarried().isEmpty() && selectorGrid != null)
        {
            selectorGrid.renderTooltips(graphics, x, y);
        }

        super.extractTooltip(graphics, x, y);
    }

    private record SelectorOption(ItemStack stack, @Nullable Holder<RecipeMode> mode) {}

    private static class SelectorGrid extends BaseGridRenderable.FixedElements<SelectorOption>
    {
        private final RecipeModeScreen parent;
        private final RecipeModeHolderBlockEntity blockEntity;

        SelectorGrid(int x, int y, RecipeModeScreen parent)
        {
            super(x, y, 18, 18, 8, 3, parent.options);
            this.parent = parent;
            this.blockEntity = parent.menu.menuContext();
        }

        @Override
        public void renderElement(GuiGraphicsExtractor graphics, SelectorOption element, int posX, int posY, int gridIndex, int elementIndex, int mouseX, int mouseY)
        {
            Identifier sprite;
            if (blockEntity.getMode() == element.mode)
            {
                sprite = GRID_UNIT_SELECTED;
            }
            else if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, posX, posY, 18, 18))
            {
                sprite = GRID_UNIT_FOCUSED;
            }
            else
            {
                sprite = GRID_UNIT;
            }
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, posX, posY, 18, 18);
            graphics.fakeItem(element.stack, posX + 1, posY + 1);
        }

        @Override
        public void renderElementTooltip(GuiGraphicsExtractor graphics, SelectorOption element, int mouseX, int mouseY, int gridIndex, int elementIndex)
        {
            Holder<RecipeMode> mode = element.mode;
            Component tooltip = mode != null ? mode.value().displayName() : LTXILangKeys.NONE_UNIVERSAL_TOOLTIP.translate();
            graphics.setTooltipForNextFrame(tooltip, mouseX, mouseY);
        }

        @Override
        public void onElementClicked(SelectorOption element, double mouseX, double mouseY, int button, int gridIndex, int elementIndex)
        {
            if (blockEntity.getMode() != element.mode)
            {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                parent.sendCustomButtonData(RecipeModeMenu.MODE_SWITCH_BUTTON_ID, Optional.ofNullable(element.mode), LTXINetworkSerializers.RECIPE_MODE);
            }
        }
    }
}