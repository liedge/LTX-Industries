package liedge.ltxindustries.integration.jade;

import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.MeshBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.JadeUI;
import snownee.jade.api.view.*;

import java.util.List;

abstract class ProgressExtension<BE extends LimaBlockEntityAccess> implements IServerExtensionProvider<ProgressView.Data>, IClientExtensionProvider<ProgressView.Data, ProgressView>
{
    private final Class<BE> beClass;

    protected ProgressExtension(Class<BE> beClass)
    {
        this.beClass = beClass;
    }

    protected abstract ProgressView.@Nullable Data getServerData(BlockAccessor accessor, BE blockEntity);

    @Override
    public @Nullable List<ViewGroup<ProgressView.Data>> getGroups(Accessor<?> accessor)
    {
        BE blockEntity = null;

        if (accessor instanceof BlockAccessor blockAccessor)
        {
            BlockEntity be = blockAccessor.getBlockEntity();

            if (beClass.isInstance(be))
            {
                blockEntity = beClass.cast(be);
            }
            else if (be instanceof MeshBlockEntity meshBE)
            {
                blockEntity = meshBE.getPrimaryBlockEntity(blockAccessor.getPosition(), blockAccessor.getBlockState(), beClass);
            }

            if (blockEntity != null)
            {
                ProgressView.Data data = getServerData(blockAccessor, blockEntity);
                if (data != null)
                {
                    ViewGroup<ProgressView.Data> viewGroup = new ViewGroup<>(List.of(data));
                    return List.of(viewGroup);
                }
            }
        }

        return null;
    }

    @Override
    public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ProgressView.Data>> list)
    {
        return ClientViewGroup.map(list, data -> {
            int labelProgress = (int) (data.progress() * 100f);
            Component label = Component.literal(labelProgress + "%");
            ProgressView.Part part = ProgressView.Part.of(data.progress(), LTXIConstants.LIME_GREEN);

            return new ProgressView(part, label, JadeUI.progressStyle(), BoxStyle.nestedBox());
        }, null);
    }

    @Override
    public boolean shouldRequestData(Accessor<?> accessor)
    {
        if (accessor instanceof BlockAccessor blockAccessor)
        {
            BlockEntity be = blockAccessor.getBlockEntity();

            return beClass.isInstance(be) || be instanceof MeshBlockEntity;
        }

        return false;
    }
}