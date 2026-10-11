package liedge.ltxindustries.network.packet;

import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.ServerboundPayload;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.menu.RecipeLayoutMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public interface ServerboundSetFilterPacket extends ServerboundPayload
{
    int containerId();

    int slot();

    void handle(ServerPlayer sender, RecipeLayoutMenu<?> menu);

    @Override
    default void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        if (sender.containerMenu instanceof RecipeLayoutMenu<?> menu && menu.containerId == containerId())
        {
            handle(sender, menu);
        }
    }

    record ForItems(int containerId, int slot, ItemResource resource) implements ServerboundSetFilterPacket
    {
        public static final Type<ForItems> TYPE = LTXIndustries.RESOURCES.packetType("set_item_filter");
        public static final StreamCodec<RegistryFriendlyByteBuf, ForItems> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, ForItems::containerId,
                LimaStreamCodecs.BYTE_INT, ForItems::slot,
                ItemResource.STREAM_CODEC, ForItems::resource,
                ForItems::new);

        @Override
        public void handle(ServerPlayer sender, RecipeLayoutMenu<?> menu)
        {
            menu.getMenuContext().setItemFilter(slot, resource);
        }

        @Override
        public Type<? extends CustomPacketPayload> type()
        {
            return TYPE;
        }
    }

    record ForFluids(int containerId, int slot, FluidResource resource) implements ServerboundSetFilterPacket
    {
        public static final Type<ForFluids> TYPE = LTXIndustries.RESOURCES.packetType("set_fluid_filter");
        public static final StreamCodec<RegistryFriendlyByteBuf, ForFluids> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, ForFluids::containerId,
                LimaStreamCodecs.BYTE_INT, ForFluids::slot,
                FluidResource.STREAM_CODEC, ForFluids::resource,
                ForFluids::new);

        @Override
        public void handle(ServerPlayer sender, RecipeLayoutMenu<?> menu)
        {
            menu.getMenuContext().setFluidFilter(slot, resource);
        }

        @Override
        public Type<? extends CustomPacketPayload> type()
        {
            return TYPE;
        }
    }
}