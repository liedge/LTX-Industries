package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.GameEvents.HANDHELD_EQUIPMENT;
import static liedge.ltxindustries.LTXITags.GameEvents.WEAPON_VIBRATIONS;
import static liedge.ltxindustries.registry.game.LTXIGameEvents.PROJECTILE_IMPACT;
import static liedge.ltxindustries.registry.game.LTXIGameEvents.WEAPON_FIRED;
import static net.minecraft.tags.GameEventTags.*;
import static net.minecraft.world.level.gameevent.GameEvent.*;

class GameEventsTagsGen extends LimaTagsProvider.RegistryTags<GameEvent>
{
    GameEventsTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper)
    {
        super(packOutput, BuiltInRegistries.GAME_EVENT, LTXIndustries.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(VIBRATIONS).add(WEAPON_FIRED);
        buildTag(SHRIEKER_CAN_LISTEN).add(PROJECTILE_IMPACT);
        buildTag(WARDEN_CAN_LISTEN).add(WEAPON_FIRED);

        buildTag(WEAPON_VIBRATIONS).add(WEAPON_FIRED, PROJECTILE_IMPACT);
        buildTag(HANDHELD_EQUIPMENT).add(BLOCK_CHANGE, BLOCK_DESTROY, ITEM_INTERACT_START, ITEM_INTERACT_FINISH, SHEAR);
    }
}