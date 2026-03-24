package liedge.ltxindustries.entity;

import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EquipmentRocketEntity extends BaseRocketEntity
{
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
    protected void hurtTarget(ServerLevel level, Entity targetEntity, @Nullable LivingEntity owner, Vec3 hitLocation, boolean isDirectHit)
    {
        double baseDamage = isDirectHit ? LTXIWeaponsConfig.ROCKET_LAUNCHER_BASE_IMPACT_DAMAGE.getAsDouble() : LTXIWeaponsConfig.ROCKET_LAUNCHER_BASE_SPLASH_DAMAGE.getAsDouble();
        LTXIItems.ROCKET_LAUNCHER.get().causeProjectileDamage(level, targetEntity, this, owner, LTXIDamageTypes.EXPLOSIVE_WEAPON, getWeaponItem(), baseDamage);
    }
}