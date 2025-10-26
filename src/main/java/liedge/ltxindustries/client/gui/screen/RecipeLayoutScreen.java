package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.menu.LimaMenu;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.widget.LimaSidebarButton;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.menu.RecipeLayoutMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class RecipeLayoutScreen extends LTXIMachineScreen<RecipeLayoutMenu<?>>
{
    public static final ResourceLocation MODE_OVERLAY_SPRITE = LTXIndustries.RESOURCES.location("widget/recipe_modes");

    private final RecipeLayout layout;
    @Nullable
    private final RecipeModeHolderBlockEntity modeHolder;

    public RecipeLayoutScreen(RecipeLayoutMenu<?> menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.layout = menu.getLayout();

        if (menu.menuContext() instanceof RecipeModeHolderBlockEntity blockEntity)
        {
            RecipeType<?> recipeType = blockEntity.getRecipeTypeForMode();
            boolean check = inventory.player.level().registryAccess().registry(LTXIRegistries.Keys.RECIPE_MODES).stream()
                    .flatMap(Registry::holders)
                    .anyMatch(holder -> holder.value().recipeType().equals(recipeType));
            this.modeHolder = check ? blockEntity : null;
        }
        else
        {
            this.modeHolder = null;
        }
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + layout.progressBarX(), topPos + layout.progressBarY()));

        if (modeHolder != null)
        {
            addRenderableWidget(new RecipeModeButton(leftPos - leftPadding, bottomPos - 43, this, modeHolder));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY)
    {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        blitInventoryAndHotbar(guiGraphics, LimaMenu.DEFAULT_INV_X - 1, LimaMenu.DEFAULT_INV_Y - 1);
        blitPowerInSlot(guiGraphics, 7, 52);

        renderLayout(guiGraphics, leftPos, topPos, layout);
    }

    public static void renderLayout(GuiGraphics graphics, int screenX, int screenY, RecipeLayout layout)
    {
        for (LayoutSlot.Type slotType : LayoutSlot.Type.values())
        {
            if (slotType.getContentsType() == null) continue;
            List<LayoutSlot> layoutSlots = layout.getSlotsForType(slotType);

            for (LayoutSlot slot : layoutSlots)
            {
                int sx = screenX + slot.x() - 1;
                int sy = screenY + slot.y() - 1;
                graphics.blitSprite(slot.type().getSprite(), sx, sy, 18, 18);
            }
        }
    }

    private static class RecipeModeButton extends LimaSidebarButton.LeftSided
    {
        private final RecipeLayoutScreen parent;
        private final RecipeModeHolderBlockEntity blockEntity;

        public RecipeModeButton(int x, int y, RecipeLayoutScreen parent, RecipeModeHolderBlockEntity blockEntity)
        {
            super(x, y, Component.empty());
            this.parent = parent;
            this.blockEntity = blockEntity;
        }

        @Override
        protected void renderContents(GuiGraphics graphics, int guiX, int guiY)
        {
            Holder<RecipeMode> mode = blockEntity.getMode();
            if (mode == null)
            {
                renderSprite(graphics, MODE_OVERLAY_SPRITE, guiX, guiY);
            }
            else
            {
                graphics.renderFakeItem(mode.value().displayItem(), guiX, guiY);
            }
        }

        @Override
        public void onPress(int button)
        {
            parent.sendUnitButtonData(RecipeLayoutMenu.MODES_OPEN_BUTTON_ID);
        }

        @Override
        public boolean hasTooltip()
        {
            return true;
        }

        @Override
        public void createWidgetTooltip(TooltipLineConsumer consumer)
        {
            consumer.accept(LTXILangKeys.RECIPE_MODES_TITLE_OR_TOOLTIP.translate());

            Holder<RecipeMode> mode = blockEntity.getMode();
            Component modeComponent = mode != null ? mode.value().displayName() : LTXILangKeys.NONE_UNIVERSAL_TOOLTIP.translate().withStyle(ChatFormatting.GRAY);
            consumer.accept(LTXILangKeys.RECIPE_MODE_CURRENT_MODE.translateArgs(modeComponent));
        }
    }
}