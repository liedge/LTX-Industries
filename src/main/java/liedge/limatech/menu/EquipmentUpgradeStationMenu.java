package liedge.limatech.menu;

import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limatech.blockentity.EquipmentUpgradeStationBlockEntity;
import liedge.limatech.item.EquipmentUpgradeModuleItem;
import liedge.limatech.item.UpgradableEquipmentItem;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgradeEntry;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.game.LimaTechNetworkSerializers;
import liedge.limatech.registry.game.LimaTechSounds;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import static liedge.limatech.blockentity.EquipmentUpgradeStationBlockEntity.EQUIPMENT_ITEM_SLOT;
import static liedge.limatech.blockentity.EquipmentUpgradeStationBlockEntity.UPGRADE_INPUT_SLOT;
import static liedge.limatech.registry.game.LimaTechDataComponents.EQUIPMENT_UPGRADE_ENTRY;

public class EquipmentUpgradeStationMenu extends UpgradesConfigMenu<EquipmentUpgradeStationBlockEntity, EquipmentUpgrade, EquipmentUpgrades>
{
    public EquipmentUpgradeStationMenu(LimaMenuType<EquipmentUpgradeStationBlockEntity, ?> type, int containerId, Inventory inventory, EquipmentUpgradeStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, 0);

        addSlot(EQUIPMENT_ITEM_SLOT, 24, 65);
        addUpgradeInsertionSlot(UPGRADE_INPUT_SLOT);
        addPlayerInventoryAndHotbar(15, 118);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleAction(0, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::tryRemoveUpgrade);
    }

    @Override
    protected NetworkSerializer<EquipmentUpgrades> getUpgradesSerializer()
    {
        return LimaTechNetworkSerializers.ITEM_EQUIPMENT_UPGRADES.get();
    }

    @Override
    protected EquipmentUpgrades getUpgrades()
    {
        return UpgradableEquipmentItem.getEquipmentUpgradesFromStack(menuContext.getItemHandler().getStackInSlot(EQUIPMENT_ITEM_SLOT));
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (index == EQUIPMENT_ITEM_SLOT)
        {
            return quickMoveToAllInventory(stack, true);
        }
        else if (stack.getItem() instanceof UpgradableEquipmentItem)
        {
            return quickMoveToContainerSlot(stack, EQUIPMENT_ITEM_SLOT);
        }
        else if (stack.getItem() instanceof EquipmentUpgradeModuleItem && canInstallUpgrade(stack))
        {
            return quickMoveToContainerSlot(stack, UPGRADE_INPUT_SLOT);
        }
        else
        {
            return false;
        }
    }

    @Override
    protected boolean canInstallUpgrade(ItemStack upgradeModuleItem)
    {
        ItemStack equipmentStack = menuContext.getItemHandler().getStackInSlot(EQUIPMENT_ITEM_SLOT);

        if (equipmentStack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(equipmentStack);
            EquipmentUpgradeEntry entry = upgradeModuleItem.get(EQUIPMENT_UPGRADE_ENTRY);

            return entry != null && upgrades.canInstallUpgrade(equipmentStack, entry);
        }

        return false;
    }

    @Override
    protected void tryInstallUpgrade(ItemStack upgradeModuleItem, ServerLevel level)
    {
        LimaItemHandlerBase beInventory = menuContext.getItemHandler();
        ItemStack equipmentStack = beInventory.getStackInSlot(EQUIPMENT_ITEM_SLOT).copy();

        if (equipmentStack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            EquipmentUpgrades currentUpgrades = equipmentItem.getUpgrades(equipmentStack);
            EquipmentUpgradeEntry entry = upgradeModuleItem.get(EQUIPMENT_UPGRADE_ENTRY);

            if (entry != null && currentUpgrades.canInstallUpgrade(equipmentStack, entry))
            {
                // Get previous rank
                int previousRank = currentUpgrades.getUpgradeRank(entry.upgrade());

                // Modify the upgrades and consume upgrade module item
                EquipmentUpgrades newUpgrades = currentUpgrades.toMutableContainer().set(entry).toImmutable();
                equipmentItem.setUpgrades(equipmentStack, newUpgrades);
                beInventory.setStackInSlot(EQUIPMENT_ITEM_SLOT, equipmentStack);
                beInventory.extractItem(UPGRADE_INPUT_SLOT, 1, false);

                if (previousRank > 0) ejectModuleItem(getServerUser(), entry.upgrade(), previousRank);

                sendSoundToPlayer(getServerUser(), LimaTechSounds.UPGRADE_INSTALL);
            }
        }
    }

    @Override
    protected void tryRemoveUpgrade(ServerPlayer sender, ResourceLocation upgradeId)
    {
        LimaItemHandlerBase beInventory = menuContext.getItemHandler();
        ItemStack equipmentStack = beInventory.getStackInSlot(EQUIPMENT_ITEM_SLOT).copy();

        if (equipmentStack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            EquipmentUpgrades currentUpgrades = equipmentItem.getUpgrades(equipmentStack);
            Holder<EquipmentUpgrade> upgradeHolder = level().registryAccess().holderOrThrow(ResourceKey.create(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, upgradeId));

            int rank = currentUpgrades.getUpgradeRank(upgradeHolder);
            if (rank > 0)
            {
                EquipmentUpgrades newUpgrades = currentUpgrades.toMutableContainer().remove(upgradeHolder).toImmutable();
                equipmentItem.setUpgrades(equipmentStack, newUpgrades);
                beInventory.setStackInSlot(EQUIPMENT_ITEM_SLOT, equipmentStack);

                ejectModuleItem(sender, upgradeHolder, rank);
                sendSoundToPlayer(sender, LimaTechSounds.UPGRADE_REMOVE);
            }
        }
    }

    @Override
    protected ItemStack createModuleItem(Holder<EquipmentUpgrade> upgrade, int upgradeRank)
    {
        return EquipmentUpgradeModuleItem.createStack(upgrade, upgradeRank);
    }
}