package liedge.limatech.entity;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.util.LimaEntityUtil;
import liedge.limatech.LimaTechTags;
import liedge.limatech.lib.upgrades.UpgradesContainerBase;
import liedge.limatech.registry.game.LimaTechMobEffects;
import liedge.limatech.util.config.LimaTechServerConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

public final class LimaTechEntityUtil
{
    private static final int MAX_ENTITY_CHECK_RECURSION = 5;

    private LimaTechEntityUtil() {}

    /**
     * Checks if an entity can be considered 'alive' for the purposes of this mod. The {@link EnderDragon}
     * requires a special check as it's health is 1 when it is defeated instead of 0.
     * @param entity Entity to check
     * @return If the entity is alive
     */
    public static boolean isEntityAlive(Entity entity)
    {
        if (entity instanceof EnderDragon dragon) return dragon.getHealth() > 1f;
        return entity.isAlive();
    }

    /**
     * Checks if the {@code attackingEntity} is a {@link ServerPlayer}. If it is, then checks both
     * the game's PVP rule and this mod's PVP config option. Both must be true for this check to pass.
     * Otherwise, this check always passes.
     * @param attackingEntity The attacking entity
     * @param target The target entity
     * @return Whether the attacking entity can attack the target
     */
    public static boolean checkPlayerPVPRule(@Nullable Entity attackingEntity, Player target)
    {
        if (attackingEntity instanceof ServerPlayer serverPlayer)
        {
            return serverPlayer.canHarmPlayer(target) && LimaTechServerConfig.ENABLE_MOD_PVP.getAsBoolean();
        }

        return true;
    }

    public static boolean isValidWeaponTarget(@Nullable Entity attackingEntity, Entity target)
    {
        return isValidWeaponTarget(attackingEntity, target, 0);
    }

    private static boolean isValidWeaponTarget(@Nullable Entity attackingEntity, Entity target, int recursionDepth)
    {
        if (recursionDepth > MAX_ENTITY_CHECK_RECURSION) return false;

        // Don't hurt the owner, removed/dead entities and immune entity type tag entities
        if (!isEntityAlive(target) || target == attackingEntity || target.getType().is(LimaTechTags.EntityTypes.INVALID_TARGETS)) return false;

        // Attacks can come with no attacking entity (rogue entities/machines/etc.)
        final boolean validAttacker = attackingEntity != null;

        return switch (target)
        {
            // Don't hurt traceable entities with the same owner
            case TraceableEntity traceable when validAttacker && traceable.getOwner() == attackingEntity -> false;

            // Don't hurt ownable mobs with same owner
            case OwnableEntity ownable when validAttacker && ownable.getOwner() == attackingEntity -> false;

            // Don't hurt part entities if their parent entity is dead
            case PartEntity<?> part when !isValidWeaponTarget(attackingEntity, part.getParent(), recursionDepth + 1) -> false;

            // Check pvp rules if target is a player
            case Player player when !checkPlayerPVPRule(attackingEntity, player) -> false;

            // Finally, don't hurt the vehicle entity owner is riding (if any)
            default -> validAttacker && !attackingEntity.isPassengerOfSameVehicle(target);
        };
    }

    public static IntList flattenEntityIds(Collection<Entity> entities)
    {
        IntList list = new IntArrayList(entities.size());

        for (Entity e : entities)
        {
            int depth = 0;
            while (e instanceof PartEntity<?> && depth <= MAX_ENTITY_CHECK_RECURSION)
            {
                e = ((PartEntity<?>) e).getParent();
                depth++;
            }

            if (depth <= MAX_ENTITY_CHECK_RECURSION) list.add(LimaEntityUtil.getEntityId(e));
        }

        return list;
    }

    /**
     * Determines if a vector intersects/clips with an entity's bounding box.
     * @param target The entity who's bounding box will be checked.
     * @param start The start position of the path vector to be checked.
     * @param end The end position of the path vector to be checked.
     * @param bbExpansion How much to expand the target's bounding box before performing the clip check.
     * @return The hit result containing entity and the clip point on the entity's bounding box or null if vector did not clip entity's bounding box.
     */
    public static @Nullable EntityHitResult clipEntityBoundingBox(Entity target, Vec3 start, Vec3 end, double bbExpansion)
    {
        AABB bb = target.getBoundingBox().inflate(Math.max(target.getPickRadius(), bbExpansion));
        if (bb.contains(start))
        {
            return new EntityHitResult(target, start);
        }
        else
        {
            return bb.clip(start, end).map(vec -> new EntityHitResult(target, vec)).orElse(null);
        }
    }

    public static <T extends Entity & TraceableEntity> HitResult traceProjectileEntityPath(Level level, T projectile, ClipContext.Block blockCollision, ClipContext.Fluid fluidCollision, double bbExpansion)
    {
        Vec3 origin = projectile.position();
        Vec3 path = projectile.getDeltaMovement();
        BlockHitResult blockTrace = level.clip(new ClipContext(origin, origin.add(path), blockCollision, fluidCollision, projectile));
        Vec3 impact = blockTrace.getLocation();

        EntityHitResult entityTrace = level.getEntities(projectile, projectile.getBoundingBox().expandTowards(path).inflate(0.3d), hit -> isValidWeaponTarget(projectile.getOwner(), hit))
                .stream()
                .sorted(Comparator.comparingDouble(hit -> hit.distanceToSqr(origin)))
                .flatMap(hit -> Stream.ofNullable(clipEntityBoundingBox(hit, origin, impact, bbExpansion)))
                .findFirst().orElse(null);

        return entityTrace != null ? entityTrace : blockTrace;
    }

    public static void hurtWithEnchantedFakePlayer(ServerLevel level, Entity target, @Nullable LivingEntity owner, UpgradesContainerBase<?, ?> upgrades, Function<@Nullable LivingEntity, ? extends DamageSource> damageSourceFunction, float damage)
    {
        if (owner instanceof Player)
        {
            ItemEnchantments enchantments = upgrades.getEnchantments();

            owner = FakePlayerFactory.get(level, ((Player) owner).getGameProfile());

            if (enchantments.isEmpty())
            {
                owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY); // Just in case...
                target.hurt(damageSourceFunction.apply(owner), damage);
            }
            else
            {
                ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
                stack.set(DataComponents.ENCHANTMENTS, enchantments);

                owner.setItemInHand(InteractionHand.MAIN_HAND, stack);
                target.hurt(damageSourceFunction.apply(owner), damage);
                owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        else
        {
            target.hurt(damageSourceFunction.apply(owner), damage);
        }
    }

    public static boolean checkIfEntityHasNeuroEffect(@Nullable Entity applyingEntity)
    {
        if (applyingEntity instanceof LivingEntity livingEntity) return livingEntity.hasEffect(LimaTechMobEffects.NEURO_SUPPRESSED);

        return false;
    }
}