package liedge.ltxindustries.blockentity.turret;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.entity.LTXIEntityUtil;
import liedge.ltxindustries.entity.damage.TurretDamageSource;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RailgunTurretBlockEntity extends SemiAutoTurretBlockEntity
{
    public RailgunTurretBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.RAILGUN_TURRET.get(), pos, state, 1.75d, 25, 25);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.RAILGUN_TURRET_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.RAILGUN_TURRET_ENERGY_PER_TARGET.getAsInt();
    }

    @Override
    protected int getMaxTargetsPerSearch()
    {
        return 1;
    }

    @Override
    protected int getSearchInterval()
    {
        return 80;
    }

    @Override
    protected int getChargingDuration()
    {
        return 30;
    }

    @Override
    protected boolean isValidDefaultTarget(ServerLevel level, Entity targetEntity)
    {
        return targetEntity.is(LTXITags.EntityTypes.HIGH_THREAT_TARGETS);
    }

    @Override
    protected void attackTarget(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player owner, Entity target)
    {
        float baseDamage = (float) LTXIMachinesConfig.RAILGUN_TURRET_DAMAGE.getAsDouble();

        LTXIEntityUtil.hurtWithEnchantedFakePlayer(level, target, owner, getUpgrades(), ignored -> TurretDamageSource.create(level, LTXIDamageTypes.RAILGUN_TURRET, this, null, owner, traceStart), baseDamage);
        LimaNetworkUtil.sendParticle(level, new ColorParticleOptions(LTXIParticles.RAILGUN_BOLT, LTXIConstants.LIME_GREEN), LimaNetworkUtil.LONG_PARTICLE_DIST, traceStart, target.getBoundingBox().getCenter());
        level.playSound(null, traceStart.x, traceStart.y, traceStart.z, LTXISounds.RAILGUN_TURRET_FIRE, SoundSource.BLOCKS, 2.5f, Mth.randomBetween(level.getRandom(), 0.85f, 0.95f));
    }
}