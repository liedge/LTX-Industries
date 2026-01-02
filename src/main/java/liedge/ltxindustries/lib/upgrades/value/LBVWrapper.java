package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.enchantment.LevelBasedValue;

record LBVWrapper(LevelBasedValue value) implements ContextlessValue
{
    static final MapCodec<LBVWrapper> CODEC = LevelBasedValue.CODEC.fieldOf("value").xmap(LBVWrapper::new, LBVWrapper::value);

    @Override
    public double calculate(int upgradeRank)
    {
        return value.calculate(upgradeRank);
    }

    @Override
    public MapCodec<? extends ContextlessValue> codec()
    {
        return CODEC;
    }
}