package liedge.limatech.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.LimaTechTags;
import liedge.limatech.entity.BaseRocketEntity;
import liedge.limatech.lib.TurretTargetList;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import liedge.limatech.registry.game.LimaTechMenus;
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

public class RocketTurretBlockEntity extends BaseTurretBlockEntity
{
    public RocketTurretBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.ROCKET_TURRET.get(), pos, state, 1.625d, 50d, 20d, 70d);
    }

    @Override
    protected int getEnergyPerTarget()
    {
        return LimaTechMachinesConfig.ATMOS_TURRET_ENERGY_PER_TARGET.getAsInt();
    }

    @Override
    protected int getTargetScanTime()
    {
        return 100;
    }

    @Override
    protected int getMaxTargetsPerScan()
    {
        return 4;
    }

    @Override
    protected int getFiringSequenceDelay()
    {
        return 40;
    }

    @Override
    protected boolean isValidTarget(Entity entity)
    {
        return entity.getType().is(LimaTechTags.EntityTypes.FLYING_TARGETS);
    }

    @Override
    protected void serverTargetFiringTick(ServerLevel level, BlockPos pos, BlockState state, @Nullable Player owner, Entity target, TurretTargetList targetList)
    {
        // Fire rockets in quick succession
        if (ticker >= 5)
        {
            Vec3 start = getProjectileStart();
            BaseRocketEntity.TurretRocket rocket = new BaseRocketEntity.TurretRocket(level, this);
            rocket.setOwner(owner);
            rocket.setPos(start);
            rocket.aimTowardsEntity(target, 2.5f, 0);
            rocket.setTargetEntity(target);
            level.addFreshEntity(rocket);
            level.playSound(null, start.x, start.y, start.z, LimaTechSounds.ROCKET_LAUNCHER_FIRE.get(), SoundSource.BLOCKS, 1.5f, Mth.randomBetween(level.random, 0.75f, 0.9f));

            targetList.removeTarget(target);
            currentTarget = null;
            ticker = 0;
        }
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LimaTechMachinesConfig.ATMOS_TURRET_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.ROCKET_TURRET.get();
    }
}