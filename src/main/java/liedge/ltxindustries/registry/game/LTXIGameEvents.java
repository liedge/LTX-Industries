package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXIGameEvents
{
    private LTXIGameEvents() {}

    private static final DeferredRegister<GameEvent> GAME_EVENTS = LTXIndustries.RESOURCES.deferredRegister(Registries.GAME_EVENT);

    public static void register(IEventBus bus)
    {
        GAME_EVENTS.register(bus);
    }

    public static final DeferredHolder<GameEvent, GameEvent> WEAPON_FIRED = GAME_EVENTS.register("weapon_fired", () -> new GameEvent(32));
    public static final DeferredHolder<GameEvent, GameEvent> PROJECTILE_EXPLODED = GAME_EVENTS.register("projectile_exploded", () -> new GameEvent(32));
}