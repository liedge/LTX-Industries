package liedge.ltxindustries.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.BootstrapObjectBuilder;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public record RecipeMode(HolderSet<RecipeType<?>> recipeTypes, Component displayName, ItemStack displayItem)
{
    public static final Codec<RecipeMode> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.RECIPE_TYPE).fieldOf("recipe_types").forGetter(RecipeMode::recipeTypes),
            ComponentSerialization.CODEC.fieldOf("display_name").forGetter(RecipeMode::displayName),
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("display_item").forGetter(RecipeMode::displayItem))
            .apply(instance, RecipeMode::new));
    public static final Codec<Holder<RecipeMode>> CODEC = RegistryFixedCodec.create(LTXIRegistries.Keys.RECIPE_MODES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<RecipeMode>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LTXIRegistries.Keys.RECIPE_MODES);

    public static Builder builder(ResourceKey<RecipeMode> key)
    {
        return new Builder(key);
    }

    public static final class Builder implements BootstrapObjectBuilder<RecipeMode>
    {
        private final ResourceKey<RecipeMode> key;
        private HolderSet<RecipeType<?>> recipeTypes;
        private Component displayName;
        private ItemStack displayItem;

        private Builder(ResourceKey<RecipeMode> key)
        {
            this.key = key;
            this.displayName = defaultName();
            this.displayItem = ItemStack.EMPTY;
        }

        public Builder styledName(ChatFormatting formatting)
        {
            this.displayName = defaultName().withStyle(formatting);
            return this;
        }

        public Builder styledName(LimaColor color)
        {
            this.displayName = defaultName().withStyle(color.chatStyle());
            return this;
        }

        public Builder forTypes(HolderSet<RecipeType<?>> holderSet)
        {
            this.recipeTypes = holderSet;
            return this;
        }

        @SafeVarargs
        public final Builder forTypes(Holder<RecipeType<?>>... holders)
        {
            return forTypes(HolderSet.direct(holders));
        }

        public Builder forType(Holder<RecipeType<?>> holder)
        {
            return forTypes(HolderSet.direct(holder));
        }

        public Builder icon(ItemStack displayItem)
        {
            this.displayItem = displayItem;
            return this;
        }

        public Builder icon(ItemLike item)
        {
            return icon(new ItemStack(item));
        }

        @Override
        public ResourceKey<RecipeMode> key()
        {
            return key;
        }

        @Override
        public RecipeMode build()
        {
            Preconditions.checkState(recipeTypes != null && recipeTypes.size() > 0, "Empty recipe types holder set.");
            Preconditions.checkState(!displayItem.isEmpty(), "Empty display item.");

            return new RecipeMode(recipeTypes, displayName, displayItem);
        }

        private MutableComponent defaultName()
        {
            return Component.translatable(identityTranslationKey());
        }
    }
}