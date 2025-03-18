package liedge.limatech.lib;

import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.blockentity.RocketTurretBlockEntity;
import liedge.limatech.registry.LimaTechUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

public class TurretDamageSource extends LimaDynamicDamageSource
{
    public static TurretDamageSource create(Level level, ResourceKey<DamageType> damageTypeKey, RocketTurretBlockEntity blockEntity, @Nullable Entity directEntity, @Nullable Entity owner)
    {
        Holder<DamageType> holder = level.registryAccess().holderOrThrow(damageTypeKey);
        return new TurretDamageSource(holder, blockEntity, directEntity, owner);
    }

    private final RocketTurretBlockEntity blockEntity;

    public TurretDamageSource(Holder<DamageType> type, RocketTurretBlockEntity blockEntity, @Nullable Entity directEntity, @Nullable Entity causingEntity)
    {
        super(type, directEntity, causingEntity);
        this.blockEntity = blockEntity;
    }

    public RocketTurretBlockEntity getBlockEntity()
    {
        return blockEntity;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int modifyEnchantmentLevel(LootContext context, Holder<Enchantment> enchantment, int entityLevel)
    {
        // Don't allow player enchants for turrets
        return blockEntity.getUpgrades().flatMapToInt(LimaTechUpgradeEffectComponents.ENCHANTMENT_LEVEL.get(), (effect, rank) -> enchantment.is(effect.enchantment()) ? rank : 0).sum();
    }
}