package liedge.ltxindustries.item;

import liedge.limacore.util.LimaEntityUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.util.config.LTXIServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class EnergyArmorItem extends EnergyEquipmentItem implements Equipable
{
    private static ItemAttributeModifiers createAttributes(EquipmentSlot slot, float armor)
    {
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(slot);
        ResourceLocation modifierId = LTXIndustries.RESOURCES.location("armor." + slot.getSerializedName());

        return ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(modifierId, armor, AttributeModifier.Operation.ADD_VALUE), slotGroup)
                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(modifierId, 3f, AttributeModifier.Operation.ADD_VALUE), slotGroup)
                .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(modifierId, 0.1f, AttributeModifier.Operation.ADD_VALUE), slotGroup)
                .build();
    }

    private final EquipmentSlot equipmentSlot;
    private final ItemAttributeModifiers defaultModifiers;

    public EnergyArmorItem(Properties properties, EquipmentSlot equipmentSlot, float armor)
    {
        super(properties);
        this.equipmentSlot = equipmentSlot;
        this.defaultModifiers = createAttributes(equipmentSlot, armor);
    }

    public boolean cancelFallDamage(LivingEntity entity, ItemStack stack, float distance)
    {
        double safeDist = LimaEntityUtil.getAttributeValueSafe(entity, Attributes.SAFE_FALL_DISTANCE);
        return distance <= safeDist || consumeEnergyAction(entity, stack);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
    {
        return defaultModifiers;
    }

    @Override
    public EquipmentSlot getEquipmentSlot()
    {
        return equipmentSlot;
    }

    @Override
    public int getBaseEnergyCapacity(ItemStack stack)
    {
        return LTXIServerConfig.ARMOR_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemStack stack)
    {
        return LTXIServerConfig.ARMOR_ENERGY_PER_ACTION.getAsInt();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
    {
        return swapWithEquipmentSlot(this ,level, player, usedHand);
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer)
    {
        return true;
    }
}