package liedge.ltxindustries.blockentity.base;

import liedge.limacore.menu.LimaMenuProvider;
import liedge.ltxindustries.menu.BlockIOConfigurationMenu;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public interface ConfigurableIOBlockEntity extends SubMenuProviderBlockEntity
{
    String KEY_IO_CONFIGS = "io_configs";

    Direction getFacing();

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

    void setIOConfiguration(BlockEntityInputType inputType, BlockIOConfiguration configuration);

    IOConfigurationRules getIOConfigRules(BlockEntityInputType inputType);

    default void openIOControlMenuScreen(Player player, BlockEntityInputType inputType)
    {
        BlockIOConfigurationMenu.MenuContext context = new BlockIOConfigurationMenu.MenuContext(this, inputType);
        Component title = Objects.requireNonNull(LTXIMenus.BLOCK_IO_CONFIGURATION.get().getDefaultTitle()).translateArgs(context.inputType().translate());
        LimaMenuProvider.create(LTXIMenus.BLOCK_IO_CONFIGURATION.get(), context, title, false).openMenuScreen(player);
    }
}