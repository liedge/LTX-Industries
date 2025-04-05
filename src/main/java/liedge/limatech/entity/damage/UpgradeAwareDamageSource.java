package liedge.limatech.entity.damage;

import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.lib.upgrades.UpgradesContainerBase;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class UpgradeAwareDamageSource extends LimaDynamicDamageSource
{
    protected UpgradeAwareDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition)
    {
        super(type, directEntity, causingEntity, damageSourcePosition);
    }

    protected abstract @Nullable IItemHandler getOrCreateTeleportInventory();

    public abstract UpgradesContainerBase<?, ?> getUpgrades();

    public abstract @Nullable Vec3 directTeleportDropsLocation();

    public final @Nullable IItemHandler directTeleportDropsInventory()
    {
        if (getUpgrades().upgradeEffectTypePresent(LimaTechUpgradeEffectComponents.DIRECT_ITEM_TELEPORT.get()))
        {
            return getOrCreateTeleportInventory();
        }
        else
        {
            return null;
        }
    }
}