package liedge.ltxindustries.item.weapon;

import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.entity.CompoundHitResult;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MagnumWeaponItem extends SemiAutoWeaponItem
{
    public MagnumWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        if (!level.isClientSide())
        {
            double inaccuracy = LimaEntityUtil.isEntityUsingItem(player, InteractionHand.MAIN_HAND) ? 0.15d : 2d;
            CompoundHitResult hitResult = CompoundHitResult.tracePath(level, player, 25d, inaccuracy, 0.25d, 1000);
            EquipmentUpgrades upgrades = getUpgrades(heldItem);

            hitResult.entityHits().forEach(hit -> causeInstantDamage(upgrades, player, hit.getEntity(), LTXIWeaponsConfig.MAGNUM_BASE_DAMAGE.getAsDouble()));
            level.gameEvent(player, LTXIGameEvents.WEAPON_FIRED, player.getEyePosition());

            sendTracerParticle(level, hitResult.origin(), hitResult.impactLocation());
        }

        level.playSound(player, player, LTXISounds.MAGNUM_FIRE.get(), SoundSource.PLAYERS, 1.0f, 0.75f + (player.getRandom().nextFloat() * 0.2f));
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LTXIWeaponsConfig.MAGNUM_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemStack stack)
    {
        return LTXIWeaponsConfig.MAGNUM_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public Item getAmmoItem(ItemStack stack)
    {
        return LTXIItems.HEAVY_AMMO_CANISTER.asItem();
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 7;
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 13;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 30;
    }
}