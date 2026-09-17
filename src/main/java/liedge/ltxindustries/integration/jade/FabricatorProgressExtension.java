package liedge.ltxindustries.integration.jade;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.view.ProgressView;

final class FabricatorProgressExtension extends ProgressExtension<BaseFabricatorBlockEntity>
{
    static final FabricatorProgressExtension INSTANCE = new FabricatorProgressExtension();

    private FabricatorProgressExtension()
    {
        super(BaseFabricatorBlockEntity.class);
    }

    @Override
    protected ProgressView.@Nullable Data getServerData(BlockAccessor accessor, BaseFabricatorBlockEntity blockEntity)
    {
        if (blockEntity.isCrafting() && accessor.getLevel() instanceof ServerLevel serverLevel)
        {
            int total = blockEntity.getRecipeCheck().getLastUsedRecipe(serverLevel).map(o -> o.value().getEnergyRequired()).orElse(0);
            float progress = LimaCoreMath.getFloatRatio(blockEntity.getEnergyCraftProgress(), total);
            return new ProgressView.Data(progress);
        }

        return null;
    }

    @Override
    public Identifier getUid()
    {
        return LTXIJadePlugin.UID_FABRICATOR_PROGRESS;
    }
}