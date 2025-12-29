package liedge.ltxindustries.lib.upgrades.effect;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public enum EffectTarget implements StringRepresentable
{
    ATTACKER("attacker", LootContextParams.ATTACKING_ENTITY),
    VICTIM("victim", LootContextParams.THIS_ENTITY),
    DAMAGING_ENTITY("damaging_entity", LootContextParams.DIRECT_ATTACKING_ENTITY);

    public static final LimaEnumCodec<EffectTarget> CODEC = LimaEnumCodec.create(EffectTarget.class);
    public static final LimaEnumCodec<EffectTarget> SOURCE_CODEC = LimaEnumCodec.create(EffectTarget.class, List.of(ATTACKER, VICTIM));

    private final String name;
    private final LootContextParam<? extends Entity> param;

    EffectTarget(String name, LootContextParam<? extends Entity> param)
    {
        this.name = name;
        this.param = param;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }

    public @Nullable Entity apply(LootContext context)
    {
        return context.getParamOrNull(param);
    }
}