package liedge.limatech.util.config;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class LimaTechServerConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Validator helpers
    private static boolean validateEntityTypes(Object rawObject)
    {
        if (rawObject instanceof String string)
        {
            ResourceLocation rl = ResourceLocation.tryParse(string);
            return rl != null && BuiltInRegistries.ENTITY_TYPE.containsKey(rl);
        }
        else
        {
            return false;
        }
    }

    // Config spec values
    private static final ModConfigSpec.BooleanValue WEAPONS_HURT_OWNED_ENTITIES = BUILDER.comment(
            "Whether this mod's weapons and their projectiles hurt entities owned/tamed by the weapon user.",
            "Does not apply to entities with no owner or entities owned by someone else. [Default: false]").define("weapons_hurt_owned_entities", false);

    private static final ModConfigSpec.DoubleValue WEAPON_PLAYER_DAMAGE_MODIFIER = BUILDER.comment(
            "Modifies the base damage (before any resistances/effects/etc.) dealt by this mod's weapons/projectiles to other players.").defineInRange("weapon_player_damage_modifier", 1f, 0f, Double.MAX_VALUE);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> WEAPON_DAMAGE_BLACKLIST_CV = BUILDER.comment(
            "Entity types blacklisted from being hurt by this mod's weapons and projectiles.")
            .defineListAllowEmpty("weapon_damage_blacklist", List.of("minecraft:item", "minecraft:experience_orb", "minecraft:item_frame", "minecraft:glow_item_frame"), () -> "modid:entity_type", LimaTechServerConfig::validateEntityTypes);

    // Fabricator machine values
    private static final ModConfigSpec.IntValue FABRICATOR_ENERGY_CAPACITY = BUILDER.comment(
            "Energy capacity of the Fabricator").defineInRange("fabricator_energy", 5_000_000, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue FABRICATOR_ENERGY_IO_RATE = BUILDER.comment(
            "Energy IO rate of the Fabricator.",
            "NOTE: This is also the crafting 'speed' of the fabricator as the recipe crafting times are based on energy amounts.").defineInRange("fabricator_energy_io_rate", 100_000, 1, Integer.MAX_VALUE);

    private static final Set<EntityType<?>> WEAPON_DAMAGE_BLACKLIST = new ObjectOpenHashSet<>();

    public static final ModConfigSpec CONFIG_SPEC = BUILDER.build();

    public static void onConfigLoaded(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == CONFIG_SPEC)
        {
            WEAPON_DAMAGE_BLACKLIST.clear();
            List<EntityType<?>> dmgBlacklist = WEAPON_DAMAGE_BLACKLIST_CV.get().stream().map(id -> BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(id))).collect(Collectors.toList());
            WEAPON_DAMAGE_BLACKLIST.addAll(dmgBlacklist);
        }
    }

    // Value accessors
    public static boolean canWeaponDamage(EntityType<?> entityType)
    {
        return !WEAPON_DAMAGE_BLACKLIST.contains(entityType);
    }

    public static boolean weaponsHurtOwnedEntities()
    {
        return WEAPONS_HURT_OWNED_ENTITIES.get();
    }

    public static float weaponPlayerDamageModifier()
    {
        return WEAPON_PLAYER_DAMAGE_MODIFIER.get().floatValue();
    }

    public static int fabricatorEnergyCapacity()
    {
        return FABRICATOR_ENERGY_CAPACITY.get();
    }

    public static int fabricatorEnergyIORate()
    {
        return FABRICATOR_ENERGY_IO_RATE.get();
    }
}