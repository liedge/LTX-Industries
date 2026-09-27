package liedge.ltxindustries.blockentity.base;

import liedge.limacore.menu.StandaloneMenuProvider;
import liedge.ltxindustries.menu.BlockIOConfigurationMenu;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

public interface ConfigurableIOBlockEntity extends SubMenuProviderBlockEntity
{
    String KEY_IO_CONFIGS = "io_configs";

    default Direction getFacing()
    {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    default IORuleSet getRuleSet()
    {
        return Objects.requireNonNull(getType().get(LTXIDataComponents.IO_RULES), "Missing IO rule set");
    }

    default boolean supportsInputType(ResourceType inputType)
    {
        return getRuleSet().containsKey(inputType);
    }

    @Nullable BlockIOConfiguration getIOConfiguration(ResourceType inputType);

    default BlockIOConfiguration getIOConfigurationOrThrow(ResourceType inputType)
    {
        BlockIOConfiguration configuration = getIOConfiguration(inputType);
        if (configuration != null)
            return configuration;
        else
            throw new IllegalArgumentException("Block entity does not support " + inputType.getSerializedName() + " IO configurations.");
    }

    boolean setIOConfiguration(ResourceType inputType, BlockIOConfiguration configuration);

    default IORules getIOConfigRules(ResourceType resourceType)
    {
        IORules rules = getRuleSet().get(resourceType);
        return Objects.requireNonNull(rules, "Missing IO rules for " + resourceType.getSerializedName());
    }

    default void openIOControlsSubMenu(ServerPlayer player, ResourceType resourceType)
    {
        MenuProvider provider = new StandaloneMenuProvider(Component.translatable(LTXIMenus.BLOCK_IO_CONFIGURATION.get().getDescriptionId(), resourceType.translate()), false,
                (id, inv, _) -> new BlockIOConfigurationMenu(id, inv, this, resourceType));
        player.openMenu(provider, net -> {
            net.writeBlockPos(getBlockPos());
            ResourceType.STREAM_CODEC.encode(net, resourceType);
        });
    }

    default void loadIOConfigurations(ValueInput global, BiConsumer<ResourceType, BlockIOConfiguration> consumer)
    {
        ValueInput input = global.child(KEY_IO_CONFIGS).orElse(null);
        if (input == null) return;

        for (ResourceType type : getRuleSet().getResourceTypes())
        {
            input.read(type.getSerializedName(), BlockIOConfiguration.CODEC).ifPresent(config -> consumer.accept(type, config));
        }
    }

    default void saveIOConfigurations(ValueOutput global)
    {
        ValueOutput output = global.child(KEY_IO_CONFIGS);

        for (ResourceType type : getRuleSet().getResourceTypes())
        {
            BlockIOConfiguration config = getIOConfiguration(type);
            output.storeNullable(type.getSerializedName(), BlockIOConfiguration.CODEC, config);
        }
    }
}