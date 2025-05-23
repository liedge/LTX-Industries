package liedge.limatech.blockentity;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.LimaTechTags;
import liedge.limatech.entity.LimaTechEntityUtil;
import liedge.limatech.entity.damage.TurretDamageSource;
import liedge.limatech.lib.TurretTargetList;
import liedge.limatech.registry.bootstrap.LimaTechDamageTypes;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import liedge.limatech.registry.game.LimaTechMenus;
import liedge.limatech.registry.game.LimaTechParticles;
import liedge.limatech.registry.game.LimaTechSounds;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RailgunTurretBlockEntity extends BaseTurretBlockEntity
{
    public RailgunTurretBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.RAILGUN_TURRET.get(), pos, state, 1.75d, 25, 25, 25);
    }

    @Override
    protected int getEnergyPerTarget()
    {
        return LimaTechMachinesConfig.NOCTIS_TURRET_ENERGY_PER_TARGET.getAsInt();
    }

    @Override
    protected int getTargetScanTime()
    {
        return 80;
    }

    @Override
    protected int getMaxTargetsPerScan()
    {
        return 1;
    }

    @Override
    protected int getFiringSequenceDelay()
    {
        return 40;
    }

    @Override
    protected boolean isValidTarget(Entity entity)
    {
        return entity.getType().is(LimaTechTags.EntityTypes.HIGH_THREAT_TARGETS);
    }

    @Override
    protected void serverTargetFiringTick(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player owner, Entity target, TurretTargetList targetList)
    {
        if (ticker >= 10)
        {
            Vec3 start = getProjectileStart();
            float baseDamage = (float) LimaTechMachinesConfig.NOCTIS_TURRET_DAMAGE.getAsDouble();

            LimaTechEntityUtil.hurtWithEnchantedFakePlayer(level, target, owner, getUpgrades(), ignored -> TurretDamageSource.create(level, LimaTechDamageTypes.RAILGUN_TURRET, this, null, owner, start), baseDamage);

            LimaNetworkUtil.sendParticle(level, new ColorParticleOptions(LimaTechParticles.RAILGUN_BOLT, LimaTechConstants.LIME_GREEN), LimaNetworkUtil.LONG_PARTICLE_DIST, start, target.getBoundingBox().getCenter());
            level.playSound(null, start.x ,start.y, start.z, LimaTechSounds.RAILGUN_BOOM.get(), SoundSource.BLOCKS, 2.5f, Mth.randomBetween(level.random, 0.85f, 0.95f));

            targetList.removeTarget(target);
            currentTarget = null;
            ticker = 0;
        }
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LimaTechMachinesConfig.NOCTIS_TURRET_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.RAILGUN_TURRET.get();
    }
}