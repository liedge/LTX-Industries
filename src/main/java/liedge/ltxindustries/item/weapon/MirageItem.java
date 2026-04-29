package liedge.ltxindustries.item.weapon;

import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import liedge.ltxindustries.registry.bootstrap.LTXIUpgrades;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MirageItem extends FullAutoWeaponItem implements ScopingWeaponItem
{
    public MirageItem(Properties properties)
    {
        super(properties, 30, 45d, 30, LTXIItems.LIGHTWEIGHT_WEAPON_ENERGY);
    }

    @Override
    public @Nullable ResourceKey<Upgrade> getDefaultUpgradeKey()
    {
        return LTXIUpgrades.MIRAGE_DEFAULT;
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, LTXIExtendedInput controls)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            double inaccuracy = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND) ? 0.15d : 3d;
            traceLightfrag(serverLevel, player, heldItem, LTXIWeaponsConfig.MIRAGE_BASE_DAMAGE.getAsDouble(), inaccuracy, 0.1d);
        }

        level.playSound(player, player, LTXISounds.MIRAGE_FIRE.get(), SoundSource.PLAYERS, 2f, 1f);
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 1;
    }

    @Override
    public float getBaseScopingFOV()
    {
        return 0.85f;
    }

    @Override
    public double getMouseSensitivityModifier()
    {
        return 0.9f;
    }
}