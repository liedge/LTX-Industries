package liedge.limatech.registry;

import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.lib.weapons.GrenadeType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

public final class LimaTechSounds
{
    private LimaTechSounds() {}

    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = LimaTech.RESOURCES.deferredRegister(Registries.SOUND_EVENT);

    public static void initRegister(IEventBus bus)
    {
        SOUND_EVENTS.register(bus);
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> WEAPON_MODE_SWITCH = register("weapon_mode_switch");

    public static final DeferredHolder<SoundEvent, SoundEvent> SUBMACHINE_GUN_LOOP = register("submachine_gun_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOTGUN_FIRE = register("shotgun_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> GRENADE_LAUNCHER_FIRE = register("grenade_launcher_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROCKET_LAUNCHER_FIRE = register("rocket_launcher_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAGNUM_FIRE = register("magnum_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> MISSILE_EXPLODE = register("missile_explode");
    public static final Map<GrenadeType, DeferredHolder<SoundEvent, SoundEvent>> GRENADE_SOUNDS = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(GrenadeType.class, e -> register(e.getSerializedName() + "_grenade_explode"));

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name)
    {
        return SOUND_EVENTS.register(name, SoundEvent::createVariableRangeEvent);
    }
}