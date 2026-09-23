package liedge.ltxindustries.blockentity.base;

import liedge.limacore.menu.LimaMenuProvider;
import liedge.ltxindustries.menu.BlockIOConfigurationMenu;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;

public interface ConfigurableIOBlockEntity extends SubMenuProviderBlockEntity
{
    String KEY_IO_CONFIGS = "io_configs";

    default Direction getFacing()
    {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    Collection<ResourceType> getConfigurableInputTypes();

    default boolean supportsInputType(ResourceType inputType)
    {
        return getConfigurableInputTypes().contains(inputType);
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

    IORules getIOConfigRules(ResourceType inputType);

    default void openIOControlsSubMenu(Player player, ResourceType inputType)
    {
        BlockIOConfigurationMenu.MenuContext context = new BlockIOConfigurationMenu.MenuContext(this, inputType);
        Component title = Objects.requireNonNull(LTXIMenus.BLOCK_IO_CONFIGURATION.get().getDefaultTitle()).translateArgs(context.inputType().translate());
        LimaMenuProvider.create(LTXIMenus.BLOCK_IO_CONFIGURATION.get(), context, title, false).openMenuScreen(player);
    }

    default void loadIOConfigurations(ValueInput global, BiConsumer<ResourceType, BlockIOConfiguration> consumer)
    {
        ValueInput input = global.child(KEY_IO_CONFIGS).orElse(null);
        if (input == null) return;

        for (ResourceType type : getConfigurableInputTypes())
        {
            input.read(type.getSerializedName(), BlockIOConfiguration.CODEC).ifPresent(config -> consumer.accept(type, config));
        }
    }

    default void saveIOConfigurations(ValueOutput global)
    {
        ValueOutput output = global.child(KEY_IO_CONFIGS);

        for (ResourceType type : getConfigurableInputTypes())
        {
            BlockIOConfiguration config = getIOConfiguration(type);
            output.storeNullable(type.getSerializedName(), BlockIOConfiguration.CODEC, config);
        }
    }
}