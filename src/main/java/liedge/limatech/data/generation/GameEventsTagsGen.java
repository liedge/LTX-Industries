package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.registry.game.LimaTechGameEvents.PROJECTILE_EXPLODED;
import static liedge.limatech.registry.game.LimaTechGameEvents.WEAPON_FIRED;
import static net.minecraft.tags.GameEventTags.SHRIEKER_CAN_LISTEN;
import static net.minecraft.tags.GameEventTags.VIBRATIONS;

class GameEventsTagsGen extends LimaTagsProvider.RegistryTags<GameEvent>
{
    GameEventsTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper)
    {
        super(packOutput, BuiltInRegistries.GAME_EVENT, LimaTech.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(VIBRATIONS).add(WEAPON_FIRED, PROJECTILE_EXPLODED);
        buildTag(SHRIEKER_CAN_LISTEN).add(PROJECTILE_EXPLODED);
    }
}