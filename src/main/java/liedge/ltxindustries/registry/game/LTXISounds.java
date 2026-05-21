package liedge.ltxindustries.registry.game;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXISounds
{
    private LTXISounds() {}

    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = LTXIndustries.RESOURCES.deferredRegister(Registries.SOUND_EVENT);

    public static void register(IEventBus bus)
    {
        SOUND_EVENTS.register(bus);
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> UPGRADE_INSTALL = registerSound("upgrade_install");
    public static final DeferredHolder<SoundEvent, SoundEvent> UPGRADE_REMOVE = registerSound("upgrade_remove");
    public static final DeferredHolder<SoundEvent, SoundEvent> EQUIPMENT_MODE_SWITCH = registerSound("equipment_mode_switch");
    public static final DeferredHolder<SoundEvent, SoundEvent> TURRET_TARGET_FOUND = registerSound("turret_target_found");
    public static final DeferredHolder<SoundEvent, SoundEvent> BUBBLE_SHIELD_BREAK = registerSound("shield_break");

    public static final DeferredHolder<SoundEvent, SoundEvent> WAYFINDER_FIRE = registerSound("wayfinder_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> SERENITY_FIRE = registerSound("serenity_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> MIRAGE_FIRE = registerSound("mirage_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> AURORA_FIRE = registerSound("aurora_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> HANABI_FIRE = registerSound("hanabi_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> STARGAZER_CHARGE = registerSound("stargazer_charge");
    public static final DeferredHolder<SoundEvent, SoundEvent> STARGAZER_FIRE = registerSound("stargazer_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> DAYBREAK_FIRE = registerSound("daybreak_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> NOVA_FIRE = registerSound("nova_fire");
    
    public static final DeferredHolder<SoundEvent, SoundEvent> ROCKET_EXPLODE = registerSound("rocket_explode");
    public static final DeferredHolder<SoundEvent, SoundEvent> EXPLOSIVE_SHELL_IMPACT = registerSound("explosive_shell_impact");
    public static final DeferredHolder<SoundEvent, SoundEvent> FLAME_SHELL_IMPACT = registerSound("flame_shell_impact");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRYO_SHELL_IMPACT = registerSound("cryo_shell_impact");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELECTRIC_SHELL_IMPACT = registerSound("electric_shell_impact");
    public static final DeferredHolder<SoundEvent, SoundEvent> ACID_SHELL_IMPACT = registerSound("acid_shell_impact");
    public static final DeferredHolder<SoundEvent, SoundEvent> GLOOM_GAS_SHELL_IMPACT = registerSound("gloom_gas_shell_impact");

    public static final DeferredHolder<SoundEvent, SoundEvent> ROCKET_TURRET_FIRE = registerSound("rocket_turret_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> RAILGUN_TURRET_FIRE = registerSound("railgun_turret_fire");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name)
    {
        return SOUND_EVENTS.register(name, SoundEvent::createVariableRangeEvent);
    }
}