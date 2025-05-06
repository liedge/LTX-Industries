package liedge.limatech.menu;

import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.network.NetworkSerializer;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limatech.blockentity.base.UpgradableMachineBlockEntity;
import liedge.limatech.item.MachineUpgradeModuleItem;
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import liedge.limatech.lib.upgrades.machine.MachineUpgradeEntry;
import liedge.limatech.lib.upgrades.machine.MachineUpgrades;
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

import static liedge.limatech.registry.game.LimaTechDataComponents.MACHINE_UPGRADE_ENTRY;

public class MachineUpgradeMenu extends UpgradesConfigMenu<UpgradableMachineBlockEntity, MachineUpgrade, MachineUpgrades>
{
    public MachineUpgradeMenu(LimaMenuType<UpgradableMachineBlockEntity, ?> type, int containerId, Inventory inventory, UpgradableMachineBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addUpgradeInsertionSlot(0);
        addPlayerInventoryAndHotbar(15, 118);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(0, menuContext::returnToPrimaryMenuScreen);
        builder.handleAction(1, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::tryRemoveUpgrade);
    }

    @Override
    protected NetworkSerializer<MachineUpgrades> getUpgradesSerializer()
    {
        return LimaTechNetworkSerializers.ITEM_MACHINE_UPGRADES.get();
    }

    @Override
    protected MachineUpgrades getUpgradesFromContext()
    {
        return menuContext.getUpgrades();
    }

    @Override
    protected IItemHandlerModifiable menuContainer()
    {
        return menuContext.getItemHandler(1);
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

        return entry != null && upgrades.canInstallUpgrade(menuContext, entry.upgrade());
    }

    @Override
    protected void tryInstallUpgrade(ItemStack upgradeModuleItem, ServerLevel level)
    {
        MachineUpgrades currentUpgrades = menuContext.getUpgrades();
        MachineUpgradeEntry entry = upgradeModuleItem.get(MACHINE_UPGRADE_ENTRY);

        if (entry != null && currentUpgrades.canInstallUpgrade(menuContext, entry.upgrade()))
        {
            MachineUpgrades newUpgrades = currentUpgrades.toMutableContainer().set(entry).toImmutable();
            menuContext.setUpgrades(newUpgrades);
            menuContainer().extractItem(0, 1, false);
        }
    }

    @Override
    protected void tryRemoveUpgrade(ServerPlayer sender, ResourceLocation upgradeId)
    {
        ResourceKey<MachineUpgrade> upgradeKey = ResourceKey.create(LimaTechRegistries.Keys.MACHINE_UPGRADES, upgradeId);
        MachineUpgrades currentUpgrades = menuContext.getUpgrades();
        Holder<MachineUpgrade> upgradeHolder = level().registryAccess().holderOrThrow(upgradeKey);

        int upgradeRank = currentUpgrades.getUpgradeRank(upgradeHolder);
        if (upgradeRank > 0)
        {
            ItemStack upgradeModuleItem = MachineUpgradeModuleItem.createStack(upgradeHolder, upgradeRank);
            PlayerMainInvWrapper invWrapper = new PlayerMainInvWrapper(playerInventory);
            int nextSlot = LimaItemHandlerUtil.getNextEmptySlot(invWrapper);

            if (nextSlot != -1)
            {
                MachineUpgrades newUpgrades = currentUpgrades.toMutableContainer().remove(upgradeHolder).toImmutable();
                menuContext.setUpgrades(newUpgrades);
                invWrapper.insertItem(nextSlot, upgradeModuleItem, false);
            }
        }
    }
}