package liedge.ltxindustries.registry.game;

import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.advancements.criterion.GrenadeElementSubPredicate;
import liedge.ltxindustries.advancements.criterion.HomingTargetSubPredicate;
import net.minecraft.advancements.criterion.EntitySubPredicate;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.ApiStatus;

public final class LTXILootRegistries
{
    private LTXILootRegistries() {}

    @ApiStatus.Internal
    public static void register(RegisterEvent event, ModResources resources)
    {
        resources.registerByEvent(Registries.ENTITY_SUB_PREDICATE_TYPE, event, LTXILootRegistries::registerSubPredicates);
    }

    private static void registerSubPredicates(ModResources.RegisterHelper<MapCodec<? extends EntitySubPredicate>> helper)
    {
        helper.register("grenade_element", GrenadeElementSubPredicate.CODEC);
        helper.register("homing_target", HomingTargetSubPredicate.CODEC);
    }
}