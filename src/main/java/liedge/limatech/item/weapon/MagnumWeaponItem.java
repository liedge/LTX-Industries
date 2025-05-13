package liedge.limatech.item.weapon;

import liedge.limacore.util.LimaEntityUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.entity.CompoundHitResult;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.lib.weapons.AbstractWeaponControls;
import liedge.limatech.registry.game.LimaTechGameEvents;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.registry.game.LimaTechParticles;
import liedge.limatech.registry.game.LimaTechSounds;
import liedge.limatech.util.config.LimaTechWeaponsConfig;
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

            hitResult.entityHits().forEach(hit -> causeInstantDamage(upgrades, player, hit.getEntity(), LimaTechWeaponsConfig.MAGNUM_BASE_DAMAGE.getAsDouble()));
            level.gameEvent(player, LimaTechGameEvents.WEAPON_FIRED, player.getEyePosition());

            LimaNetworkUtil.sendSingleParticle(level, LimaTechParticles.LIGHTFRAG_TRACER, player, true, LimaNetworkUtil.LONG_PARTICLE_DIST, hitResult.origin(), hitResult.impact().getLocation());
        }

        level.playSound(player, player, LimaTechSounds.MAGNUM_FIRE.get(), SoundSource.PLAYERS, 1.0f, 0.75f + (player.getRandom().nextFloat() * 0.2f));
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LimaTechWeaponsConfig.MAGNUM_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemStack stack)
    {
        return LimaTechWeaponsConfig.MAGNUM_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public Item getAmmoItem(ItemStack stack)
    {
        return LimaTechItems.MAGNUM_AMMO_CANISTER.asItem();
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