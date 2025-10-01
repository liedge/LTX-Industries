package liedge.ltxindustries.blockentity.base;

import com.google.common.collect.ImmutableMap;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limacore.util.LimaRegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

public final class ConfigurableIOBlockEntityType<BE extends LimaBlockEntity> extends LimaBlockEntityType<BE>
{
    public static <BE extends LimaBlockEntity> Builder<BE> sidedBuilder(BlockEntitySupplier<BE> factory)
    {
        return new Builder<>(factory);
    }

    private final Map<BlockEntityInputType, IOConfigurationRules> configRules;
    private final Set<BlockEntityInputType> validInputTypes;

    private ConfigurableIOBlockEntityType(BlockEntitySupplier<BE> factory, Set<Block> validBlocks, Map<BlockEntityInputType, IOConfigurationRules> configRules, @Nullable Holder<MenuType<?>> menuTypeHolder)
    {
        super(factory, validBlocks, menuTypeHolder);
        this.configRules = configRules;
        this.validInputTypes = Collections.unmodifiableSet(configRules.keySet());
    }

    public Collection<BlockEntityInputType> getValidInputTypes()
    {
        return validInputTypes;
    }

    public IOConfigurationRules getIOConfigRules(BlockEntityInputType inputType)
    {
        IOConfigurationRules rules = configRules.get(inputType);
        if (rules != null) return rules;
        else throw new IllegalArgumentException(String.format("Block entity type %s does not support input type %s", LimaRegistryUtil.getNonNullRegistryId(this, BuiltInRegistries.BLOCK_ENTITY_TYPE), inputType.getSerializedName()));
    }

    public static class Builder<BE extends LimaBlockEntity> extends AbstractBuilder<BE, ConfigurableIOBlockEntityType<BE>, Builder<BE>>
    {
        private final Map<BlockEntityInputType, IOConfigurationRules> ruleMap = new EnumMap<>(BlockEntityInputType.class);

        private Builder(BlockEntitySupplier<BE> factory)
        {
            super(factory);
        }

        public Builder<BE> withConfigRules(BlockEntityInputType inputType, IOConfigurationRules rules)
        {
            LimaCollectionsUtil.putNoDuplicates(ruleMap, inputType, rules);
            return this;
        }

        public Builder<BE> withConfigRules(BlockEntityInputType inputType, UnaryOperator<IOConfigurationRules.Builder> builder)
        {
            return withConfigRules(inputType, builder.apply(IOConfigurationRules.builder()).build());
        }

        @Override
        public ConfigurableIOBlockEntityType<BE> build()
        {
            return new ConfigurableIOBlockEntityType<>(factory, getValidBlocks(), ImmutableMap.copyOf(ruleMap), menuTypeHolder);
        }
    }
}