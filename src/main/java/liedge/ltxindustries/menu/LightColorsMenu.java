package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.network.sync.SimpleValueTracker;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.ltxindustries.blockentity.UpgradeStationBlockEntity;
import liedge.ltxindustries.data.LightChannels;
import liedge.ltxindustries.data.LightColors;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class LightColorsMenu extends BlockEntityMenu<UpgradeStationBlockEntity>
{
    private final LimaBlockEntityItems generalInventory;

    private LightColors lightColors = LightColors.EMPTY;
    private LightChannels lightChannels = LightChannels.NONE;
    private boolean updateScreen;

    public LightColorsMenu(LimaMenuType<UpgradeStationBlockEntity, ?> type, int containerId, Inventory inventory, UpgradeStationBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        this.generalInventory = menuContext.getItemsOrThrow(BlockContentsType.GENERAL);

        addPlayerInventoryAndHotbar(DEFAULT_INV_X, 88);
    }

    @Override
    public boolean stillValid(Player player)
    {
        return super.stillValid(player) && menuContext.hasValidItem();
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(SimpleValueTracker.create(LTXINetworkSerializers.LIGHT_COLORS, this::updateLightColors, this::setLightColors).setAutomatic());
        collector.register(SimpleValueTracker.create(LTXINetworkSerializers.LIGHT_CHANNELS, this::updateLightChannels, this::setLightChannels).setAutomatic());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(SharedMenuButtons.EXIT_SUB_MENU, menuContext::returnToPrimaryMenuScreen);

        builder.handleAction(0, LTXINetworkSerializers.LIGHT_COLORS, (_, colors) ->
        {
            boolean valid = !lightColors.equals(colors) && colors.getMap().keySet().stream().allMatch(lightChannels::contains);
            if (valid)
            {
                ItemStack stack = getEquipmentResource().toStack();
                stack.set(LTXIDataComponents.LIGHT_COLORS, colors);
                generalInventory.set(UpgradeStationBlockEntity.EQUIPMENT_ITEM_SLOT, ItemResource.of(stack), 1);
            }
        });
    }

    public LightColors getLightColors()
    {
        return lightColors;
    }

    public LightChannels getLightChannels()
    {
        return lightChannels;
    }

    public boolean shouldUpdateScreen()
    {
        if (updateScreen)
        {
            updateScreen = false;
            return true;
        }

        return false;
    }

    private void setLightColors(LightColors lightColors)
    {
        this.lightColors = lightColors;
        updateScreen = true;
    }

    private void setLightChannels(LightChannels lightChannels)
    {
        this.lightChannels = lightChannels;
        updateScreen = true;
    }

    private LightColors updateLightColors()
    {
        this.lightColors = getEquipmentResource().getOrDefault(LTXIDataComponents.LIGHT_COLORS, LightColors.EMPTY);
        return lightColors;
    }

    private LightChannels updateLightChannels()
    {
        this.lightChannels = getEquipmentResource().getOrDefault(LTXIDataComponents.LIGHT_CHANNELS, LightChannels.NONE);
        return lightChannels;
    }

    private ItemResource getEquipmentResource()
    {
        return generalInventory.getResource(UpgradeStationBlockEntity.EQUIPMENT_ITEM_SLOT);
    }
}