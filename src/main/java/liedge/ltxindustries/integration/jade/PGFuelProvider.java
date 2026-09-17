package liedge.ltxindustries.integration.jade;

import liedge.ltxindustries.blockentity.PortableGeneratorBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

import java.util.function.Function;

final class PGFuelProvider implements StreamServerDataProvider<BlockAccessor, Integer>
{
    private static final StreamCodec<RegistryFriendlyByteBuf, Integer> STREAM_CODEC = ByteBufCodecs.VAR_INT.mapStream(Function.identity());
    static final PGFuelProvider INSTANCE = new PGFuelProvider();

    @Override
    public @Nullable Integer streamData(BlockAccessor accessor)
    {
        if (accessor.getBlockEntity() instanceof PortableGeneratorBlockEntity blockEntity)
        {
            return blockEntity.getFuelUnits();
        }

        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Integer> streamCodec()
    {
        return STREAM_CODEC;
    }

    @Override
    public Identifier getUid()
    {
        return LTXIJadePlugin.UID_PG_FUEL;
    }

    @Override
    public boolean shouldRequestData(BlockAccessor accessor)
    {
        return accessor.getBlockEntity() instanceof PortableGeneratorBlockEntity;
    }

    static final class Client implements IBlockComponentProvider
    {
        static final Client INSTANCE = new Client();

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
        {
            int fuel = PGFuelProvider.INSTANCE.decodeFromData(accessor).orElse(-1);

            if (fuel >= 0)
            {
                tooltip.add(JadeUI.smallItem(Items.COAL.getDefaultInstance()).narration(Component.empty()));
                tooltip.append(LTXILangKeys.FUEL_UNITS_STORED.translateArgs(fuel, PortableGeneratorBlockEntity.MAX_FUEL_UNITS));
            }
        }

        @Override
        public Identifier getUid()
        {
            return LTXIJadePlugin.UID_PG_FUEL;
        }
    }
}