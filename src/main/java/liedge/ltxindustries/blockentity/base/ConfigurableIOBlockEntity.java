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

public interface ConfigurableIOBlockEntity extends SubMenuProviderBlockEntity
{
    String KEY_IO_CONFIGS = "io_configs";

    default Direction getFacing()
    {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    Collection<BlockEntityInputType> getConfigurableInputTypes();

    @Nullable BlockIOConfiguration getIOConfiguration(BlockEntityInputType inputType);

    default BlockIOConfiguration getIOConfigurationOrThrow(BlockEntityInputType inputType)
    {
        BlockIOConfiguration configuration = getIOConfiguration(inputType);
        if (configuration != null)
            return configuration;
        else
            throw new IllegalArgumentException("Block entity does not support " + inputType.getSerializedName() + " IO configurations.");
    }

    boolean setIOConfiguration(BlockEntityInputType inputType, BlockIOConfiguration configuration);

    IOConfigurationRules getIOConfigRules(BlockEntityInputType inputType);

    default void openIOControlMenuScreen(Player player, BlockEntityInputType inputType)
    {
        BlockIOConfigurationMenu.MenuContext context = new BlockIOConfigurationMenu.MenuContext(this, inputType);
        Component title = Objects.requireNonNull(LTXIMenus.BLOCK_IO_CONFIGURATION.get().getDefaultTitle()).translateArgs(context.inputType().translate());
        LimaMenuProvider.create(LTXIMenus.BLOCK_IO_CONFIGURATION.get(), context, title, false).openMenuScreen(player);
    }

    default void loadIOConfigurations(ValueInput global)
    {
        ValueInput input = global.child(KEY_IO_CONFIGS).orElse(null);
        if (input == null) return;

        for (BlockEntityInputType type : getConfigurableInputTypes())
        {
            input.read(type.getSerializedName(), BlockIOConfiguration.CODEC).ifPresent(config -> setIOConfiguration(type, config));
        }
    }

    default void saveIOConfigurations(ValueOutput global)
    {
        ValueOutput output = global.child(KEY_IO_CONFIGS);

        for (BlockEntityInputType type : getConfigurableInputTypes())
        {
            BlockIOConfiguration config = getIOConfiguration(type);
            output.storeNullable(type.getSerializedName(), BlockIOConfiguration.CODEC, config);
        }
    }
}