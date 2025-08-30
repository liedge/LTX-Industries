package liedge.ltxindustries.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.damage.LimaCoreDamageComponents;
import liedge.ltxindustries.lib.upgrades.effect.RankBasedAttributeModifier;
import liedge.ltxindustries.registry.game.LTXIEquipmentUpgradeEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.function.Consumer;

public record DamageAttributesUpgradeEffect(Holder<Attribute> attribute, RankBasedAttributeModifier modifier) implements EquipmentUpgradeEffect
{
    public static final MapCodec<DamageAttributesUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Attribute.CODEC.fieldOf("attribute").forGetter(DamageAttributesUpgradeEffect::attribute),
                    RankBasedAttributeModifier.CODEC.fieldOf("modifier").forGetter(DamageAttributesUpgradeEffect::modifier))
            .apply(instance, DamageAttributesUpgradeEffect::new));

    public static DamageAttributesUpgradeEffect of(Holder<Attribute> attribute, ResourceLocation id, LevelBasedValue amount, AttributeModifier.Operation operation)
    {
        return new DamageAttributesUpgradeEffect(attribute, new RankBasedAttributeModifier(id, amount, operation));
    }

    @Override
    public void applyEquipmentEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        DamageSource source = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        if (source != null) source.mergeListElement(LimaCoreDamageComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers.Entry(attribute, modifier.get(upgradeRank), EquipmentSlotGroup.ANY));
    }

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LTXIEquipmentUpgradeEffects.DAMAGE_ATTRIBUTES_EQUIPMENT_EFFECT.get();
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        lines.accept(attribute.value().toComponent(modifier.get(upgradeRank), TooltipFlag.NORMAL).withStyle(ChatFormatting.GRAY));
    }
}