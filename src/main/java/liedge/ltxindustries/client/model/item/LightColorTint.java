package liedge.ltxindustries.client.model.item;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.data.LightColors;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record LightColorTint(LightColors.Channel channel) implements ItemTintSource
{
    public static final MapCodec<LightColorTint> CODEC = LightColors.Channel.CODEC.fieldOf("channel").xmap(LightColorTint::new, LightColorTint::channel);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner)
    {
        if (stack.getItem() instanceof UpgradableEquipmentItem item)
        {
            return item.getLightColor(stack, channel);
        }

        return -1;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type()
    {
        return CODEC;
    }
}