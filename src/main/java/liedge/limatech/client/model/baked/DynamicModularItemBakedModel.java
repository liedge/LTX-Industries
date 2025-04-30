package liedge.limatech.client.model.baked;

import liedge.limacore.client.model.BakedItemLayer;
import liedge.limacore.client.model.BakedQuadGroup;
import liedge.limacore.client.model.LimaBasicBakedModel;
import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DynamicModularItemBakedModel extends LimaBasicBakedModel
{
    private final RenderType customBaseRenderType;
    private final RenderType customEmissiveRenderType;
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

        this.customBaseRenderType = !renderTypeGroup.isEmpty() ? renderTypeGroup.entity() : Sheets.translucentItemSheet();
        this.customEmissiveRenderType = LimaCoreRenderTypes.ITEM_POS_TEX_COLOR_SOLID;
        this.submodels = Collections.unmodifiableMap(submodels);

        if (!useCustomRenderer)
        {
            BakedQuadGroup masterGroup = BakedQuadGroup.allOf(allQuads.values());
            BakedItemLayer baseLayer = new BakedItemLayer(this, masterGroup.getNonEmissiveQuads(), renderTypeGroup, false);
            BakedItemLayer emissiveLayer = new BakedItemLayer(this, masterGroup.getEmissiveQuads(), customEmissiveRenderType, customEmissiveRenderType, true);
            this.renderPasses = List.of(baseLayer, emissiveLayer);
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

    public RenderType getCustomBaseRenderType()
    {
        return customBaseRenderType;
    }

    public RenderType getCustomEmissiveRenderType()
    {
        return customEmissiveRenderType;
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
}