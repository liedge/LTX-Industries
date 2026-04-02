package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.blockentity.UpgradeStationBlockEntity;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.upgrades.UpgradeEntry;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

import static liedge.ltxindustries.blockentity.UpgradeStationBlockEntity.EQUIPMENT_ITEM_SLOT;
import static liedge.ltxindustries.blockentity.UpgradeStationBlockEntity.UPGRADE_MODULE_SLOT;

public class UpgradeStationMenu extends UpgradesConfigMenu<UpgradeStationBlockEntity>
{
    public UpgradeStationMenu(LimaMenuType<UpgradeStationBlockEntity, ?> type, int containerId, Inventory inventory, UpgradeStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, menuContext.getItemsOrThrow(BlockContentsType.GENERAL), UPGRADE_MODULE_SLOT);

        addSlot(BlockContentsType.GENERAL, EQUIPMENT_ITEM_SLOT, 24, 65);
        addPlayerInventoryAndHotbar(15, 118);
    }

    @Override
    protected Upgrades getUpgrades()
    {
        return UpgradableEquipmentItem.getUpgradesFrom(moduleSourceInventory.getResource(EQUIPMENT_ITEM_SLOT));
    }

    @Override
    protected boolean canInstallUpgrade(ItemStack upgradeModuleItem)
    {
        ItemStack equipmentStack = moduleSourceInventory.getResource(EQUIPMENT_ITEM_SLOT).toStack();

        if (equipmentStack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            Upgrades upgrades = equipmentItem.getUpgrades(equipmentStack);
            UpgradeEntry entry = upgradeModuleItem.get(LTXIDataComponents.UPGRADE_ENTRY);

            return entry != null && upgrades.canInstallUpgrade(equipmentStack, entry);
        }

        return false;
    }

    @Override
    protected boolean installUpgrade(ServerLevel level, ItemStack moduleItem)
    {
        ItemStack equipmentStack = moduleSourceInventory.getResource(EQUIPMENT_ITEM_SLOT).toStack();

        if (equipmentStack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            Upgrades currentUpgrades = equipmentItem.getUpgrades(equipmentStack);
            UpgradeEntry moduleEntry = moduleItem.get(LTXIDataComponents.UPGRADE_ENTRY);

            if (moduleEntry != null && currentUpgrades.canInstallUpgrade(equipmentStack, moduleEntry))
            {
                int previousRank = currentUpgrades.getUpgradeRank(moduleEntry.upgrade());

                Upgrades newUpgrades = currentUpgrades.mutable().set(moduleEntry).build();
                setUpgradesAndRefresh(level, equipmentStack, equipmentItem, newUpgrades);
                moduleSourceInventory.set(EQUIPMENT_ITEM_SLOT, ItemResource.of(equipmentStack), 1);

                if (previousRank > 0) ejectModuleItem(getServerUser(), moduleEntry.upgrade(), previousRank, false);

                sendSoundToPlayer(getServerUser(), LTXISounds.UPGRADE_INSTALL, 1f, 1f);

                return true;
            }
        }

        return false;
    }

    @Override
    protected void tryRemoveUpgrade(ServerPlayer sender, Identifier upgradeId)
    {
        ItemStack equipmentStack = moduleSourceInventory.getResource(EQUIPMENT_ITEM_SLOT).toStack();

        if (equipmentStack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            Upgrades currentUpgrades = equipmentItem.getUpgrades(equipmentStack);
            Holder<Upgrade> holder = level().registryAccess().holderOrThrow(ResourceKey.create(LTXIRegistries.Keys.UPGRADES, upgradeId));

            int rank = currentUpgrades.getUpgradeRank(holder);
            if (rank > 0)
            {
                Upgrades newUpgrades = currentUpgrades.mutable().remove(holder).build();
                setUpgradesAndRefresh(sender.level(), equipmentStack, equipmentItem, newUpgrades);
                moduleSourceInventory.set(EQUIPMENT_ITEM_SLOT, ItemResource.of(equipmentStack), 1);

                ejectModuleItem(sender, holder, rank, true);
                sendSoundToPlayer(sender, LTXISounds.UPGRADE_REMOVE, 1f, 1f);
            }
        }
    }

    private void setUpgradesAndRefresh(ServerLevel level, ItemStack stack, UpgradableEquipmentItem equipmentItem, Upgrades newUpgrades)
    {
        equipmentItem.setUpgrades(stack, newUpgrades);
        equipmentItem.onUpgradeRefresh(LimaLootUtil.emptyLootContext(level), stack, newUpgrades);
    }
}