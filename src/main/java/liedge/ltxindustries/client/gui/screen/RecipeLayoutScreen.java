package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.menu.slot.FluidResourceSlot;
import liedge.limacore.menu.slot.LimaFluidSlot;
import liedge.limacore.menu.slot.LimaItemSlot;
import liedge.limacore.util.LimaCoreObjects;
import liedge.limacore.util.LimaStreamsUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.widget.LTXISidebarButton;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.client.gui.widget.OpenRecipeModesButton;
import liedge.ltxindustries.menu.RecipeLayoutMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public final class RecipeLayoutScreen extends MachineBaseScreen<RecipeLayoutMenu<?>>
{
    private static final Identifier UNLOCKED_SPRITE = LTXIndustries.RESOURCES.id("widget/slots_unlocked");
    private static final Identifier LOCK_SPRITE = LTXIndustries.RESOURCES.id("widget/slots_locked");
    private static final int LOCKED_OVERLAY = ARGB.color(64, LTXIConstants.HOSTILE_ORANGE);

    private final RecipeLayout layout;
    private final @Nullable RecipeModeHolderBlockEntity modeHolder;
    private final Set<Slot> inputSlots;
    private final Set<LimaFluidSlot> inputFluidSlots;

    private final ItemStack barrierStack = new ItemStack(Items.BARRIER);

    public RecipeLayoutScreen(RecipeLayoutMenu<?> menu, Inventory inventory, Component title)
    {
        this(menu, menu.getLayout(), inventory, title);
    }

    private RecipeLayoutScreen(RecipeLayoutMenu<?> menu, RecipeLayout layout, Inventory inventory, Component title)
    {
        super(menu, inventory, title, layout.getWidth(), layout.getHeight());
        this.layout = layout;
        this.modeHolder = LimaCoreObjects.tryCast(RecipeModeHolderBlockEntity.class, menu.getMenuContext());

        ResourceHandler<?> items = menu.getMenuContext().getItems(BlockContentsType.INPUT);
        this.inputSlots = menu.slots.stream()
                .filter(s -> s instanceof LimaItemSlot lis && lis.getResourceHandler() == items)
                .collect(LimaStreamsUtil.toUnmodifiableObjectSet());

        ResourceHandler<?> fluids = menu.getMenuContext().getFluids(BlockContentsType.INPUT);
        this.inputFluidSlots = menu.getFluidSlots().stream()
                .filter(s -> s instanceof FluidResourceSlot frs && frs.getResourceHandler() == fluids)
                .collect(LimaStreamsUtil.toUnmodifiableObjectSet());

    }

    public Set<Slot> getInputSlots()
    {
        return inputSlots;
    }

    public Set<LimaFluidSlot> getInputFluidSlots()
    {
        return inputFluidSlots;
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();
        addRenderableOnly(new MachineProgressWidget(menu.getMenuContext(), leftPos + layout.getProgressBarX(), topPos + layout.getProgressBarY()));

        int lsy = bottomPos - 3 - (LTXISidebarButton.SIDEBAR_BUTTON_HEIGHT * 2);
        if (modeHolder != null)
        {
            addRenderableWidget(new OpenRecipeModesButton(leftPos - leftPadding, lsy, this, modeHolder));
            lsy -= LTXISidebarButton.SIDEBAR_BUTTON_HEIGHT;
        }

        if (!inputSlots.isEmpty() || !inputFluidSlots.isEmpty()) addRenderableWidget(new LockSlotsButton(leftPos - leftPadding, lsy, this));
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY)
    {
        super.extractSlot(graphics, slot, mouseX, mouseY);

        NonNullList<ItemResource> filter = menu.getMenuContext().getItemFilters();
        if (filter == null) return;

        if (inputSlots.contains(slot) && !quickCraftSlots.contains(slot))
        {
            ItemStack stack = slot.getItem();
            if (stack.isEmpty())
            {
                ItemResource resource = filter.get(slot.getSlotIndex());
                ItemStack renderStack = resource.isEmpty() ? barrierStack : resource.toStack();

                int x = slot.x;
                int y = slot.y;

                graphics.fakeItem(renderStack, x, y);
                graphics.fill(x, y, x + 16, y + 16, LOCKED_OVERLAY);
            }
        }
    }

    @Override
    protected void extractFluidSlot(GuiGraphicsExtractor graphics, LimaFluidSlot slot, int mouseX, int mouseY)
    {
        NonNullList<FluidResource> filter = menu.getMenuContext().getFluidFilters();

        if (inputFluidSlots.contains(slot) && slot.getAmount() <= 0 && filter != null)
        {
            int x = slot.getX();
            int y = slot.getY();

            FluidResource resource = filter.get(slot.getSlotIndex());
            if (!resource.isEmpty())
            {
                LimaGuiUtil.fluidSprite(graphics, resource.toStack(1), x, y);
            }

            graphics.fill(x, y, x + 16, y + 16, LOCKED_OVERLAY);
        }
        else
        {
            super.extractFluidSlot(graphics, slot, mouseX, mouseY);
        }
    }

    @Override
    protected void extractFluidSlotTooltips(LimaFluidSlot slot, List<Component> lines)
    {
        super.extractFluidSlotTooltips(slot, lines);

        lines.add(LTXILangKeys.DUMP_FLUIDS.translate().withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitPowerInSlot(graphics, 7, 52);

        renderLayout(graphics, leftPos, topPos, layout);
    }

    public static void renderLayout(GuiGraphicsExtractor graphics, int screenX, int screenY, RecipeLayout layout)
    {
        for (LayoutSlot.Type slotType : LayoutSlot.Type.values())
        {
            if (slotType.getContentsType() == null) continue;
            List<LayoutSlot> layoutSlots = layout.getSlotsForType(slotType);

            for (LayoutSlot slot : layoutSlots)
            {
                int sx = screenX + slot.x() - 1;
                int sy = screenY + slot.y() - 1;
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, slot.type().getSprite(), sx, sy, 18, 18);
            }
        }
    }

    private static class LockSlotsButton extends LTXISidebarButton.LeftSided
    {
        private final RecipeLayoutScreen parent;

        private LockSlotsButton(int x, int y, RecipeLayoutScreen parent)
        {
            super(x, y, Component.empty());
            this.parent = parent;
        }

        @Override
        protected void onPress()
        {
            parent.sendUnitButtonData(0);
        }

        @Override
        protected void extractInnerContents(GuiGraphicsExtractor graphics, int guiX, int guiY)
        {
            Identifier sprite = parent.getMenu().getMenuContext().isLocked() ? LOCK_SPRITE : UNLOCKED_SPRITE;
            renderSprite(graphics, sprite, guiX, guiY);
        }

        @Override
        public void extractTooltip(TooltipLineConsumer consumer, int mouseX, int mouseY)
        {
            consumer.accept(LTXILangKeys.INPUT_FILTER_LOCK.translate());
        }
    }
}