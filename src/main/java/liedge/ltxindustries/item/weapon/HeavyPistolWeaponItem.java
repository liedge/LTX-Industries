package liedge.ltxindustries.item.weapon;

import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HeavyPistolWeaponItem extends SemiAutoWeaponItem
{
    public HeavyPistolWeaponItem(Properties properties)
    {
        super(properties, 7, 25, 30, LTXIItems.HEAVY_WEAPON_ENERGY, 100, 1d);
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        double inaccuracy = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND) ? 0.15d : 2d;
        traceLightfrag(heldItem, player, level, LTXIWeaponsConfig.HEAVY_PISTOL_BASE_DAMAGE.getAsDouble(), inaccuracy, 0.25d);
        level.playSound(player, player, LTXISounds.HEAVY_PISTOL_FIRE.get(), SoundSource.PLAYERS, 1.0f, 0.75f + (player.getRandom().nextFloat() * 0.2f));
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LTXIWeaponsConfig.HEAVY_PISTOL_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemStack stack)
    {
        return LTXIWeaponsConfig.HEAVY_PISTOL_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 13;
    }
}