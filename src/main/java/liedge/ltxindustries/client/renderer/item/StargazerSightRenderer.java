package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.booleans.BooleanIntPair;
import liedge.limacore.client.renderer.LimaSpecialModelRenderer;
import liedge.limacore.client.util.LimaModelsUtil;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public final class StargazerSightRenderer implements LimaSpecialModelRenderer<BooleanIntPair>
{
    private final Vector3fc pos;
    private final ItemTintSource tint;

    private StargazerSightRenderer(Vector3fc pos, ItemTintSource tint)
    {
        this.pos = pos;
        this.tint = tint;
    }

    @Override
    public void submit(@Nullable BooleanIntPair argument, PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        if (argument == null) return;

        poseStack.pushPose();

        poseStack.translate(pos.x(), pos.y(), pos.z());

        nodeCollector.submitCustomGeometry(poseStack, LTXIRenderTypes.WONDERLAND_EPHEMERA, (pose, buffer) ->
        {
            int tint = argument.rightInt();

            LTXIRenderer.submitArcRing(pose, buffer, 0.05625f, 0.0025f, 0, 360, 24, tint);
            LTXIRenderer.submitArcRing(pose, buffer, 0.0078125f, 0.00390625f, 0, 360, 20, tint);

            float spin;
            if (argument.leftBoolean())
            {
                spin = (Util.getMillis() % 10000L) / 10000f * 360f;
            }
            else
            {
                spin = -27.5f;
            }
            LTXIRenderer.submitSplitArcsRing(pose, buffer, spin, 3, 55f, 0.00390625f, 0.0625f, 5, tint);
        });

        poseStack.popPose();
    }

    @Override
    public BooleanIntPair extractArgument(ItemStack item, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner)
    {
        boolean mainHand = LimaModelsUtil.isFirstPersonMainHand(displayContext, owner);
        int tint = resolveTint(this.tint, item, level, owner);

        return BooleanIntPair.of(mainHand, tint);
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) { }

    public record Unbaked(Vector3fc pos, ItemTintSource tint) implements LimaUnbaked<BooleanIntPair>
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                ExtraCodecs.VECTOR3F.fieldOf("pos").forGetter(Unbaked::pos),
                ItemTintSources.CODEC.fieldOf("tint").forGetter(Unbaked::tint))
                .apply(i, Unbaked::new));

        @Override
        public LimaSpecialModelRenderer<BooleanIntPair> bake(BakingContext context)
        {
            return new StargazerSightRenderer(pos, tint);
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<BooleanIntPair>> type()
        {
            return CODEC;
        }
    }
}