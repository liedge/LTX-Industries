package liedge.ltxindustries.client.model.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.renderer.LimaSpecialModelRenderer;
import liedge.limacore.client.util.LimaModelsUtil;
import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.client.renderer.item.WeaponSpecialRenderer;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import java.util.List;

public final class WeaponItemModel implements ItemModel
{
    private static final ItemTintSource DEFAULT_TINT = new Constant(-1);

    private final ItemModel fixedModel;
    private final ModelRenderProperties properties;
    private final WeaponSpecialRenderer renderer;
    private final ItemTintSource frameTint;
    private final ItemTintSource chamberTint;

    private WeaponItemModel(ItemModel fixedModel, ModelRenderProperties properties, WeaponSpecialRenderer renderer, ItemTintSource frameTint, ItemTintSource chamberTint)
    {
        this.fixedModel = fixedModel;
        this.properties = properties;
        this.renderer = renderer;
        this.frameTint = frameTint;
        this.chamberTint = chamberTint;
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed)
    {
        Player player = owner == null ? null : LimaCoreObjects.tryCast(Player.class, owner.asLivingEntity());

        if (!LimaModelsUtil.isFirstPersonMainHand(displayContext, player))
        {
            fixedModel.update(output, item, resolver, displayContext, level, owner, seed);
            return;
        }

        ItemStackRenderState.LayerRenderState layer = output.newLayer();

        properties.applyToLayer(layer, displayContext);

        WeaponSpecialRenderer.State argument = renderer.extract(item, player, displayContext, level);

        if (argument != null)
        {
            argument.frameTint = new int[]{LimaModelsUtil.resolveTint(frameTint, item, level, player)};
            argument.chamberTint = new int[]{LimaModelsUtil.resolveTint(chamberTint, item, level, player)};
        }

        layer.setupSpecialModel(renderer, argument);
    }

    public record Unbaked(Identifier template,
                          Identifier frame,
                          Identifier chamber,
                          List<LimaSpecialModelRenderer.LimaUnbaked<?>> frameExtras,
                          WeaponSpecialRenderer.SpecialUnbaked specialModel,
                          ItemTintSource frameTint,
                          ItemTintSource chamberTint) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Identifier.CODEC.fieldOf("template").forGetter(Unbaked::template),
                Identifier.CODEC.fieldOf("frame").forGetter(Unbaked::frame),
                Identifier.CODEC.fieldOf("chamber").forGetter(Unbaked::chamber),
                LimaSpecialModelRenderer.LimaUnbaked.CODEC.sizeLimitedListOf(4).optionalFieldOf("frame_extras", List.of()).forGetter(Unbaked::frameExtras),
                WeaponSpecialRenderer.SpecialUnbaked.CODEC.fieldOf("renderer").forGetter(Unbaked::specialModel),
                ItemTintSources.CODEC.optionalFieldOf("frame_tint", DEFAULT_TINT).forGetter(Unbaked::frameTint),
                ItemTintSources.CODEC.optionalFieldOf("chamber_tint", DEFAULT_TINT).forGetter(Unbaked::chamberTint))
                .apply(i, Unbaked::new));

        @Override
        public ItemModel bake(BakingContext context, Matrix4fc transformation)
        {
            List<ItemModel.Unbaked> fixedParts = new ObjectArrayList<>();
            fixedParts.add(LimaModelsUtil.blendsLast(frame, frameTint));
            fixedParts.add(LimaModelsUtil.blendsLast(chamber, chamberTint));
            frameExtras.forEach(extra -> fixedParts.add(LimaModelsUtil.specialModel(template, extra)));

            ModelBaker baker = context.blockModelBaker();
            ItemModel fixedModel = LimaModelsUtil.composite(fixedParts).bake(context, transformation);
            ModelRenderProperties properties = LimaModelsUtil.resolveProperties(baker, template);
            WeaponSpecialRenderer renderer = specialModel.bake(context, frame, chamber, frameExtras);

            return new WeaponItemModel(fixedModel, properties, renderer, frameTint, chamberTint);
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            resolver.markDependency(template);
            resolver.markDependency(frame);
            resolver.markDependency(chamber);
        }

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type()
        {
            return CODEC;
        }
    }
}