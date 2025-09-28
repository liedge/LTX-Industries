package liedge.ltxindustries.entity;

import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.blockentity.RocketTurretBlockEntity;
import liedge.ltxindustries.entity.damage.TurretDamageSource;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TurretRocketEntity extends BaseRocketEntity
{
    private MachineUpgrades upgrades = MachineUpgrades.EMPTY;
    private @Nullable BlockPos turretPos;

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
    public MachineUpgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    protected void damageTarget(Level level, @Nullable LivingEntity owner, Entity targetEntity, Vec3 hitLocation, boolean isDirectHit)
    {
        float baseDamage = (float) LTXIMachinesConfig.ATMOS_TURRET_ROCKET_DAMAGE.getAsDouble();

        RocketTurretBlockEntity be = turretPos != null ? LimaBlockUtil.getSafeBlockEntity(level, turretPos, RocketTurretBlockEntity.class) : null;
        if (be != null)
        {
            // Auto-wrap player owners with a Fake Player instance that has the turret's enchantments
            LTXIEntityUtil.hurtWithEnchantedFakePlayer((ServerLevel) level, targetEntity, owner, be.getUpgrades(), le -> TurretDamageSource.create(level, LTXIDamageTypes.TURRET_ROCKET, be, this, le, null), baseDamage);
        } else
        {
            targetEntity.hurt(level.damageSources().source(LTXIDamageTypes.TURRET_ROCKET, this, owner), baseDamage); // Standard damage source if parent turret is missing/invalid
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        upgrades = readMachineUpgrades(tag);
        if (tag.contains("turret_pos"))
            turretPos = LimaNbtUtil.strictDecode(BlockPos.CODEC, NbtOps.INSTANCE, tag, "turret_pos");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        writeMachineUpgrades(upgrades, tag);
        if (turretPos != null)
            tag.put("turret_pos", LimaCoreCodecs.strictEncode(BlockPos.CODEC, NbtOps.INSTANCE, turretPos));
    }
}
