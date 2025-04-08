package liedge.limatech.item.weapon;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShotgunWeaponItem extends SemiAutoWeaponItem
{
    public ShotgunWeaponItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean canFocusReticle(ItemStack heldItem, Player player, AbstractWeaponControls controls)
    {
        return false;
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LimaTechWeaponsConfig.SHOTGUN_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemStack stack)
    {
        return LimaTechWeaponsConfig.SHOTGUN_ENERGY_AMMO_COST.getAsInt();
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        if (!level.isClientSide())
        {
            Object2IntMap<Entity> pelletHits = new Object2IntOpenHashMap<>();

            for (int i = 0; i < 7; i++)
            {
                CompoundHitResult hitResult = CompoundHitResult.tracePath(level, player, 10d, 6.5d, hit -> hit.getBoundingBox().getSize() <= 1d ? 0.75d : 0.375d, 5);
                hitResult.entityHits().forEach(hit -> pelletHits.mergeInt(hit.getEntity(), 1, Integer::sum));
                LimaNetworkUtil.spawnAlwaysVisibleParticle(level, LimaTechParticles.LIGHTFRAG_TRACER, hitResult.origin(), hitResult.impact().getLocation());
            }

            final double basePelletDamage = LimaTechWeaponsConfig.SHOTGUN_BASE_PELLET_DAMAGE.getAsDouble();
            EquipmentUpgrades upgrades = getUpgrades(heldItem);

            pelletHits.forEach((hitEntity, pellets) -> causeInstantDamage(upgrades, player, hitEntity, basePelletDamage * pellets));
            level.gameEvent(player, LimaTechGameEvents.WEAPON_FIRED, player.getEyePosition());
        }

        level.playSound(player, player, LimaTechSounds.SHOTGUN_FIRE.get(), SoundSource.PLAYERS, 2.0f, Mth.randomBetween(player.getRandom(), 0.9f, 1f));
    }

    @Override
    public Item getAmmoItem(ItemStack stack)
    {
        return LimaTechItems.SPECIALIST_AMMO_CANISTER.asItem();
    }

    @Override
    public int getAmmoCapacity(ItemStack stack)
    {
        return 5;
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 12;
    }

    @Override
    public int getReloadSpeed(ItemStack stack)
    {
        return 30;
    }
}