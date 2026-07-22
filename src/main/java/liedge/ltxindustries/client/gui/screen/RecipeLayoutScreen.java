package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import liedge.ltxindustries.client.gui.widget.OpenRecipeModesButton;
import liedge.ltxindustries.menu.RecipeLayoutMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class RecipeLayoutScreen extends MachineBaseScreen<RecipeLayoutMenu<?>>
{
    private final RecipeLayout layout;
    @Nullable
    private final RecipeModeHolderBlockEntity modeHolder;

    public RecipeLayoutScreen(RecipeLayoutMenu<?> menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.layout = menu.getLayout();
        this.modeHolder = LimaCoreObjects.tryCast(RecipeModeHolderBlockEntity.class, menu.menuContext());
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();
        addRenderableOnly(new MachineProgressWidget(menu.menuContext(), leftPos + layout.progressBarX(), topPos + layout.progressBarY()));

        if (modeHolder != null)
        {
            addRenderableWidget(new OpenRecipeModesButton(leftPos - leftPadding, bottomPos - 43, this, modeHolder));
        }
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
}