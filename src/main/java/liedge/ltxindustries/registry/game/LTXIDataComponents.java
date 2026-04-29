package liedge.ltxindustries.registry.game;

import com.mojang.serialization.Codec;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.item.tool.ToolSpeed;
import liedge.ltxindustries.lib.upgrades.UpgradeEntry;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXIDataComponents
{
    private LTXIDataComponents() {}

    private static final DeferredRegister.DataComponents TYPES = LTXIndustries.RESOURCES.deferredDataComponents();

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }

    // Upgrade components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Upgrades>> UPGRADES = TYPES.registerComponentType("upgrades", builder -> builder.persistent(Upgrades.CODEC).networkSynchronized(Upgrades.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UpgradeEntry>> UPGRADE_ENTRY = TYPES.registerComponentType("upgrade", builder -> builder.persistent(UpgradeEntry.CODEC).networkSynchronized(UpgradeEntry.STREAM_CODEC).cacheEncoding());

    // Misc components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<Recipe<?>>>> BLUEPRINT_RECIPE = TYPES.registerComponentType("blueprint_recipe", builder -> builder.persistent(Recipe.KEY_CODEC).networkSynchronized(ResourceKey.streamCodec(Registries.RECIPE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockIOConfiguration>> BLOCK_IO_CONFIGURATION = TYPES.registerComponentType("block_io_config", builder -> builder.persistent(BlockIOConfiguration.CODEC).networkSynchronized(BlockIOConfiguration.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToolSpeed>> TOOL_SPEED = TYPES.registerComponentType("tool_speed", builder -> builder.persistent(ToolSpeed.CODEC).networkSynchronized(ToolSpeed.STREAM_CODEC));

    // Weapon components
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> WEAPON_AMMO = TYPES.registerComponentType("ammo", builder -> builder.persistent(ExtraCodecs.intRange(0, 1000)).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAGAZINE_CAPACITY = TYPES.registerComponentType("magazine_capacity", builder -> builder.persistent(ExtraCodecs.intRange(0, 1000)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> WEAPON_RANGE = TYPES.registerComponentType("range", builder -> builder.persistent(Codec.doubleRange(0, 250)).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RELOAD_SPEED = TYPES.registerComponentType("reload_speed", builder -> builder.persistent(Codec.intRange(0, 500)).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeaponReloadSource>> RELOAD_SOURCE = TYPES.registerComponentType("reload_source", builder -> builder.persistent(WeaponReloadSource.CODEC).networkSynchronized(WeaponReloadSource.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_HITS = TYPES.registerComponentType("max_hits", builder -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Double>> BLOCK_PIERCE = TYPES.registerComponentType("block_pierce", builder -> builder.persistent(Codec.doubleRange(0d, 512d)).networkSynchronized(ByteBufCodecs.DOUBLE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GrenadeType>> GRENADE_TYPE = TYPES.registerComponentType("grenade_type", builder -> builder.persistent(GrenadeType.CODEC).networkSynchronized(GrenadeType.STREAM_CODEC));
}