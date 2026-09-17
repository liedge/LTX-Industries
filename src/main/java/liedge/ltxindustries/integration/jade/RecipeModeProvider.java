package liedge.ltxindustries.integration.jade;

import liedge.ltxindustries.blockentity.MeshBlockEntity;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.recipe.RecipeMode;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;

final class RecipeModeProvider implements StreamServerDataProvider<BlockAccessor, Holder<RecipeMode>>
{
    static final RecipeModeProvider INSTANCE = new RecipeModeProvider();

    @Override
    public @Nullable Holder<RecipeMode> streamData(BlockAccessor accessor)
    {
        RecipeModeHolderBlockEntity blockEntity = null;

        BlockEntity be =  accessor.getBlockEntity();
        if (be instanceof RecipeModeHolderBlockEntity)
        {
            blockEntity = (RecipeModeHolderBlockEntity) be;
        }
        else if (be instanceof MeshBlockEntity meshBE)
        {
            blockEntity = meshBE.getPrimaryBlockEntity(accessor.getPosition(), accessor.getBlockState(), RecipeModeHolderBlockEntity.class);
        }

        if (blockEntity != null)
        {
            return blockEntity.getMode();
        }

        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Holder<RecipeMode>> streamCodec()
    {
        return RecipeMode.STREAM_CODEC;
    }

    @Override
    public Identifier getUid()
    {
        return LTXIJadePlugin.UID_RECIPE_MODE;
    }

    static final class Client implements IBlockComponentProvider
    {
        static final Client INSTANCE = new Client();

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
        {
            Holder<RecipeMode> mode = RecipeModeProvider.INSTANCE.decodeFromData(accessor).orElse(null);

            if (mode != null)
            {
                tooltip.add(new RecipeModeElement(mode, 0.5f).size(10, 8).offset(0, -1));
                tooltip.append(ComponentUtils.wrapInSquareBrackets(mode.value().title()));
            }
        }

        @Override
        public Identifier getUid()
        {
            return LTXIJadePlugin.UID_RECIPE_MODE;
        }
    }
}