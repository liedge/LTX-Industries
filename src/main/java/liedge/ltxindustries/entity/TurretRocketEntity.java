package liedge.ltxindustries.entity;

import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.turret.RocketTurretBlockEntity;
import liedge.ltxindustries.blockentity.turret.TurretClipContext;
import liedge.ltxindustries.entity.damage.TurretDamageSource;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TurretRocketEntity extends BaseRocketEntity
{
    private Upgrades upgrades = Upgrades.EMPTY;
    private @Nullable BlockPos turretPos;
    private @Nullable AABB turretBB;

    public TurretRocketEntity(EntityType<?> type, Level level, @Nullable BlockPos turretPos)
    {
        super(type, level);
        this.turretPos = turretPos;
    }

    public TurretRocketEntity(EntityType<?> type, Level level)
    {
        this(type, level, null);
    }

    public TurretRocketEntity(Level level, RocketTurretBlockEntity blockEntity)
    {
        this(LTXIEntities.TURRET_ROCKET.get(), level, blockEntity.getBlockPos());
        this.upgrades = blockEntity.getUpgrades();
    }

    @Override
    public Upgrades getUpgrades()
    {
        return upgrades;
    }

    private @Nullable AABB getTurretBB()
    {
        if (turretPos != null && LimaBlockUtil.getSafeBlockEntity(level(), turretPos) instanceof RocketTurretBlockEntity be)
        {
            this.turretBB = be.getBoundingBox();
        }

        return turretBB;
    }

    @Override
    protected ClipContext blockTraceContext(Vec3 start, Vec3 path)
    {
        AABB turretBB = getTurretBB();
        return turretBB != null ? new TurretClipContext(start, start.add(path), this, turretBB) : super.blockTraceContext(start, path);
    }

    @Override
    protected void hurtTarget(ServerLevel level, Entity targetEntity, @Nullable LivingEntity owner, Vec3 hitLocation, boolean isDirectHit)
    {
        float baseDamage = (float) LTXIMachinesConfig.ROCKET_TURRET_ROCKET_DAMAGE.getAsDouble();

        RocketTurretBlockEntity be = turretPos != null ? LimaBlockUtil.getSafeBlockEntity(level, turretPos, RocketTurretBlockEntity.class) : null;
        if (be != null)
        {
            // Auto-wrap player owners with a Fake Player instance that has the turret's enchantments
            LTXIEntityUtil.hurtWithEnchantedFakePlayer(level, targetEntity, owner, be.getUpgrades(), fakePlayer -> TurretDamageSource.create(level, LTXIDamageTypes.ROCKET_TURRET, be, this, fakePlayer, null), baseDamage);
        }
        else
        {
            targetEntity.hurtServer(level, level.damageSources().source(LTXIDamageTypes.ROCKET_TURRET, this, owner), baseDamage);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input)
    {
        super.readAdditionalSaveData(input);
        upgrades = input.read(LTXIConstants.KEY_UPGRADES_CONTAINER, Upgrades.CODEC).orElse(Upgrades.EMPTY);
        turretPos = input.read("turret_pos", BlockPos.CODEC).orElse(null);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output)
    {
        super.addAdditionalSaveData(output);
        output.store(LTXIConstants.KEY_UPGRADES_CONTAINER, Upgrades.CODEC, upgrades);
        output.storeNullable("turret_pos", BlockPos.CODEC, turretPos);
    }
}
