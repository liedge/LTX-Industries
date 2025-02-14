package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.limatech.lib.math.CompoundOperation;
import liedge.limatech.lib.math.LevelBasedDoubleValue;
import liedge.limatech.lib.upgrades.effect.UpgradeDataComponentType;
import liedge.limatech.lib.upgrades.effect.UpgradeEffect;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ValueUpgradeEffect extends UpgradeEffect
{
    Codec<ValueUpgradeEffect> CODEC = LimaCoreCodecs.flatDispatchCodec(ValueUpgradeEffectType.CODEC, SimpleValueOperationEffect.class, SimpleValueOperationEffect.FLAT_CODEC, ValueUpgradeEffect::type, ValueUpgradeEffectType::getCodec);
    Codec<List<ValueUpgradeEffect>> LIST_CODEC = CODEC.listOf();

    static SimpleValueOperationEffect simpleValue(LevelBasedDoubleValue value, CompoundOperation operation)
    {
        return new SimpleValueOperationEffect(value, operation);
    }

    static TargetAttributeValueEffect addEnemyAttribute(Holder<Attribute> attribute, LevelBasedDoubleValue amount)
    {
        return new TargetAttributeValueEffect(attribute, amount);
    }

    static PlayerAttributeValueEffect addPlayerAttribute(Holder<Attribute> attribute, LevelBasedDoubleValue amount)
    {
        return new PlayerAttributeValueEffect(attribute, amount);
    }

    double calculate(@Nullable Player player, @Nullable Entity targetEntity, int upgradeRank);

    Component getValueTooltip(int upgradeRank, boolean beneficial);

    CompoundOperation operation();

    ValueUpgradeEffectType type();

    final class ComponentType extends UpgradeDataComponentType<List<ValueUpgradeEffect>> implements Translatable
    {
        private final String descriptionId;
        private final boolean beneficial;

        public ComponentType(ResourceLocation id, boolean beneficial)
        {
            super(LIST_CODEC);
            this.descriptionId = ModResources.prefixIdTranslationKey("value_effect", id);
            this.beneficial = beneficial;
        }

        @Override
        public String descriptionId()
        {
            return descriptionId;
        }

        @Override
        public void appendTooltipLines(List<ValueUpgradeEffect> data, int upgradeRank, List<Component> lines)
        {
            for (ValueUpgradeEffect e : data)
            {
                lines.add(translateArgs(e.getValueTooltip(upgradeRank, beneficial)));
            }
        }
    }
}