package liedge.ltxindustries.item.weapon;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.OrderedEnum;
import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.entity.OrbGrenadeEntity;
import liedge.ltxindustries.item.ScrollModeSwitchItem;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import liedge.ltxindustries.registry.game.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.Set;

import static liedge.ltxindustries.registry.game.LTXIDataComponents.GRENADE_TYPE;

public class GrenadeLauncherItem extends SemiAutoWeaponItem implements ScrollModeSwitchItem
{
    public static final Translatable GRENADE_TYPE_TOOLTIP = LTXILangKeys.tooltip("equipped_grenade");

    public static GrenadeType getGrenadeTypeFromItem(ItemStack stack)
    {
        return stack.getOrDefault(GRENADE_TYPE, GrenadeType.EXPLOSIVE);
    }

    public GrenadeLauncherItem(Properties properties)
    {
        super(properties, 6, 1.5d, 50, LTXIItems.EXPLOSIVES_WEAPON_ENERGY, 1, 0);
    }

    public void setGrenadeType(ItemStack stack, GrenadeType grenadeType)
    {
        stack.set(GRENADE_TYPE, grenadeType);
    }

    /**
     * Creates a stack meant for use in upgrade icons or creative tabs.
     * @param grenadeType The grenade type equipped
     * @return The item stack
     */
    public ItemStack createDecorativeStack(GrenadeType grenadeType)
    {
        ItemStack stack = new ItemStack(this);
        setAmmoLoadedMax(stack);
        setGrenadeType(stack, grenadeType);

        return stack;
    }

    @Override
    public boolean canFocusReticle(ItemStack heldItem, Player player, AbstractWeaponControls controls)
    {
        return false;
    }

    @Override
    public void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        super.onUpgradeRefresh(context, stack, upgrades);
        GrenadeType currentlyEquipped = getGrenadeTypeFromItem(stack);
        boolean shouldReset = upgrades.effectFlatStream(LTXIUpgradeEffectComponents.GRENADE_UNLOCK.get()).noneMatch(currentlyEquipped::equals);
        if (shouldReset) setGrenadeType(stack, GrenadeType.EXPLOSIVE);
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        if (!level.isClientSide())
        {
            OrbGrenadeEntity grenade = new OrbGrenadeEntity(level, getGrenadeTypeFromItem(heldItem), heldItem);
            grenade.setOwner(player);
            grenade.aimAndSetPosFromShooter(player, Math.min(heldItem.getOrDefault(LTXIDataComponents.WEAPON_RANGE, getWeaponRange(heldItem)), MAX_PROJECTILE_SPEED), 0d);
            level.addFreshEntity(grenade);
            level.gameEvent(player, LTXIGameEvents.WEAPON_FIRED, player.getEyePosition());
        }

        level.playSound(player, player, LTXISounds.GRENADE_LAUNCHER_FIRE.get(), SoundSource.PLAYERS, 2f, Mth.randomBetween(level.random, 0.9f, 1.1f));
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 15;
    }

    @Override
    public void switchItemMode(ItemStack stack, Player player, int delta)
    {
        final boolean forward = delta == 1;

        EquipmentUpgrades upgrades = getUpgrades(stack);
        Set<GrenadeType> availableTypes = new ObjectOpenHashSet<>();
        availableTypes.add(GrenadeType.EXPLOSIVE); // Always allow equipping explosive shells
        upgrades.forEachEffect(LTXIUpgradeEffectComponents.GRENADE_UNLOCK, (effect, rank) -> availableTypes.add(effect));

        GrenadeType currentType = GrenadeLauncherItem.getGrenadeTypeFromItem(stack);
        GrenadeType toSwitch = forward ? OrderedEnum.nextAvailable(availableTypes, currentType) : OrderedEnum.previousAvailable(availableTypes, currentType);
        if (!currentType.equals(toSwitch))
        {
            setGrenadeType(stack, toSwitch);
            player.level().playSound(null, player, LTXISounds.WEAPON_MODE_SWITCH.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    @Override
    public void addAdditionalToCreativeTab(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output, CreativeModeTab.TabVisibility tabVisibility)
    {
        HolderLookup.Provider registries = parameters.holders();
        ItemStack stack = new ItemStack(this);
        setUpgrades(stack, EquipmentUpgrades.builder()
                .set(registries.holderOrThrow(LTXIEquipmentUpgrades.OMNI_GRENADE_CORE))
                .toImmutable());
        setAmmoLoadedMax(stack);
        output.accept(stack, tabVisibility);
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        consumer.accept(GRENADE_TYPE_TOOLTIP.translateArgs(getGrenadeTypeFromItem(stack).translate()));
        super.appendTooltipHintComponents(level, stack, consumer);
    }
}