package liedge.ltxindustries.entity.damage;

import com.google.common.base.Predicates;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.lib.upgrades.effect.CaptureBlockDrops;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

public record DropsRedirect(IItemHandler targetInventory, @Nullable Vec3 newDropsLocation, Predicate<ItemEntity> predicate)
{
    public static @Nullable DropsRedirect forMobDrops(IItemHandler targetInventory, @Nullable Vec3 newDropsLocation, UpgradesContainerBase<?, ?> upgrades)
    {
        if (!upgrades.upgradeEffectTypePresent(LTXIUpgradeEffectComponents.CAPTURE_MOB_DROPS.get())) return null;
        return new DropsRedirect(targetInventory, newDropsLocation, Predicates.alwaysTrue());
    }

    public static @Nullable DropsRedirect forMobDrops(Player player,UpgradesContainerBase<?, ?> upgrades)
    {
        return forMobDrops(new PlayerMainInvWrapper(player.getInventory()), player.getEyePosition(), upgrades);
    }

    public static @Nullable DropsRedirect forBlocks(Player player, UpgradesContainerBase<?, ?> upgrades)
    {
        HolderSet<Item> holders = upgrades.mergeEffectHolderSets(LTXIUpgradeEffectComponents.CAPTURE_BLOCK_DROPS, CaptureBlockDrops::items);
        if (holders.equals(HolderSet.empty())) return null;
        return new DropsRedirect(new PlayerMainInvWrapper(player.getInventory()), player.getEyePosition(), ie -> holders.contains(ie.getItem().getItemHolder()));
    }

    public void captureAndRelocateDrops(Collection<ItemEntity> itemEntities)
    {
        Iterator<ItemEntity> iterator = itemEntities.iterator();

        // Capture item entities in inventory
        while (iterator.hasNext())
        {
            ItemEntity itemEntity = iterator.next();
            if (!predicate.test(itemEntity)) continue;

            ItemStack original = itemEntity.getItem();
            ItemStack insertRemainder = ItemHandlerHelper.insertItemStacked(targetInventory, original, false);

            // Entity drop removed if fully inserted
            if (insertRemainder.isEmpty())
            {
                iterator.remove();
            }
            else
            {
                // Always relocate drops
                if (newDropsLocation != null)
                {
                    itemEntity.setPos(newDropsLocation);
                    itemEntity.setDeltaMovement(Vec3.ZERO);
                }

                // Replace the stack on partial insert
                if (original.getCount() != insertRemainder.getCount())
                    itemEntity.setItem(insertRemainder);
            }
        }
    }
}