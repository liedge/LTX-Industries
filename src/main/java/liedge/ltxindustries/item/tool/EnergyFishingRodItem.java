package liedge.ltxindustries.item.tool;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;

public class EnergyFishingRodItem extends BaseEnergyToolItem
{
    public EnergyFishingRodItem(Properties properties)
    {
        super(properties, 0f);
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
    {
        ItemStack stack = player.getItemInHand(usedHand);
        if (!hasEnergyForAction(stack)) return InteractionResultHolder.pass(stack); // Return on no energy

        // Fishing hook deployed
        if (player.fishing != null)
        {
            if (!level.isClientSide() && player.fishing.retrieve(stack) > 0)
            {
                consumeEnergyAction(player, stack);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)); // What is this pitch formula? Simplify?
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        }
        else // Fishing hook not yet cast
        {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));

            if (level instanceof ServerLevel serverLevel)
            {
                int fishingLuck = EnchantmentHelper.getFishingLuckBonus(serverLevel, stack, player);
                int fishingLureSpeed = (int) (EnchantmentHelper.getFishingTimeReduction(serverLevel, stack, player) * 20f);
                level.addFreshEntity(new FishingHook(player, level, fishingLuck, fishingLureSpeed));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}