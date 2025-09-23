package liedge.ltxindustries.item.weapon;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import liedge.ltxindustries.entity.CompoundHitResult;
import liedge.ltxindustries.entity.DynamicClipContext;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIGameEvents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ShotgunWeaponItem extends SemiAutoWeaponItem
{
    public ShotgunWeaponItem(Properties properties)
    {
        super(properties, 5, 10d, 40, LTXIItems.SPECIALIST_WEAPON_ENERGY, 5, 0.33d);
    }

    @Override
    public @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return LTXIEquipmentUpgrades.SHOTGUN_DEFAULT;
    }

    @Override
    public boolean canFocusReticle(ItemStack heldItem, Player player, AbstractWeaponControls controls)
    {
        return false;
    }

    @Override
    public void weaponFired(ItemStack heldItem, Player player, Level level, AbstractWeaponControls controls)
    {
        if (!level.isClientSide())
        {
            Object2IntMap<Entity> pelletHits = new Object2IntOpenHashMap<>();

            for (int i = 0; i < 7; i++)
            {
                CompoundHitResult hitResult = CompoundHitResult.tracePath(level, player, getWeaponRange(heldItem), 6.5d, getEntityMaxHits(heldItem), getBlockPierceDistance(heldItem), DynamicClipContext.FluidCollisionPredicate.NONE,
                        hit -> hit.getBoundingBox().getSize() <= 1d ? 0.75d : 0.375d);
                hitResult.entityHits().forEach(hit -> pelletHits.mergeInt(hit.getEntity(), 1, Integer::sum));

                sendTracerParticle(level, hitResult.origin(), hitResult.impactLocation());
            }

            final double basePelletDamage = LTXIWeaponsConfig.SHOTGUN_BASE_PELLET_DAMAGE.getAsDouble();
            EquipmentUpgrades upgrades = getUpgrades(heldItem);

            pelletHits.forEach((hitEntity, pellets) -> causeLightfragDamage(upgrades, player, hitEntity, basePelletDamage * pellets));
            level.gameEvent(player, LTXIGameEvents.WEAPON_FIRED, player.getEyePosition());
        }

        level.playSound(player, player, LTXISounds.SHOTGUN_FIRE.get(), SoundSource.PLAYERS, 2.0f, Mth.randomBetween(player.getRandom(), 0.9f, 1f));
    }

    @Override
    public int getFireRate(ItemStack stack)
    {
        return 10;
    }
}