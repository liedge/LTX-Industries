package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
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

public class MachineUpgradeMenu extends UpgradesConfigMenu<LTXIMachineBlockEntity>
{
    public static final int BACK_BUTTON_ID = 1;

    public MachineUpgradeMenu(LimaMenuType<LTXIMachineBlockEntity, ?> type, int containerId, Inventory inventory, LTXIMachineBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext, menuContext.getItemsOrThrow(BlockContentsType.AUXILIARY), LTXIMachineBlockEntity.AUX_MODULE_ITEM_SLOT);

        addPlayerInventoryAndHotbar(15, 118);
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleUnitAction(BACK_BUTTON_ID, menuContext::returnToPrimaryMenuScreen);
    }

    @Override
    protected Upgrades getUpgrades()
    {
        return menuContext.getUpgrades();
    }

    @Override
    protected boolean canInstallUpgrade(ItemStack upgradeModuleItem)
    {
        UpgradeEntry entry = upgradeModuleItem.get(LTXIDataComponents.UPGRADE_ENTRY);
        return entry != null && getUpgrades().canInstallUpgrade(menuContext, entry);
    }

    @Override
    protected boolean installUpgrade(ServerLevel level, ItemStack moduleItem)
    {
        Upgrades currentUpgrades = getUpgrades();
        UpgradeEntry moduleEntry = moduleItem.get(LTXIDataComponents.UPGRADE_ENTRY);

        if (moduleEntry != null && currentUpgrades.canInstallUpgrade(menuContext, moduleEntry))
        {
            int previousRank = currentUpgrades.getUpgradeRank(moduleEntry.upgrade());

            Upgrades newUpgrades = currentUpgrades.mutable().set(moduleEntry).build();
            menuContext.setUpgrades(newUpgrades);

            if (previousRank > 0) ejectModuleItem(getServerUser(), moduleEntry.upgrade(), previousRank, false);

            sendSoundToPlayer(getServerUser(), LTXISounds.UPGRADE_INSTALL, 1f, 1f);

            return true;
        }

        return false;
    }

    @Override
    protected void tryRemoveUpgrade(ServerPlayer sender, Identifier upgradeId)
    {
        Upgrades currentUpgrades = getUpgrades();
        Holder<Upgrade> holder = level().registryAccess().holderOrThrow(ResourceKey.create(LTXIRegistries.Keys.UPGRADES, upgradeId));

        int rank = currentUpgrades.getUpgradeRank(holder);
        if (rank > 0)
        {
            Upgrades newUpgrades = currentUpgrades.mutable().remove(holder).build();
            menuContext.setUpgrades(newUpgrades);

            ejectModuleItem(sender, holder, rank, true);

            sendSoundToPlayer(sender, LTXISounds.UPGRADE_REMOVE, 1f, 1f);
        }
    }
}