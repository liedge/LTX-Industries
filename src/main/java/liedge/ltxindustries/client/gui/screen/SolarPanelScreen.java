package liedge.ltxindustries.client.gui.screen;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.menu.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelScreen extends MachineBaseScreen<SolarPanelMenu>
{
    private static final Identifier SUN_OFF_SPRITE = LTXIndustries.RESOURCES.id("widget/sun_off");
    private static final Identifier SUN_ON_SPRITE = LTXIndustries.RESOURCES.id("widget/sun_on");

    public SolarPanelScreen(SolarPanelMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, 148);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        int sunX = leftPos + 82;
        int sunY = topPos + 28;

        if (LTXIBlockProperties.isMachineActive(menu.menuContext().getBlockState()))
        {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SUN_ON_SPRITE, sunX, sunY, 12, 12);
        }
        else
        {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SUN_OFF_SPRITE, sunX, sunY, 12, 12);
        }
    }
}