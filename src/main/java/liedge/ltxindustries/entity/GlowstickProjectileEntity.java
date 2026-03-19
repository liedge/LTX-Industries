package liedge.ltxindustries.entity;

import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GlowstickProjectileEntity extends LTXIProjectileEntity
{
    public GlowstickProjectileEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public GlowstickProjectileEntity(Level level)
    {
        this(LTXIEntities.GLOWSTICK_PROJECTILE.get(), level);
    }

    @Override
    public int getLifetime()
    {
        return 100;
    }

    @Override
    protected CollisionResult onCollision(ServerLevel level, @Nullable LivingEntity owner, HitResult hitResult, Vec3 hitLocation)
    {
        if (hitResult.getType() == HitResult.Type.ENTITY) return CollisionResult.NO_OP;

        if (hitResult instanceof BlockHitResult blockHitResult)
        {
            Direction placeDirection = blockHitResult.getDirection();
            BlockPos placePos = blockHitResult.getBlockPos().relative(placeDirection);

            if (level.getBlockState(placePos).canBeReplaced())
            {
                BlockState state = LTXIBlocks.GLOWSTICK.get().defaultBlockState()
                        .setValue(BlockStateProperties.FACING, placeDirection)
                        .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(placePos).is(Fluids.WATER));

                if (state.canSurvive(level, placePos))
                    level.setBlockAndUpdate(placePos, state);
            }
        }

        return CollisionResult.DESTROY;
    }

    @Override
    protected void tickClient(Level level)
    {
        if (tickCount % 2 == 0)
        {
            double dx = getX() + (random.nextDouble() - random.nextDouble()) * 0.125d;
            double dz = getZ() + (random.nextDouble() - random.nextDouble()) * 0.125d;
            level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LTXIParticles.COLOR_GLITTER, LTXIConstants.LIME_GREEN, 0.75f), true, dx, getY(0.5d), dz, 0, 0, 0);
        }
    }
}