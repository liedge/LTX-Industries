package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.limatech.lib.math.CompoundOperation;
import liedge.limatech.lib.math.LevelBasedDoubleValue;
import liedge.limatech.lib.upgrades.effect.UpgradeEffect;
import liedge.limatech.lib.upgrades.effect.UpgradeEffectDataType;
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

    static DataType createDataType(ResourceLocation id, boolean beneficial)
    {
        String descriptionId = ModResources.prefixIdTranslationKey("value_effect", id);
        return new DataType(id, descriptionId, beneficial);
    }

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

    record DataType(ResourceLocation id, String descriptionId, boolean beneficial) implements UpgradeEffectDataType<List<ValueUpgradeEffect>>, Translatable
    {
        @Override
        public Codec<List<ValueUpgradeEffect>> codec()
        {
            return LIST_CODEC;
        }

        @Override
        public void appendTooltipLines(List<ValueUpgradeEffect> effectData, int upgradeRank, List<Component> lines)
        {
            for (ValueUpgradeEffect effect : effectData)
            {
                lines.add(this.translateArgs(effect.getValueTooltip(upgradeRank, beneficial)));
            }
        }
    }
}