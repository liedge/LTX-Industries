package liedge.ltxindustries.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.lib.upgrades.effect.entity.*;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;

public final class LTXIEntityUpgradeEffects
{
    private LTXIEntityUpgradeEffects() {}

    @ApiStatus.Internal
    public static void register(RegisterEvent event, ModResources resources)
    {
        resources.registerByEvent(LTXIRegistries.Keys.ENTITY_UPGRADE_EFFECTS, event, LTXIEntityUpgradeEffects::registerCodecs);
    }

    private static void registerCodecs(ModResources.RegisterHelper<MapCodec<? extends EntityUpgradeEffect>> helper)
    {
        helper.register("all_of", CompoundEntityEffect.CODEC);
        helper.register("aoe", ApplyInArea.CODEC);
        helper.register("heal", HealEntity.CODEC);
        helper.register("damage", DamageEntity.CODEC);
        helper.register("ignite", IgniteEntity.CODEC);
        helper.register("restore_shield", RestoreShield.CODEC);
        helper.register("apply_effect", ApplyMobEffect.CODEC);
        helper.register("drop_loot", DropLootTable.CODEC);
    }
}