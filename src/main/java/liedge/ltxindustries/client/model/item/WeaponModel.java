package liedge.ltxindustries.client.model.item;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import liedge.ltxindustries.client.model.custom.EnergyDisplayModel;
import liedge.ltxindustries.client.renderer.item.WeaponRenderState;
import liedge.ltxindustries.client.renderer.item.WeaponSpecialRenderer;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public final class WeaponModel implements ItemModel
{
    private final ItemTransforms transforms;
    private final List<EnergyDisplayModel> energyDisplays;
    private final @Nullable ItemTintSource energyTint;

    private final WeaponSpecialRenderer renderer;
    private final Supplier<Vector3fc[]> extents;

    private WeaponModel(ItemTransforms transforms, List<EnergyDisplayModel> energyDisplays, @Nullable ItemTintSource energyTint, WeaponSpecialRenderer renderer)
    {
        this.transforms = transforms;
        this.energyDisplays = energyDisplays;
        this.energyTint = energyTint;
        this.renderer = renderer;
        this.extents = Suppliers.memoize(() ->
        {
            Set<Vector3fc> set = new ObjectOpenHashSet<>();
            this.renderer.getExtents(set::add);
            return set.toArray(Vector3fc[]::new);
        });
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed)
    {
        output.appendModelIdentityElement(this);

        ItemStackRenderState.LayerRenderState layer = output.newLayer();

        WeaponRenderState argument = renderer.extractArgument(item, displayContext, owner);

        layer.setExtents(extents);
        layer.setupSpecialModel(renderer, argument);

        // Base argument modification
        if (argument != null)
        {
            argument.energyDisplays = this.energyDisplays;
            if (energyTint != null)
            {
                argument.energyTint = this.energyTint.calculate(item, level, owner == null ? null : owner.asLivingEntity());
            }

            output.appendModelIdentityElement(argument);
        }

        layer.setUsesBlockLight(true);
        layer.setItemTransform(transforms.getTransform(displayContext));
    }

    public record Unbaked(Identifier template, List<EnergyDisplayModel> energyDisplays, Optional<ItemTintSource> energyTint, WeaponSpecialRenderer.Unbaked specialModel)
        implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Identifier.CODEC.fieldOf("template").forGetter(Unbaked::template),
                EnergyDisplayModel.CODEC.listOf().fieldOf("energy_displays").forGetter(Unbaked::energyDisplays),
                ItemTintSources.CODEC.optionalFieldOf("energy_tint").forGetter(Unbaked::energyTint),
                WeaponSpecialRenderer.Unbaked.CODEC.fieldOf("special_model").forGetter(Unbaked::specialModel))
                .apply(i, Unbaked::new));

        @Override
        public ItemModel bake(BakingContext context, Matrix4fc transformation)
        {
            ModelBaker baker = context.blockModelBaker();
            ItemTransforms transforms = baker.getModel(template).getTopTransforms();

            return new WeaponModel(transforms, energyDisplays, energyTint.orElse(null), specialModel.bakeRenderer(baker));
        }

        @Override
        public void resolveDependencies(Resolver resolver)
        {
            resolver.markDependency(template);
            specialModel.resolveDependencies(resolver);
        }

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type()
        {
            return CODEC;
        }
    }
}