package liedge.ltxindustries.item.weapon;

import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class HeavyPistolItem extends SemiAutoWeaponItem
{
    public HeavyPistolItem(Properties properties)
    {
        super(properties, 7, 25, 30, LTXIItems.HEAVY_WEAPON_ENERGY, 100, 1d);
    }

    @Override
    public @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return LTXIEquipmentUpgrades.HEAVY_PISTOL_DEFAULT;
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, LTXIExtendedInput controls)
    {
        double inaccuracy = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND) ? 0.15d : 2d;
        traceLightfrag(heldItem, player, level, LTXIWeaponsConfig.HEAVY_PISTOL_BASE_DAMAGE.getAsDouble(), inaccuracy, 0.25d);
        level.playSound(player, player, LTXISounds.HEAVY_PISTOL_FIRE.get(), SoundSource.PLAYERS, 1.0f, 0.75f + (player.getRandom().nextFloat() * 0.2f));
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 13;
    }
}