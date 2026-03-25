package liedge.ltxindustries.item;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import liedge.ltxindustries.util.config.LTXIServerConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;

public class EnergyArmorItem extends EnergyEquipmentItem
{
    private static ItemAttributeModifiers createAttributes(EquipmentSlot slot, float armor)
    {
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(slot);
        Identifier modifierId = LTXIndustries.RESOURCES.id("armor." + slot.getSerializedName());

        return ItemAttributeModifiers.builder().add(Attributes.ARMOR, new AttributeModifier(modifierId, armor, AttributeModifier.Operation.ADD_VALUE), slotGroup).build();
    }

    private static Equippable creatEquippable(EquipmentSlot slot)
    {
        return Equippable.builder(slot)
                .setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
                .setAllowedEntities(EntityType.PLAYER)
                .build();
    }

    private final EquipmentSlot equipmentSlot;
    private final ItemAttributeModifiers defaultModifiers;

    public EnergyArmorItem(Properties properties, EquipmentSlot equipmentSlot, float armor)
    {
        super(properties.component(DataComponents.EQUIPPABLE, creatEquippable(equipmentSlot)));
        this.equipmentSlot = equipmentSlot;
        this.defaultModifiers = createAttributes(equipmentSlot, armor);
    }

    @Override
    public @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return switch (equipmentSlot)
        {
            case HEAD -> LTXIEquipmentUpgrades.HEAD_DEFAULT;
            case CHEST -> LTXIEquipmentUpgrades.BODY_DEFAULT;
            case LEGS -> LTXIEquipmentUpgrades.LEGS_DEFAULT;
            case FEET -> LTXIEquipmentUpgrades.FEET_DEFAULT;
            default -> null;
        };
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
    public int getBaseEnergyCapacity(ItemInstance stack)
    {
        return LTXIServerConfig.ARMOR_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage(ItemInstance stack)
    {
        return LTXIServerConfig.ARMOR_ENERGY_PER_ACTION.getAsInt();
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer)
    {
        return true;
    }

    @Override
    public boolean isGazeDisguise(ItemStack stack, Player player, @Nullable LivingEntity entity)
    {
        return true;
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer)
    {
        return true;
    }
}