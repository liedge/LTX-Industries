package liedge.limatech.item.weapon;

import liedge.limacore.item.LimaCreativeTabStacksProvider;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.item.TooltipShiftHintItem;
import liedge.limatech.lib.weapons.AbstractWeaponInput;
import liedge.limatech.registry.LimaTechDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class WeaponItem extends Item implements TooltipShiftHintItem.TextOnly, LimaCreativeTabStacksProvider
{
    public static final Translatable AMMO_ITEM_TOOLTIP = LimaTech.RESOURCES.translationHolder("tooltip.{}.ammo_item");
    public static final Translatable AMMO_LOADED_TOOLTIP = LimaTech.RESOURCES.translationHolder("tooltip.{}.ammo_loaded");

    private String namePrefix;
    private Component displayName;

    protected WeaponItem(Properties properties)
    {
        super(properties);
    }

    //#region Weapon user events
    public abstract void triggerPressed(ItemStack heldItem, Player player, AbstractWeaponInput input);

    public abstract void triggerRelease(ItemStack heldItem, Player player, AbstractWeaponInput input, boolean releasedByPlayer);

    public boolean canContinueHoldingTrigger(ItemStack heldItem, Player player, AbstractWeaponInput input)
    {
        return false;
    }

    public void triggerHoldingTick(ItemStack heldItem, Player player, AbstractWeaponInput input) {}

    public abstract void weaponFired(ItemStack heldItem, Player player, Level level);

    public void killedByWeapon(LivingEntity killerEntity, LivingEntity killedEntity) {}
    //#endregion

    //#region Weapon properties/behavior
    public abstract Item getAmmoItem();

    public abstract int getFireRate(ItemStack stack);

    public abstract int getAmmoCapacity(ItemStack stack);

    public abstract int getReloadSpeed(ItemStack stack);

    public abstract int getRecoilA(ItemStack stack);

    public int getRecoilB(ItemStack stack)
    {
        return 0;
    }
    //#endregion

    public String getNamePrefix()
    {
        if (namePrefix == null) namePrefix = getDescriptionId() + ".prefix";
        return namePrefix;
    }

    private Component getDisplayName()
    {
        if (displayName == null)
        {
            displayName = Component.translatable(getNamePrefix())
                    .withStyle(LimaTechConstants.LIME_GREEN::applyStyle)
                    .append(Component.translatable(getDescriptionId()).withStyle(ChatFormatting.ITALIC));
        }

        return displayName;
    }

    @Override
    public Component getDescription()
    {
        return getDisplayName();
    }

    @Override
    public Component getName(ItemStack stack)
    {
        return getDisplayName();
    }

    @Override
    public void appendTooltipHintLines(@Nullable Level level, ItemStack stack, Consumer<FormattedText> consumer)
    {
        consumer.accept(AMMO_LOADED_TOOLTIP.translateArgs(stack.getOrDefault(LimaTechDataComponents.WEAPON_AMMO, 0), getAmmoCapacity(stack)).withStyle(LimaTechConstants.LIME_GREEN::applyStyle));
        consumer.accept(AMMO_ITEM_TOOLTIP.translateArgs(getAmmoItem().getDescription()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void generateCreativeTabStacks(ResourceLocation tabId, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output)
    {
        ItemStack stack = new ItemStack(this);
        stack.set(LimaTechDataComponents.WEAPON_AMMO, getAmmoCapacity(stack));
        output.accept(stack);
    }

    @Override
    public boolean addDefaultItemStack()
    {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book)
    {
        return false;
    }
}