package liedge.limatech.menu;

import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limatech.blockentity.EquipmentUpgradeStationBlockEntity;
import liedge.limatech.item.EquipmentUpgradeModuleItem;
import liedge.limatech.item.UpgradableEquipmentItem;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgradeEntry;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.game.LimaTechNetworkSerializers;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import static liedge.limatech.blockentity.EquipmentUpgradeStationBlockEntity.EQUIPMENT_ITEM_SLOT;
import static liedge.limatech.blockentity.EquipmentUpgradeStationBlockEntity.UPGRADE_INPUT_SLOT;

public class EquipmentUpgradeStationMenu extends UpgradesConfigMenu<EquipmentUpgradeStationBlockEntity, EquipmentUpgrade, EquipmentUpgrades>
{
    public EquipmentUpgradeStationMenu(LimaMenuType<EquipmentUpgradeStationBlockEntity, ?> type, int containerId, Inventory inventory, EquipmentUpgradeStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

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
    protected EquipmentUpgrades getUpgradesFromContext()
    {
        return UpgradableEquipmentItem.getEquipmentUpgradesFromStack(menuContext.getItemHandler().getStackInSlot(EQUIPMENT_ITEM_SLOT));
    }

    @Override
    protected IItemHandlerModifiable menuContainer()
    {
        return menuContext.getItemHandler();
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
    protected boolean canInstallUpgrade(ItemStack upgradeItem)
    {
        ItemStack equipmentStack = menuContext.getItemHandler().getStackInSlot(EQUIPMENT_ITEM_SLOT);

        if (equipmentStack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            EquipmentUpgrades upgrades = equipmentItem.getUpgrades(equipmentStack);
            EquipmentUpgradeEntry data = EquipmentUpgradeEntry.getFromItem(upgradeItem);

            return data != null && upgrades.canInstallUpgrade(equipmentStack, data.upgrade());
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
            EquipmentUpgradeEntry data = EquipmentUpgradeEntry.getFromItem(upgradeModuleItem);

            if (data != null && currentUpgrades.canInstallUpgrade(equipmentStack, data.upgrade()))
            {
                EquipmentUpgrades newUpgrades = currentUpgrades.toMutableContainer().set(data.upgrade(), data.upgradeRank()).toImmutable();
                equipmentItem.setUpgrades(equipmentStack, newUpgrades);
                beInventory.setStackInSlot(EQUIPMENT_ITEM_SLOT, equipmentStack);
                beInventory.extractItem(UPGRADE_INPUT_SLOT, 1, false);
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
            ResourceKey<EquipmentUpgrade> upgradeKey = ResourceKey.create(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, upgradeId);
            EquipmentUpgrades currentUpgrades = equipmentItem.getUpgrades(equipmentStack);
            Holder<EquipmentUpgrade> upgradeHolder = level().registryAccess().holderOrThrow(upgradeKey);

            int upgradeRank = currentUpgrades.getUpgradeRank(upgradeHolder);
            if (upgradeRank > 0)
            {
                ItemStack upgradeModuleItem = EquipmentUpgradeModuleItem.createStack(upgradeHolder, upgradeRank);

                PlayerMainInvWrapper invWrapper = new PlayerMainInvWrapper(playerInventory);
                int nextSlot = LimaItemHandlerUtil.getNextEmptySlot(invWrapper);

                if (nextSlot != -1)
                {
                    EquipmentUpgrades newUpgrades = currentUpgrades.toMutableContainer().remove(upgradeHolder).toImmutable();
                    equipmentItem.setUpgrades(equipmentStack, newUpgrades);
                    invWrapper.insertItem(nextSlot, upgradeModuleItem, false);
                    beInventory.setStackInSlot(EQUIPMENT_ITEM_SLOT, equipmentStack);
                }
            }
        }
    }
}