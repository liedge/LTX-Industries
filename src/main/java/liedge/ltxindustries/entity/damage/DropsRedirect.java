package liedge.ltxindustries.entity.damage;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.lib.upgrades.effect.equipment.DirectDropsUpgradeEffect;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;
import net.neoforged.neoforge.registries.holdersets.AnyHolderSet;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public record DropsRedirect(IItemHandler targetInventory, @Nullable Vec3 newDropsLocation, Predicate<ItemEntity> predicate)
{
    private static <U extends UpgradeBase<?, U>> HolderSet<Item> mergeSets(UpgradesContainerBase<?, U> upgrades, DirectDropsUpgradeEffect.Type type)
    {
        List<HolderSet<Item>> sets = new ObjectArrayList<>();

        for (Object2IntMap.Entry<Holder<U>> entry : upgrades.toEntrySet())
        {
            List<DirectDropsUpgradeEffect> effects = entry.getKey().value().getListEffect(LTXIUpgradeEffectComponents.DIRECT_DROPS.get());
            for (DirectDropsUpgradeEffect effect : effects)
            {
                if (effect.type() == type)
                {
                    HolderSet<Item> set = effect.items();
                    if (set instanceof AnyHolderSet<Item>) return set;
                    else sets.add(set);
                }
            }
        }

        return LimaRegistryUtil.mergeHolderSets(sets);
    }

    public static <U extends UpgradeBase<?, U>> @Nullable DropsRedirect create(IItemHandler targetInventory, @Nullable Vec3 newDropsLocation, UpgradesContainerBase<?, U> upgrades, DirectDropsUpgradeEffect.Type type)
    {
        HolderSet<Item> set = mergeSets(upgrades, type);
        if (set.equals(HolderSet.empty())) return null;
        return new DropsRedirect(targetInventory, newDropsLocation, ie -> set.contains(ie.getItem().getItemHolder()));
    }

    public static @Nullable DropsRedirect forPlayer(Player player, UpgradesContainerBase<?, ?> upgrades, DirectDropsUpgradeEffect.Type type)
    {
        return create(new PlayerMainInvWrapper(player.getInventory()), player.getEyePosition(), upgrades, type);
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