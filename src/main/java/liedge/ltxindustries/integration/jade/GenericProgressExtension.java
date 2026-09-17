package liedge.ltxindustries.integration.jade;

import liedge.ltxindustries.blockentity.base.TimedProcessBlockEntity;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.view.ProgressView;

final class GenericProgressExtension extends ProgressExtension<TimedProcessBlockEntity>
{
    static final GenericProgressExtension INSTANCE = new GenericProgressExtension();

    private GenericProgressExtension()
    {
        super(TimedProcessBlockEntity.class);
    }

    @Override
    protected ProgressView.@Nullable Data getServerData(BlockAccessor accessor, TimedProcessBlockEntity blockEntity)
    {
        if (blockEntity.isActive())
        {
            return new ProgressView.Data(blockEntity.getProcessTimePercent());
        }

        return null;
    }

    @Override
    public Identifier getUid()
    {
        return LTXIJadePlugin.UID_GENERIC_PROGRESS;
    }
}