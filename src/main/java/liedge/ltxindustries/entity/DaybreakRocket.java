package liedge.ltxindustries.entity;

import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.data.LightColors;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;

public class DaybreakRocket extends BaseRocketEntity implements IEntityWithComplexSpawn
{
    private int color = -1;

    public DaybreakRocket(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public DaybreakRocket(Level level, ItemStack weaponItem)
    {
        this(LTXIEntities.DAYBREAK_ROCKET.get(), level);
        setWeaponItem(weaponItem.copy());
        this.color = UpgradableEquipmentItem.getLightColorFrom(weaponItem, LightColors.Channel.ENERGY);
    }

    @Override
    public int getColor()
    {
        return color;
    }

    @Override
    protected void hurtTarget(ServerLevel level, Entity targetEntity, @Nullable LivingEntity owner, Vec3 hitLocation, boolean isDirectHit)
    {
        double baseDamage = isDirectHit ? LTXIWeaponsConfig.DAYBREAK_BASE_IMPACT_DAMAGE.getAsDouble() : LTXIWeaponsConfig.DAYBREAK_BASE_SPLASH_DAMAGE.getAsDouble();
        hurtEntity(level, LTXIDamageTypes.EXPLOSIVE, targetEntity, owner, baseDamage);
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

    @Override
    protected void readAdditionalSaveData(ValueInput input)
    {
        super.readAdditionalSaveData(input);
        this.color = input.getIntOr(LTXIConstants.KEY_COLOR, -1);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output)
    {
        super.addAdditionalSaveData(output);
        output.putInt(LTXIConstants.KEY_COLOR, this.color);
    }
}