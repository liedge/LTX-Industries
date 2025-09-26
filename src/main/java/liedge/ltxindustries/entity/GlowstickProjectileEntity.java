package liedge.ltxindustries.entity;

import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GlowstickProjectileEntity extends LimaTraceableProjectile
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
    protected HitResult tracePath(Level level)
    {
        Vec3 origin = this.position();
        Vec3 path = this.getDeltaMovement();

        return level.clip(new ClipContext(origin, origin.add(path), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
    }

    @Override
    protected void onProjectileHit(Level level, HitResult hitResult, Vec3 hitLocation)
    {
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

        discard();
    }

    @Override
    protected void tickProjectile(Level level, boolean isClientSide)
    {
        if (isClientSide)
        {
            if (tickCount % 2 == 0)
            {
                double dx = getX() + (random.nextDouble() - random.nextDouble()) * 0.125d;
                double dz = getZ() + (random.nextDouble() - random.nextDouble()) * 0.125d;
                level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LTXIParticles.COLOR_GLITTER, LTXIConstants.LIME_GREEN, 0.75f), true, dx, getY(0.5d), dz, 0, 0, 0);
            }
        }
    }
}