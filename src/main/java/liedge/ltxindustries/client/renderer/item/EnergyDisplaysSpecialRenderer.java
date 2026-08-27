package liedge.ltxindustries.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatIntPair;
import liedge.limacore.client.renderer.LimaSpecialModelRenderer;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.item.EnergyHolderItem;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.client.model.custom.EnergyDisplayModel;
import liedge.ltxindustries.item.weapon.WeaponItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public final class EnergyDisplaysSpecialRenderer implements LimaSpecialModelRenderer<FloatIntPair>
{
    private final FillSource fill;
    private final List<EnergyDisplayModel> models;
    private final ItemTintSource tint;

    private EnergyDisplaysSpecialRenderer(FillSource fill, List<EnergyDisplayModel> models, ItemTintSource tint)
    {
        this.fill = fill;
        this.models = models;
        this.tint = tint;
    }

    @Override
    public @Nullable FloatIntPair extractArgument(ItemStack item, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner)
    {
        float f = fill.get(item);
        return f <= 0 ? null : FloatIntPair.of(f, resolveTint(tint, item, level, owner));
    }

    @Override
    public void submit(@Nullable FloatIntPair argument, PoseStack poseStack, SubmitNodeCollector nodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor)
    {
        if (argument != null)
        {
            for (EnergyDisplayModel model : models)
            {
                model.submit(poseStack, nodeCollector, argument.firstFloat(), argument.secondInt(), 0.8f);
            }
        }
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output)
    {
        for (EnergyDisplayModel model : models)
        {
            model.getExtents(output);
        }
    }

    public enum FillSource implements StringRepresentable
    {
        ECA("energy_cell_array"),
        WEAPONS("weapons");

        private static final Codec<FillSource> CODEC = LimaEnumCodec.create(FillSource.class);

        private final String name;

        FillSource(String name)
        {
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        private float get(ItemStack stack)
        {
            switch (this)
            {
                case ECA ->
                {
                    if (stack.getItem() instanceof EnergyHolderItem item)
                        return item.getChargePercentage(stack);
                }
                case WEAPONS ->
                {
                    if (stack.getItem() instanceof WeaponItem item)
                        return LimaCoreMath.getFloatRatio(item.getAmmoLoaded(stack), item.getAmmoCapacity(stack));
                }
            }

            return 0f;
        }
    }

    public record Unbaked(FillSource fill, List<EnergyDisplayModel> models, ItemTintSource tint) implements LimaUnbaked<FloatIntPair>
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                FillSource.CODEC.fieldOf("fill").forGetter(Unbaked::fill),
                EnergyDisplayModel.CODEC.listOf(1, 10).fieldOf("models").forGetter(Unbaked::models),
                ItemTintSources.CODEC.fieldOf("tint").forGetter(Unbaked::tint))
                .apply(i ,Unbaked::new));

        @Override
        public LimaSpecialModelRenderer<FloatIntPair> bake(BakingContext context)
        {
            return new EnergyDisplaysSpecialRenderer(fill, models, tint);
        }

        @Override
        public MapCodec<? extends SpecialModelRenderer.Unbaked<FloatIntPair>> type()
        {
            return CODEC;
        }
    }
}