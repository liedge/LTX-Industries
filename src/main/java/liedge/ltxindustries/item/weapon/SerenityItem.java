package liedge.ltxindustries.item.weapon;

import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
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

public class SerenityItem extends FullAutoWeaponItem
{
    public SerenityItem(Properties properties)
    {
        super(properties, 45, 12.5d, 20, LTXIItems.LIGHTWEIGHT_WEAPON_ENERGY, 1, 0);
    }

    @Override
    public @Nullable ResourceKey<Upgrade> getDefaultUpgradeKey()
    {
        return LTXIEquipmentUpgrades.SERENITY_DEFAULT;
    }

    @Override
    public boolean isOneHanded(ItemStack stack)
    {
        return true;
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, LTXIExtendedInput controls)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            double inaccuracy = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND) ? 0.25d : 4d;
            traceLightfrag(serverLevel, player, heldItem, LTXIWeaponsConfig.SERENITY_BASE_DAMAGE.getAsDouble(), inaccuracy, 0.2d);
        }

        level.playSound(player, player, LTXISounds.SERENITY_FIRE.get(), SoundSource.PLAYERS, 2f, 1f);
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 0;
    }
}