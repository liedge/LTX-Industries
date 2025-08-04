package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.NetworkSerializer;
import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
import liedge.ltxindustries.item.MachineUpgradeModuleItem;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgradeEntry;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
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

import static liedge.ltxindustries.registry.game.LTXIDataComponents.MACHINE_UPGRADE_ENTRY;

public class MachineUpgradeMenu extends UpgradesConfigMenu<LTXIMachineBlockEntity, MachineUpgrade, MachineUpgrades>
{
    public static final int BACK_BUTTON_ID = 1;

    public MachineUpgradeMenu(LimaMenuType<LTXIMachineBlockEntity, ?> type, int containerId, Inventory inventory, LTXIMachineBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, menuContext.getAuxInventory(), LTXIMachineBlockEntity.AUX_MODULE_ITEM_SLOT);

        addPlayerInventoryAndHotbar(15, 118);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleUnitAction(BACK_BUTTON_ID, menuContext::returnToPrimaryMenuScreen);
    }

    @Override
    protected NetworkSerializer<MachineUpgrades> getUpgradesSerializer()
    {
        return LTXINetworkSerializers.ITEM_MACHINE_UPGRADES.get();
    }

    @Override
    protected MachineUpgrades getUpgrades()
    {
        return menuContext.getUpgrades();
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        if (stack.getItem() instanceof MachineUpgradeModuleItem && canInstallUpgrade(stack))
        {
            return quickMoveToContainerSlot(stack, 0);
        }
        else
        {
            return false;
        }
    }

    @Override
    protected boolean canInstallUpgrade(ItemStack upgradeModuleItem)
    {
        MachineUpgrades upgrades = menuContext.getUpgrades();
        MachineUpgradeEntry entry = upgradeModuleItem.get(MACHINE_UPGRADE_ENTRY);

        return entry != null && upgrades.canInstallUpgrade(menuContext, entry);
    }

    @Override
    protected void tryInstallUpgrade(ItemStack upgradeModuleItem, ServerLevel level)
    {
        MachineUpgrades currentUpgrades = menuContext.getUpgrades();
        MachineUpgradeEntry entry = upgradeModuleItem.get(MACHINE_UPGRADE_ENTRY);

        if (entry != null && currentUpgrades.canInstallUpgrade(menuContext, entry))
        {
            // Get previous rank
            int previousRank = currentUpgrades.getUpgradeRank(entry.upgrade());

            // Modify the upgrades and consume upgrade module item
            MachineUpgrades newUpgrades = currentUpgrades.toMutableContainer().set(entry).toImmutable();
            menuContext.setUpgrades(newUpgrades);
            moduleSourceInventory.extractItem(moduleSlot, 1, false);

            if (previousRank > 0) ejectModuleItem(getServerUser(), entry.upgrade(), previousRank);

            sendSoundToPlayer(getServerUser(), LTXISounds.UPGRADE_INSTALL);
        }
    }

    @Override
    protected void tryRemoveUpgrade(ServerPlayer sender, ResourceLocation upgradeId)
    {
        MachineUpgrades currentUpgrades = menuContext.getUpgrades();
        Holder<MachineUpgrade> upgradeHolder = level().registryAccess().holderOrThrow(ResourceKey.create(LTXIRegistries.Keys.MACHINE_UPGRADES, upgradeId));

        int rank = currentUpgrades.getUpgradeRank(upgradeHolder);
        if (rank > 0)
        {
            MachineUpgrades newUpgrades = currentUpgrades.toMutableContainer().remove(upgradeHolder).toImmutable();
            menuContext.setUpgrades(newUpgrades);

            ejectModuleItem(sender, upgradeHolder, rank);

            sendSoundToPlayer(sender, LTXISounds.UPGRADE_REMOVE);
        }
    }

    @Override
    protected ItemStack createModuleItem(Holder<MachineUpgrade> upgrade, int upgradeRank)
    {
        return MachineUpgradeModuleItem.createStack(upgrade, upgradeRank);
    }
}