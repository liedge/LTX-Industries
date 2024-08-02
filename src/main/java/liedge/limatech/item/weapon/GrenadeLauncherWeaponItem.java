package liedge.limatech.item.weapon;

import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import liedge.limatech.entity.OrbGrenadeEntity;
import liedge.limatech.item.ScrollModeSwitchItem;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechSounds;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static liedge.limatech.registry.LimaTechDataComponents.GRENADE_ELEMENT;

public class GrenadeLauncherWeaponItem extends SemiAutoWeaponItem implements ScrollModeSwitchItem
{
    public static final Translatable ORB_ELEMENT_TOOLTIP = LimaTech.RESOURCES.translationHolder("tooltip.{}.selected_orb_element");

    public GrenadeLauncherWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level)
    {
        if (!level.isClientSide())
        {
            OrbGrenadeEntity grenade = new OrbGrenadeEntity(level, OrbGrenadeElement.getFromItem(heldItem));
            grenade.setOwner(player);
            grenade.aimAndSetPosFromShooter(player, 1.75d, 0.5d);
            level.addFreshEntity(grenade);
        }

        level.playSound(player, player, LimaTechSounds.GRENADE_LAUNCHER_FIRE.get(), SoundSource.PLAYERS, 1.5f, Mth.randomBetween(level.random, 0.7f, 0.825f));
    }

    @Override
    public Item getAmmoItem()
    {
        return LimaTechItems.EXPLOSIVES_AMMO_CANISTER.asItem();
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 15;
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 6;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 40;
    }

    @Override
    public int getRecoilA(ItemStack stack)
    {
        return 6;
    }

    @Override
    public int getRecoilB(ItemStack stack)
    {
        return 10;
    }

    @Override
    public void switchItemMode(ItemStack stack, Player player, int delta)
    {
        boolean forward = delta == 1;

        OrbGrenadeElement element = OrbGrenadeElement.getFromItem(stack);
        element = forward ? element.next() : element.previous();
        stack.set(GRENADE_ELEMENT, element);
    }

    @Override
    public void appendTooltipHintLines(@Nullable Level level, ItemStack stack, Consumer<FormattedText> consumer)
    {
        consumer.accept(ORB_ELEMENT_TOOLTIP.translateArgs(OrbGrenadeElement.getFromItem(stack).translate()));
        super.appendTooltipHintLines(level, stack, consumer);
    }
}