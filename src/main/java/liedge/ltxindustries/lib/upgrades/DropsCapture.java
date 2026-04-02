package liedge.ltxindustries.lib.upgrades;

import com.google.common.base.Predicates;
import liedge.ltxindustries.lib.upgrades.effect.CaptureBlockDrops;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

public record DropsCapture(ResourceHandler<ItemResource> target, @Nullable Vec3 fallbackPos, Predicate<ItemEntity> predicate)
{
    public static @Nullable DropsCapture mobDropsToContainer(ResourceHandler<ItemResource> target, @Nullable Vec3 fallbackPos, Upgrades upgrades)
    {
        if (upgrades.upgradeEffectTypeAbsent(LTXIUpgradeEffectComponents.CAPTURE_MOB_DROPS)) return null;

        return new DropsCapture(target, fallbackPos, Predicates.alwaysTrue());
    }

    public static @Nullable DropsCapture mobDropsToPlayer(Player player, Upgrades upgrades)
    {
        return mobDropsToContainer(PlayerInventoryWrapper.of(player), player.getEyePosition(), upgrades);
    }

    public static @Nullable DropsCapture blockDropsToPlayer(Player player, Upgrades upgrades)
    {
        HolderSet<Item> holders = upgrades.mergeEffectHolderSets(LTXIUpgradeEffectComponents.CAPTURE_BLOCK_DROPS, CaptureBlockDrops::items);
        if (holders.equals(HolderSet.empty())) return null;

        return new DropsCapture(PlayerInventoryWrapper.of(player), player.getEyePosition(), ie -> ie.getItem().is(holders));
    }


    public void run(Collection<ItemEntity> itemEntities)
    {
        Iterator<ItemEntity> iterator = itemEntities.iterator();

        while (iterator.hasNext())
        {
            ItemEntity entity = iterator.next();
            if (!predicate.test(entity)) continue;

            ItemStack original = entity.getItem();
            int inserted = ResourceHandlerUtil.insertStacking(target, ItemResource.of(original), original.getCount(), null);
            original.shrink(inserted);

            if (original.isEmpty())
            {
                iterator.remove();
            }
            else if (fallbackPos != null)
            {
                entity.setPos(fallbackPos);
                entity.setDeltaMovement(Vec3.ZERO);
            }
        }
    }
}