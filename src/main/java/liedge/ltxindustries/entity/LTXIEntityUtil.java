package liedge.ltxindustries.entity;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.util.config.LTXIServerConfig;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.entity.PartEntity;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public final class LTXIEntityUtil
{
    private static final int MAX_ENTITY_CHECK_RECURSION = 5;

    private LTXIEntityUtil() {}

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
            return serverPlayer.canHarmPlayer(target) && LTXIServerConfig.ENABLE_MOD_PVP.getAsBoolean();
        }

        return true;
    }

    public static boolean isValidContextTarget(Entity targetEntity, @Nullable Entity attackingEntity, TargetPredicate predicate)
    {
        if (!(targetEntity.level() instanceof ServerLevel serverLevel)) return false;
        return isValidBaseTarget(targetEntity, attackingEntity) && predicate.test(serverLevel, targetEntity, attackingEntity);
    }

    public static boolean isValidContextTarget(Entity targetEntity, @Nullable Entity attackingEntity, Upgrades upgrades)
    {
        return isValidContextTarget(targetEntity, attackingEntity, TargetPredicate.create(upgrades));
    }

    public static boolean isValidBaseTarget(Entity targetEntity, @Nullable Entity attackingEntity)
    {
        return isValidBaseTarget(targetEntity, attackingEntity, 0);
    }

    private static boolean isValidBaseTarget(Entity targetEntity, @Nullable Entity attackingEntity, int recursionDepth)
    {
        if (recursionDepth > MAX_ENTITY_CHECK_RECURSION) return false;

        // Don't hurt the owner, removed/dead entities and immune entity type tag entities
        if (!isEntityAlive(targetEntity) || targetEntity == attackingEntity || targetEntity.is(LTXITags.EntityTypes.INVALID_TARGETS)) return false;

        // Attacks can come with no attacking entity (rogue entities/machines/etc.)
        final boolean validAttacker = attackingEntity != null;

        return switch (targetEntity)
        {
            // Don't hurt traceable entities with the same owner
            case TraceableEntity traceable when validAttacker && traceable.getOwner() == attackingEntity -> false;

            // Don't hurt ownable mobs with same owner
            case OwnableEntity ownable when validAttacker && ownable.getOwner() == attackingEntity -> false;

            // Don't hurt part entities if their parent entity is dead
            case PartEntity<?> part when !isValidBaseTarget(part.getParent(), attackingEntity, recursionDepth + 1) -> false;

            // Check pvp rules if target is a player
            case Player player when !checkPlayerPVPRule(attackingEntity, player) -> false;

            // Finally, don't hurt the vehicle entity owner is riding (if any)
            default -> !validAttacker || !attackingEntity.isPassengerOfSameVehicle(targetEntity);
        };
    }

    public static IntList flattenEntityIds(Collection<Entity> entities)
    {
        if (entities.isEmpty()) return IntList.of();

        IntList list = new IntArrayList(entities.size());
        for (Entity entity : entities)
        {
            int depth = 0;
            while (entity instanceof PartEntity<?> partEntity && depth <= MAX_ENTITY_CHECK_RECURSION)
            {
                entity = partEntity.getParent();
                depth++;
            }

            if (depth < MAX_ENTITY_CHECK_RECURSION) list.add(LimaEntityUtil.getEntityId(entity));
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

    public static Stream<EntityHitResult> traceEntities(Level level, Entity directEntity, @Nullable Entity sourceEntity, Vec3 start, Vec3 end, TargetPredicate predicate, ToDoubleFunction<Entity> bbExpansion)
    {
        Vec3 path = end.subtract(start);

        return level.getEntities(directEntity, directEntity.getBoundingBox().expandTowards(path).inflate(0.3d), hit -> isValidContextTarget(hit, sourceEntity, predicate))
                .stream()
                .sorted(Comparator.comparingDouble(hit -> hit.distanceToSqr(start)))
                .mapMulti((hit, consumer) -> {
                    EntityHitResult hitResult = clipEntityBoundingBox(hit, start, end, bbExpansion.applyAsDouble(hit));
                    if (hitResult != null) consumer.accept(hitResult);
                });
    }

    public static boolean hurtWithEnchantedFakePlayer(ServerLevel level, Entity target, @Nullable LivingEntity owner, Upgrades upgrades, Function<@Nullable LivingEntity, ? extends DamageSource> damageSourceFunction, float damage)
    {
        if (owner instanceof Player player)
        {
            FakePlayer fakePlayer = FakePlayerFactory.get(level, player.getGameProfile());
            ItemEnchantments enchantments = upgrades.getEnchantments();

            if (!enchantments.isEmpty())
            {
                ItemStack stack = new ItemStack(Items.STICK);
                stack.set(DataComponents.ENCHANTMENTS, enchantments);
                fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
            }

            boolean result = target.hurtServer(level, damageSourceFunction.apply(fakePlayer), damage);
            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            return result;
        }
        else
        {
            return target.hurtServer(level, damageSourceFunction.apply(owner), damage);
        }
    }
}