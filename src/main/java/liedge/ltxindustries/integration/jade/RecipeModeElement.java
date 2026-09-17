package liedge.ltxindustries.integration.jade;

import liedge.ltxindustries.client.gui.ItemLikeIconsRenderer;
import liedge.ltxindustries.recipe.RecipeMode;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2fStack;
import snownee.jade.api.ui.Element;

final class RecipeModeElement extends Element
{
    private final Holder<RecipeMode> mode;
    private final float scale;

    RecipeModeElement(Holder<RecipeMode> mode, float scale)
    {
        this.mode = mode;
        this.scale = scale;

        int size = Mth.floor(18f * scale);

        this.width = size;
        this.height = size;
    }

    @Override
    public Component getNarration()
    {
        return mode.value().title();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
    {
        Matrix3x2fStack matrixStack = graphics.pose();

        matrixStack.pushMatrix();

        matrixStack.translate(getX() + 1, getY() + 1);
        matrixStack.scale(scale);

        ItemLikeIconsRenderer.render(graphics, mode.value().icon(), 0, 0);

        matrixStack.popMatrix();
    }
}