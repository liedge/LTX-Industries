package liedge.ltxindustries.blockentity.base;

import com.mojang.serialization.DynamicOps;
import liedge.limacore.menu.LimaMenuProvider;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.menu.BlockIOConfigurationMenu;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

    boolean setIOConfiguration(BlockEntityInputType inputType, BlockIOConfiguration configuration);

    IOConfigurationRules getIOConfigRules(BlockEntityInputType inputType);

    default void openIOControlMenuScreen(Player player, BlockEntityInputType inputType)
    {
        BlockIOConfigurationMenu.MenuContext context = new BlockIOConfigurationMenu.MenuContext(this, inputType);
        Component title = Objects.requireNonNull(LTXIMenus.BLOCK_IO_CONFIGURATION.get().getDefaultTitle()).translateArgs(context.inputType().translate());
        LimaMenuProvider.create(LTXIMenus.BLOCK_IO_CONFIGURATION.get(), context, title, false).openMenuScreen(player);
    }

    default void loadIOConfigurations(CompoundTag tag, DynamicOps<Tag> ops)
    {
        CompoundTag configsTag = tag.getCompound(KEY_IO_CONFIGS);

        for (BlockEntityInputType type : getConfigurableInputTypes())
        {
            BlockIOConfiguration config = LimaNbtUtil.tryDecode(BlockIOConfiguration.CODEC, ops, configsTag, type.getSerializedName());
            if (config != null) setIOConfiguration(type, config);
        }
    }

    default void saveIOConfigurations(CompoundTag tag, DynamicOps<Tag> ops)
    {
        CompoundTag configsTag = new CompoundTag();

        for (BlockEntityInputType type : getConfigurableInputTypes())
        {
            BlockIOConfiguration config = getIOConfigurationOrThrow(type);
            LimaNbtUtil.tryEncodeTo(BlockIOConfiguration.CODEC, ops, config, configsTag, type.getSerializedName());
        }

        tag.put(KEY_IO_CONFIGS, configsTag);
    }
}