package liedge.ltxindustries.lib.weapons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface WeaponReloadSource extends UpgradeTooltipsProvider
{
    Codec<WeaponReloadSource> CODEC = Type.CODEC.dispatch(WeaponReloadSource::getType, Type::getCodec);
    StreamCodec<RegistryFriendlyByteBuf, WeaponReloadSource> STREAM_CODEC = Type.STREAM_CODEC.dispatch(WeaponReloadSource::getType, Type::getStreamCodec);

    static WeaponReloadSource withItem(Holder<Item> holder)
    {
        return new ItemSource(holder);
    }

    static WeaponReloadSource commonEnergy()
    {
        return CommonEnergySource.INSTANCE;
    }

    static WeaponReloadSource infiniteAmmo()
    {
        return InfiniteSource.INSTANCE;
    }

    boolean canReload(ItemStack heldItem, Player player, WeaponItem weaponItem);

    boolean performReload(ItemStack heldItem, Player player, WeaponItem weaponItem);

    @Override
    default void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        lines.accept(getType().upgradeTooltip.translate().withStyle(getType().color.chatStyle()));
    }

    default Component getItemTooltip()
    {
        return getType().itemTooltip.translate().withStyle(getType().color.chatStyle());
    }

    Type getType();

    record ItemSource(Holder<Item> reloadItem) implements WeaponReloadSource
    {
        private static final MapCodec<ItemSource> CODEC = ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").xmap(ItemSource::new, ItemSource::reloadItem);
        private static final StreamCodec<RegistryFriendlyByteBuf, ItemSource> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ITEM).map(ItemSource::new, ItemSource::reloadItem);

        @Override
        public boolean canReload(ItemStack heldItem, Player player, WeaponItem weaponItem)
        {
            Inventory inventory = player.getInventory();
            return Stream.concat(inventory.items.stream(), inventory.offhand.stream()).anyMatch(stack -> stack.is(reloadItem));
        }

        @Override
        public boolean performReload(ItemStack heldItem, Player player, WeaponItem weaponItem)
        {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++)
            {
                ItemStack invItem = player.getInventory().getItem(i);
                if (invItem.is(reloadItem))
                {
                    player.getInventory().removeItem(i, 1);
                    return true;
                }
            }

            return false;
        }

        @Override
        public Type getType()
        {
            return Type.ITEM;
        }

        @Override
        public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
        {
            lines.accept(getType().upgradeTooltip.translateArgs(itemNameTooltip()).withStyle(ChatFormatting.GRAY));
        }

        @Override
        public Component getItemTooltip()
        {
            return getType().itemTooltip.translateArgs(itemNameTooltip()).withStyle(ChatFormatting.GRAY);
        }

        private Component itemNameTooltip()
        {
            return reloadItem.value().getDescription().copy().withStyle(getType().color.chatStyle());
        }
    }

    enum CommonEnergySource implements WeaponReloadSource
    {
        INSTANCE;

        @Override
        public boolean canReload(ItemStack heldItem, Player player, WeaponItem weaponItem)
        {
            return weaponItem.getEnergyStored(heldItem) >= weaponItem.getEnergyUsage(heldItem);
        }

        @Override
        public boolean performReload(ItemStack heldItem, Player player, WeaponItem weaponItem)
        {
            IEnergyStorage weaponEnergy = heldItem.getCapability(Capabilities.EnergyStorage.ITEM);
            return weaponEnergy != null && LimaEnergyUtil.consumeEnergy(weaponEnergy, weaponItem.getEnergyUsage(heldItem), true);
        }

        @Override
        public Type getType()
        {
            return Type.COMMON_ENERGY;
        }
    }

    enum InfiniteSource implements WeaponReloadSource
    {
        INSTANCE;

        @Override
        public boolean canReload(ItemStack heldItem, Player player, WeaponItem weaponItem)
        {
            return true;
        }

        @Override
        public boolean performReload(ItemStack heldItem, Player player, WeaponItem weaponItem)
        {
            return true;
        }

        @Override
        public Type getType()
        {
            return Type.INFINITE;
        }
    }

    enum Type implements StringRepresentable
    {
        ITEM("item", LTXIConstants.LIME_GREEN, ItemSource.CODEC, ItemSource.STREAM_CODEC),
        COMMON_ENERGY("energy", LTXIConstants.REM_BLUE, MapCodec.unit(CommonEnergySource.INSTANCE), StreamCodec.unit(CommonEnergySource.INSTANCE)),
        INFINITE("infinite", LTXIConstants.CREATIVE_PINK, MapCodec.unit(InfiniteSource.INSTANCE), StreamCodec.unit(InfiniteSource.INSTANCE));

        public static final Codec<Type> CODEC = LimaEnumCodec.create(Type.class);
        public static final StreamCodec<RegistryFriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Type.class);

        private final String name;
        private final LimaColor color;
        private final MapCodec<? extends WeaponReloadSource> codec;
        private final StreamCodec<? super RegistryFriendlyByteBuf, ? extends WeaponReloadSource> streamCodec;
        private final Translatable itemTooltip;
        private final Translatable upgradeTooltip;

        Type(String name, LimaColor color, MapCodec<? extends WeaponReloadSource> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends WeaponReloadSource> streamCodec)
        {
            this.name = name;
            this.color = color;
            this.codec = codec;
            this.streamCodec = streamCodec;
            this.itemTooltip = LTXILangKeys.tooltip("reload_source." + name);
            this.upgradeTooltip = LTXILangKeys.upgradeEffect("reload_source." + name);
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        public LimaColor getColor()
        {
            return color;
        }

        public MapCodec<? extends WeaponReloadSource> getCodec()
        {
            return codec;
        }

        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends WeaponReloadSource> getStreamCodec()
        {
            return streamCodec;
        }

        public Translatable getItemTooltip()
        {
            return itemTooltip;
        }

        public Translatable getUpgradeTooltip()
        {
            return upgradeTooltip;
        }
    }
}