package liedge.ltxindustries.entity;

import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;

public class EquipmentRocketEntity extends BaseRocketEntity implements IEntityWithComplexSpawn
{
    private int color = -1;

    public EquipmentRocketEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public EquipmentRocketEntity(Level level, ItemStack launcherItem)
    {
        this(LTXIEntities.DAYBREAK_ROCKET.get(), level);
        setWeaponItem(launcherItem.copy());
    }

    @Override
    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    @Override
    protected void hurtTarget(ServerLevel level, Entity targetEntity, @Nullable LivingEntity owner, Vec3 hitLocation, boolean isDirectHit)
    {
        double baseDamage = isDirectHit ? LTXIWeaponsConfig.DAYBREAK_BASE_IMPACT_DAMAGE.getAsDouble() : LTXIWeaponsConfig.DAYBREAK_BASE_SPLASH_DAMAGE.getAsDouble();
        LTXIItems.DAYBREAK.get().causeProjectileDamage(level, targetEntity, this, owner, LTXIDamageTypes.EXPLOSIVE_WEAPON, getWeaponItem(), baseDamage);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf net)
    {
        net.writeVarInt(color);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf net)
    {
        this.color = net.readVarInt();
    }
}