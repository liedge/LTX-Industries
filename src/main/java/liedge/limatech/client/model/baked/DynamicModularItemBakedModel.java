package liedge.limatech.client.model.baked;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.model.BakedItemLayer;
import liedge.limacore.client.model.BakedQuadGroup;
import liedge.limacore.client.model.LimaBasicBakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DynamicModularItemBakedModel extends LimaBasicBakedModel
{
    private final RenderType nonEmissiveRenderType;
    private final RenderType emissiveRenderType;
    private final Map<String, BakedQuadGroup> submodels;
    private final List<BakedModel> renderPasses;

    DynamicModularItemBakedModel(boolean ambientOcclusion,
                                 boolean gui3d,
                                 boolean useBlockLight,
                                 TextureAtlasSprite particleIcon,
                                 ItemTransforms transforms,
                                 boolean useCustomRenderer,
                                 RenderTypeGroup renderTypeGroup,
                                 Map<String, BakedQuadGroup> allQuads,
                                 Map<String, BakedQuadGroup> submodels)
    {
        super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, ItemOverrides.EMPTY, useCustomRenderer, renderTypeGroup);

        this.nonEmissiveRenderType = LimaCoreClientUtil.getItemRenderTypeOrDefault(renderTypeGroup);
        this.emissiveRenderType = NeoForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get();
        this.submodels = Collections.unmodifiableMap(submodels);

        if (!useCustomRenderer)
        {
            BakedQuadGroup masterGroup = BakedQuadGroup.allOf(allQuads.values());
            BakedItemLayer nonEmissiveLayer = new BakedItemLayer(this, masterGroup.getNonEmissiveQuads(), nonEmissiveRenderType);
            BakedItemLayer emissiveLayer = new BakedItemLayer(this, masterGroup.getEmissiveQuads(), emissiveRenderType);
            this.renderPasses = List.of(nonEmissiveLayer, emissiveLayer);
        }
        else
        {
            this.renderPasses = List.of();
        }
    }

    public BakedQuadGroup getSubmodel(String name)
    {
        return Objects.requireNonNull(submodels.get(name), "Submodel '" + name + "' does not exist in parent model.");
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side)
    {
        return List.of();
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
    {
        return renderPasses;
    }

    public RenderType getNonEmissiveRenderType()
    {
        return nonEmissiveRenderType;
    }

    public RenderType getEmissiveRenderType()
    {
        return emissiveRenderType;
    }
}