package liedge.ltxindustries.entity.damage;

import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.lib.upgrades.DropsCapture;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class LTXIDamageSource extends DamageSource
{
    private final Upgrades upgrades;
    private final @Nullable ItemStack weaponItem;
    private final boolean applyEffects;

    public LTXIDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 pos, Upgrades upgrades, @Nullable ItemStack weaponItem, boolean applyEffects)
    {
        super(type, directEntity, causingEntity, pos);
        this.upgrades = upgrades;
        this.weaponItem = weaponItem;
        this.applyEffects = applyEffects;
    }

    public LTXIDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, Upgrades upgrades, @Nullable ItemStack weaponItem, boolean applyEffects)
    {
        this(type, directEntity, causingEntity, null, upgrades, weaponItem, applyEffects);
    }

    public Upgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    public @Nullable ItemStack getWeaponItem()
    {
        return weaponItem;
    }

    public @Nullable DropsCapture getDropsCapture()
    {
        Player player = LimaCoreObjects.tryCast(Player.class, getEntity());
        return player != null ? DropsCapture.mobDropsToPlayer(player, upgrades) : null;
    }

    public boolean canApplyDamageEffects()
    {
        return applyEffects;
    }
}