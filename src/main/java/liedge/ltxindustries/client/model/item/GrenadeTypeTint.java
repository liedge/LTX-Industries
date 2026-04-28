package liedge.ltxindustries.client.model.item;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public final class GrenadeTypeTint implements ItemTintSource
{
    public static final GrenadeTypeTint INSTANCE = new GrenadeTypeTint();
    public static final MapCodec<GrenadeTypeTint> CODEC = MapCodec.unit(INSTANCE);

    private GrenadeTypeTint() {}

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner)
    {
        return itemStack.getOrDefault(LTXIDataComponents.GRENADE_TYPE, GrenadeType.EXPLOSIVE).getColor().argb32();
    }

    @Override
    public MapCodec<? extends ItemTintSource> type()
    {
        return CODEC;
    }
}