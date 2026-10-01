package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

public record DropLootTable(ResourceKey<LootTable> tableId) implements EntityUpgradeEffect
{
    public static final MapCodec<DropLootTable> CODEC = LootTable.KEY_CODEC.fieldOf("table").xmap(DropLootTable::new, DropLootTable::tableId);

    public static DropLootTable dropLoot(ResourceKey<LootTable> tableId)
    {
        return new DropLootTable(tableId);
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(tableId);

        table.getRandomItems(context, stack -> {
            ItemEntity entity = new ItemEntity(level, affectedEntity.getX(), affectedEntity.getEyeY(), affectedEntity.getZ(), stack);
            entity.setPickUpDelay(0);
            level.addFreshEntity(entity);
        });
    }

    @Override
    public MapCodec<? extends EntityUpgradeEffect> codec()
    {
        return CODEC;
    }
}