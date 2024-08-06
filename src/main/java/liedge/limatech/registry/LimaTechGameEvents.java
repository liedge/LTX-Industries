package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechGameEvents
{
    private LimaTechGameEvents() {}

    private static final DeferredRegister<GameEvent> GAME_EVENTS = LimaTech.RESOURCES.deferredRegister(Registries.GAME_EVENT);

    public static void initRegister(IEventBus bus)
    {
        GAME_EVENTS.register(bus);
    }

    public static final DeferredHolder<GameEvent, GameEvent> WEAPON_FIRED = GAME_EVENTS.register("weapon_fired", () -> new GameEvent(32));
    public static final DeferredHolder<GameEvent, GameEvent> PROJECTILE_EXPLODED = GAME_EVENTS.register("projectile_exploded", () -> new GameEvent(32));
}