package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.blockentity.UpgradeStationBlockEntity;
import liedge.ltxindustries.item.EquipmentUpgradeModuleItem;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgradeEntry;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import static liedge.ltxindustries.blockentity.UpgradeStationBlockEntity.EQUIPMENT_ITEM_SLOT;
import static liedge.ltxindustries.blockentity.UpgradeStationBlockEntity.UPGRADE_MODULE_SLOT;
import static liedge.ltxindustries.registry.game.LTXIDataComponents.EQUIPMENT_UPGRADE_ENTRY;

public class UpgradeStationMenu extends UpgradesConfigMenu<UpgradeStationBlockEntity, EquipmentUpgrade, EquipmentUpgrades>
{
    public UpgradeStationMenu(LimaMenuType<UpgradeStationBlockEntity, ?> type, int containerId, Inventory inventory, UpgradeStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, menuContext.getItemsOrThrow(BlockContentsType.GENERAL), UPGRADE_MODULE_SLOT);

        addSlot(BlockContentsType.GENERAL, EQUIPMENT_ITEM_SLOT, 24, 65);
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
        return UpgradableEquipmentItem.getUpgradesFrom(moduleSourceInventory.getResource(EQUIPMENT_ITEM_SLOT));
    }

    @Override
    protected boolean canInstallUpgrade(ItemStack upgradeModuleItem)
    {
        ItemStack equipmentStack = moduleSourceInventory.getResource(EQUIPMENT_ITEM_SLOT).toStack();

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
        ItemStack equipmentStack = moduleSourceInventory.getResource(EQUIPMENT_ITEM_SLOT).toStack();

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
                setUpgradesAndRefresh(level, equipmentStack, equipmentItem, newUpgrades);

                moduleSourceInventory.set(EQUIPMENT_ITEM_SLOT, ItemResource.of(equipmentStack), 1);

                try (Transaction tx = Transaction.openRoot())
                {
                    moduleSourceInventory.extract(UPGRADE_MODULE_SLOT, ItemResource.of(upgradeModuleItem), 1, tx);
                    tx.commit();
                }

                if (previousRank > 0) ejectModuleItem(getServerUser(), entry.upgrade(), previousRank);

                sendSoundToPlayer(getServerUser(), LTXISounds.UPGRADE_INSTALL, 1f, 1f);
            }
        }
    }

    @Override
    protected void tryRemoveUpgrade(ServerPlayer sender, Identifier upgradeId)
    {
        ItemStack equipmentStack = moduleSourceInventory.getResource(EQUIPMENT_ITEM_SLOT).toStack();

        if (equipmentStack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            EquipmentUpgrades currentUpgrades = equipmentItem.getUpgrades(equipmentStack);
            Holder<EquipmentUpgrade> upgradeHolder = level().registryAccess().holderOrThrow(ResourceKey.create(LTXIRegistries.Keys.EQUIPMENT_UPGRADES, upgradeId));

            int rank = currentUpgrades.getUpgradeRank(upgradeHolder);
            if (rank > 0)
            {
                EquipmentUpgrades newUpgrades = currentUpgrades.toMutableContainer().remove(upgradeHolder).toImmutable();
                setUpgradesAndRefresh(sender.level(), equipmentStack, equipmentItem, newUpgrades);
                moduleSourceInventory.set(EQUIPMENT_ITEM_SLOT, ItemResource.of(equipmentStack), 1);

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

    private void setUpgradesAndRefresh(ServerLevel level, ItemStack stack, UpgradableEquipmentItem equipmentItem, EquipmentUpgrades newUpgrades)
    {
        equipmentItem.setUpgrades(stack, newUpgrades);
        equipmentItem.onUpgradeRefresh(LimaLootUtil.emptyLootContext(level), stack, newUpgrades);
    }
}