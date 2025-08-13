package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.network.NetworkSerializer;
import liedge.ltxindustries.blockentity.EquipmentUpgradeStationBlockEntity;
import liedge.ltxindustries.item.EquipmentUpgradeModuleItem;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgradeEntry;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import static liedge.ltxindustries.blockentity.EquipmentUpgradeStationBlockEntity.EQUIPMENT_ITEM_SLOT;
import static liedge.ltxindustries.blockentity.EquipmentUpgradeStationBlockEntity.UPGRADE_MODULE_SLOT;
import static liedge.ltxindustries.registry.game.LTXIDataComponents.EQUIPMENT_UPGRADE_ENTRY;

public class EquipmentUpgradeStationMenu extends UpgradesConfigMenu<EquipmentUpgradeStationBlockEntity, EquipmentUpgrade, EquipmentUpgrades>
{
    public EquipmentUpgradeStationMenu(LimaMenuType<EquipmentUpgradeStationBlockEntity, ?> type, int containerId, Inventory inventory, EquipmentUpgradeStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, menuContext.getItemHandlerOrThrow(BlockContentsType.GENERAL), UPGRADE_MODULE_SLOT);

        addSlot(BlockContentsType.GENERAL, EQUIPMENT_ITEM_SLOT, 24, 65, stack -> stack.getItem() instanceof UpgradableEquipmentItem);
        addPlayerInventoryAndHotbar(15, 118);
    }

    @Override
    protected NetworkSerializer<EquipmentUpgrades> getUpgradesSerializer()
    {
        return LTXINetworkSerializers.ITEM_EQUIPMENT_UPGRADES.get();
    }

    @Override
    protected EquipmentUpgrades getUpgrades()
    {
        return UpgradableEquipmentItem.getEquipmentUpgradesFromStack(moduleSourceInventory.getStackInSlot(EQUIPMENT_ITEM_SLOT));
    }

    @Override
    protected boolean canInstallUpgrade(ItemStack upgradeModuleItem)
    {
        ItemStack equipmentStack = moduleSourceInventory.getStackInSlot(EQUIPMENT_ITEM_SLOT);

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
        ItemStack equipmentStack = moduleSourceInventory.getStackInSlot(EQUIPMENT_ITEM_SLOT).copy();

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
                moduleSourceInventory.setStackInSlot(EQUIPMENT_ITEM_SLOT, equipmentStack);
                moduleSourceInventory.extractItem(UPGRADE_MODULE_SLOT, 1, false);

                if (previousRank > 0) ejectModuleItem(getServerUser(), entry.upgrade(), previousRank);

                sendSoundToPlayer(getServerUser(), LTXISounds.UPGRADE_INSTALL, 1f, 1f);
            }
        }
    }

    @Override
    protected void tryRemoveUpgrade(ServerPlayer sender, ResourceLocation upgradeId)
    {
        ItemStack equipmentStack = moduleSourceInventory.getStackInSlot(EQUIPMENT_ITEM_SLOT).copy();

        if (equipmentStack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            EquipmentUpgrades currentUpgrades = equipmentItem.getUpgrades(equipmentStack);
            Holder<EquipmentUpgrade> upgradeHolder = level().registryAccess().holderOrThrow(ResourceKey.create(LTXIRegistries.Keys.EQUIPMENT_UPGRADES, upgradeId));

            int rank = currentUpgrades.getUpgradeRank(upgradeHolder);
            if (rank > 0)
            {
                EquipmentUpgrades newUpgrades = currentUpgrades.toMutableContainer().remove(upgradeHolder).toImmutable();
                equipmentItem.setUpgrades(equipmentStack, newUpgrades);
                moduleSourceInventory.setStackInSlot(EQUIPMENT_ITEM_SLOT, equipmentStack);

                ejectModuleItem(sender, upgradeHolder, rank);
                sendSoundToPlayer(sender, LTXISounds.UPGRADE_REMOVE, 1f, 1f);
            }
        }
    }

    @Override
    protected ItemStack createModuleItem(Holder<EquipmentUpgrade> upgrade, int upgradeRank)
    {
        return EquipmentUpgradeModuleItem.createStack(upgrade, upgradeRank);
    }
}