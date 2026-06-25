package liedge.ltxindustries.client.gui.screen;

import liedge.limacore.client.gui.BaseLimaRenderable;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.transfer.LimaEnergyUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.PortableGeneratorBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.menu.PortableGeneratorMenu;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;

public class PortableGeneratorScreen extends MachineBaseScreen<PortableGeneratorMenu>
{
    public PortableGeneratorScreen(PortableGeneratorMenu menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, DEFAULT_WIDTH, 148);
    }

    @Override
    protected void addWidgets()
    {
        super.addWidgets();

        addRenderableOnly(new FuelGauge(leftPos + 158, topPos + 15, menu.menuContext()));
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        blitSlotSprite(graphics, LayoutSlot.ITEM_SLOT_SPRITE, 79, 25);
    }

    private static class FuelGauge extends BaseLimaRenderable
    {
        private static final Identifier BG_SPRITE = LTXIndustries.RESOURCES.id("widget/fuel_gauge");

        private final PortableGeneratorBlockEntity blockEntity;
        private final TextureAtlasSprite lavaSprite;

        FuelGauge(int x, int y, PortableGeneratorBlockEntity blockEntity)
        {
            super(x, y, 8, 38);
            this.blockEntity = blockEntity;
            this.lavaSprite = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(Fluids.LAVA.defaultFluidState()).stillMaterial().sprite();
        }

        @Override
        public boolean hasTooltip()
        {
            return true;
        }

        @Override
        public void createWidgetTooltip(TooltipLineConsumer consumer)
        {
            consumer.accept(LTXILangKeys.FUEL_UNITS_STORED.translateArgs(blockEntity.getFuelUnits(), PortableGeneratorBlockEntity.MAX_FUEL_UNITS));
            Component energyPerFuel = Component.literal(LimaEnergyUtil.toEnergyString(LTXIMachinesConfig.PORTABLE_GENERATOR_ENERGY_PER_FUEL.getAsInt())).withStyle(LTXIConstants.REM_BLUE.chatStyle());
            consumer.accept(LTXILangKeys.ENERGY_PER_FUEL_UNIT.translateArgs(energyPerFuel));
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
        {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BG_SPRITE, getX(), getY(), getWidth(), getHeight());

            float fill = LimaCoreMath.getFloatRatio(blockEntity.getFuelUnits(), PortableGeneratorBlockEntity.MAX_FUEL_UNITS);
            if (fill > 0)
            {
                final int x1 = getX() + 1;
                final int x2 = x1 + 6;

                int fillHeight = (int) (32 * fill);
                int y = getY() + 3 + 32;

                while (fillHeight > 0)
                {
                    int tileHeight = Math.min(fillHeight, 16);
                    y -= tileHeight;
                    float vo = 1f - tileHeight * 0.0625f;

                    graphics.blit(lavaSprite.atlasLocation(), x1, y, x2, y + tileHeight, lavaSprite.getU0(), lavaSprite.getU(0.375f), lavaSprite.getV(vo), lavaSprite.getV1());
                    fillHeight -= tileHeight;
                }
            }
        }
    }
}