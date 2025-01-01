package liedge.limatech.menu;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limacore.inventory.menu.LimaItemHandlerMenuSlot;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.blockentity.EquipmentModTableBlockEntity;
import liedge.limatech.item.EquipmentUpgradeItem;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechDataComponents;
import liedge.limatech.registry.LimaTechNetworkSerializers;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.upgradesystem.EquipmentUpgrade;
import liedge.limatech.upgradesystem.EquipmentUpgradeEntry;
import liedge.limatech.upgradesystem.ItemEquipmentUpgrades;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

import static liedge.limatech.blockentity.EquipmentModTableBlockEntity.*;

public class EquipmentModTableMenu extends LimaItemHandlerMenu<EquipmentModTableBlockEntity>
{
    private final List<EquipmentUpgradeEntry> remoteUpgrades = new ObjectArrayList<>();
    private boolean screenUpdate = true;

    public EquipmentModTableMenu(LimaMenuType<EquipmentModTableBlockEntity, ?> type, int containerId, Inventory inventory, EquipmentModTableBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addContextSlot(EQUIPMENT_ITEM_SLOT, 24, 51);
        addSlot(new InsertSlot(UPGRADE_INPUT_SLOT, 15, 87));
        addContextSlot(UPGRADE_OUTPUT_SLOT, 33, 87, false);

        addPlayerInventory(15, 118);
        addPlayerHotbar(15, 176);
    }

    public List<EquipmentUpgradeEntry> getRemoteUpgrades()
    {
        return remoteUpgrades;
    }

    public boolean shouldUpdateScreen()
    {
        if (screenUpdate)
        {
            screenUpdate = false;
            return true;
        }

        return false;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaTechNetworkSerializers.ITEM_EQUIPMENT_UPGRADES, () -> ItemEquipmentUpgrades.getFromItem(menuContext.getItemHandler().getStackInSlot(EQUIPMENT_ITEM_SLOT)), upgrades -> {
            remoteUpgrades.clear();
            remoteUpgrades.addAll(upgrades.toEntryList());
            screenUpdate = true;
        }));
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleAction(0, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::tryRemoveUpgrade);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index >= EQUIPMENT_ITEM_SLOT && index <= UPGRADE_OUTPUT_SLOT)
        {
            return quickMoveToAllInventory(stack, true);
        }
        else if (stack.getItem() instanceof WeaponItem)
        {
            return quickMoveToContainerSlot(stack, EQUIPMENT_ITEM_SLOT);
        }
        else if (stack.getItem() instanceof EquipmentUpgradeItem && canInstallUpgrade(stack))
        {
            return quickMoveToContainerSlot(stack, UPGRADE_INPUT_SLOT);
        }
        else
        {
            return false;
        }
    }

    private boolean canInstallUpgrade(ItemStack upgradeItem)
    {
        ItemStack equipmentItem = menuContext.getItemHandler().getStackInSlot(EQUIPMENT_ITEM_SLOT);

        if (equipmentItem.getItem() instanceof WeaponItem)
        {
            ItemEquipmentUpgrades upgrades = ItemEquipmentUpgrades.getFromItem(equipmentItem);
            EquipmentUpgradeEntry data = EquipmentUpgradeEntry.getFromItem(upgradeItem);

            return data != null && upgrades.canInstallUpgrade(equipmentItem, data.upgrade());
        }

        return false;
    }

    private void tryInstallUpgrade(ItemStack upgradeItem)
    {
        LimaItemHandlerBase beInventory = menuContext.getItemHandler();
        ItemStack equipmentItem = beInventory.getStackInSlot(EQUIPMENT_ITEM_SLOT).copy();

        if (equipmentItem.getItem() instanceof WeaponItem)
        {
            ItemEquipmentUpgrades currentUpgrades = ItemEquipmentUpgrades.getFromItem(equipmentItem);
            EquipmentUpgradeEntry data = EquipmentUpgradeEntry.getFromItem(upgradeItem);

            if (data != null && currentUpgrades.canInstallUpgrade(equipmentItem, data.upgrade()))
            {
                data.upgrade().value().effects().forEach(effect -> effect.preUpgradeInstall(currentUpgrades, equipmentItem, data.upgradeRank()));

                ItemEquipmentUpgrades newUpgrades = currentUpgrades.asBuilder().add(data.upgrade(), data.upgradeRank()).build();
                equipmentItem.set(LimaTechDataComponents.EQUIPMENT_UPGRADES, newUpgrades);
                data.upgrade().value().effects().forEach(effect -> effect.postUpgradeInstall(newUpgrades, equipmentItem, data.upgradeRank()));

                beInventory.setStackInSlot(EQUIPMENT_ITEM_SLOT, equipmentItem);
                beInventory.extractItem(UPGRADE_INPUT_SLOT, 1, false);
            }
        }
    }

    private void tryRemoveUpgrade(ServerPlayer sender, ResourceLocation upgradeId)
    {
        LimaItemHandlerBase beInventory = menuContext.getItemHandler();
        ItemStack equipmentItem = beInventory.getStackInSlot(EQUIPMENT_ITEM_SLOT).copy();
        ResourceKey<EquipmentUpgrade> upgradeKey = ResourceKey.create(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY, upgradeId);

        ItemEquipmentUpgrades currentUpgrades = ItemEquipmentUpgrades.getFromItem(equipmentItem);
        Holder<EquipmentUpgrade> upgradeHolder = level().registryAccess().holderOrThrow(upgradeKey);

        int upgradeRank = currentUpgrades.getUpgradeRank(upgradeHolder);
        if (upgradeRank > 0)
        {
            ItemStack upgradeItem = EquipmentUpgradeItem.createStack(level().registryAccess(), upgradeKey, upgradeRank);

            if (LimaItemUtil.canMergeItemStacks(beInventory.getStackInSlot(UPGRADE_OUTPUT_SLOT), upgradeItem))
            {
                upgradeHolder.value().effects().forEach(effect -> effect.preUpgradeRemoved(currentUpgrades, equipmentItem, upgradeRank));

                ItemEquipmentUpgrades newUpgrades = currentUpgrades.asBuilder().remove(upgradeHolder).build();
                equipmentItem.set(LimaTechDataComponents.EQUIPMENT_UPGRADES, newUpgrades);
                upgradeHolder.value().effects().forEach(effect -> effect.postUpgradeRemoved(newUpgrades, equipmentItem, upgradeRank));

                beInventory.insertItem(UPGRADE_OUTPUT_SLOT, upgradeItem, false);
                beInventory.setStackInSlot(EQUIPMENT_ITEM_SLOT, equipmentItem);
            }
        }
    }

    private class InsertSlot extends LimaItemHandlerMenuSlot
    {
        private InsertSlot(int slotIndex, int xPos, int yPos)
        {
            super(menuContext.getItemHandler(), slotIndex, xPos, yPos);
        }

        @Override
        public void setByPlayer(ItemStack stack)
        {
            super.setByPlayer(stack);

            if (!level().isClientSide() && !stack.isEmpty())
            {
                tryInstallUpgrade(stack);
            }
        }

        @Override
        public boolean mayPlace(ItemStack stack)
        {
            return super.mayPlace(stack) && canInstallUpgrade(stack);
        }
    }
}