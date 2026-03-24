package liedge.ltxindustries.client.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class WeaponSpecialRenderer<S> implements SpecialModelRenderer<S>
{

    @Override
    public void submit(@Nullable S argument, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, int packedOverlay, boolean hasFoil, int outlineColor)
    {

    }

    @Override
    public void getExtents(Consumer<Vector3fc> output)
    {

    }

    @Override
    public @Nullable S extractArgument(ItemStack stack)
    {
        return null;
    }
}