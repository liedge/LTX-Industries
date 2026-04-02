package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.TypedInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Predicate;

public record UpgradeUsers(HolderSet<Item> items, HolderSet<BlockEntityType<?>> blockEntities) implements Predicate<TypedInstance<?>>
{
    public static final Codec<UpgradeUsers> CODEC = RecordCodecBuilder.create(i -> i.group(
            RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items", HolderSet.empty()).forGetter(UpgradeUsers::items),
            RegistryCodecs.homogeneousList(Registries.BLOCK_ENTITY_TYPE).optionalFieldOf("block_entities", HolderSet.empty()).forGetter(UpgradeUsers::blockEntities))
            .apply(i, UpgradeUsers::new));

    @Override
    public boolean test(TypedInstance<?> typedInstance)
    {
        return switch (typedInstance)
        {
            case ItemStack stack -> stack.is(items);
            case BlockEntity blockEntity -> blockEntity.is(blockEntities);
            default -> false;
        };
    }
}