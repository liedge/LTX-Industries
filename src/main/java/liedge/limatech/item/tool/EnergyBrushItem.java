package liedge.limatech.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;

public class EnergyBrushItem extends EnergyBaseToolItem
{
    public EnergyBrushItem(Properties properties)
    {
        super(properties, 0f);
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_BRUSH_ACTIONS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Player player = context.getPlayer();

        if (player != null && hasEnergyForAction(context.getItemInHand()) && calculateHitResult(player).getType() == HitResult.Type.BLOCK)
        {
            player.startUsingItem(context.getHand());
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 200;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration)
    {
        if (remainingUseDuration >= 0 && livingEntity instanceof Player player)
        {
            HitResult hitResult = calculateHitResult(player);
            if (hitResult instanceof BlockHitResult blockHitResult && hitResult.getType() == HitResult.Type.BLOCK)
            {
                boolean flag = ((remainingUseDuration + 1) % 10 == 5);
                if (flag)
                {
                    BlockPos pos = blockHitResult.getBlockPos();
                    BlockState state = level.getBlockState(pos);
                    HumanoidArm playerArm = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();

                    if (state.shouldSpawnTerrainParticles() && state.getRenderShape() != RenderShape.INVISIBLE) spawnDustParticles(level, blockHitResult, state, player.getViewVector(0f), playerArm);

                    SoundEvent sound = state.getBlock() instanceof BrushableBlock block ? block.getBrushSound() : SoundEvents.BRUSH_GENERIC;
                    level.playSound(player, pos, sound, SoundSource.BLOCKS);

                    if (!level.isClientSide() && level.getBlockEntity(pos) instanceof BrushableBlockEntity brushable && brushable.brush(level.getGameTime(), player, blockHitResult.getDirection()))
                    {
                        consumeActionEnergy(player, stack);
                    }
                }
            }
        }

        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    private HitResult calculateHitResult(Player player)
    {
        return ProjectileUtil.getHitResultOnViewVector(player, e -> !e.isSpectator() && e.isPickable(), player.blockInteractionRange());
    }

    private void spawnDustParticles(Level level, BlockHitResult hitResult, BlockState state, Vec3 pos, HumanoidArm arm)
    {
        BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, state);
        Direction direction = hitResult.getDirection();
        RandomSource random = level.getRandom();

        Vec3 pPos = hitResult.getLocation();
        Vec3 pDelta = particleDelta(pos, direction);

        int armOffset = arm == HumanoidArm.RIGHT ? 1 : -1;
        int count = random.nextInt(7, 12);

        for (int i = 0; i < count; i++)
        {
            double xo = direction == Direction.WEST ? 1e-6f : 0f;
            double zo = direction == Direction.NORTH ? 1e-6f : 0f;
            double dx = pDelta.x * armOffset * 3d * random.nextDouble();
            double dz = pDelta.z * armOffset * 3d * random.nextDouble();

            level.addParticle(option, pPos.x - xo, pPos.y, pPos.z - zo, dx, 0, dz);
        }
    }

    private Vec3 particleDelta(Vec3 pos, Direction direction)
    {
        return switch (direction)
        {
            case DOWN, UP -> new Vec3(pos.z(), 0, -pos.x());
            case NORTH -> new Vec3(1, 0, -0.1d);
            case SOUTH -> new Vec3(-1, 0, 0.1d);
            case WEST -> new Vec3(-0.1d, 0, -1);
            case EAST -> new Vec3(0.1d, 0, 1);
        };
    }
}