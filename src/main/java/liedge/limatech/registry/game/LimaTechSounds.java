package liedge.limatech.registry.game;

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

    public static void register(IEventBus bus)
    {
        SOUND_EVENTS.register(bus);
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> WEAPON_MODE_SWITCH = registerSound("weapon_mode_switch");
    public static final DeferredHolder<SoundEvent, SoundEvent> TURRET_TARGET_FOUND = registerSound("turret_target_found");

    public static final DeferredHolder<SoundEvent, SoundEvent> SUBMACHINE_GUN_LOOP = registerSound("submachine_gun_loop");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOTGUN_FIRE = registerSound("shotgun_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> GRENADE_LAUNCHER_FIRE = registerSound("grenade_launcher_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROCKET_LAUNCHER_FIRE = registerSound("rocket_launcher_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAGNUM_FIRE = registerSound("magnum_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> ROCKET_EXPLODE = registerSound("rocket_explode");
    public static final Map<GrenadeType, DeferredHolder<SoundEvent, SoundEvent>> GRENADE_EXPLOSIONS = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(GrenadeType.class, e -> registerSound(e.getSerializedName() + "_grenade_explode"));
    public static final DeferredHolder<SoundEvent, SoundEvent> RAILGUN_BOOM = registerSound("railgun_boom");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name)
    {
        return SOUND_EVENTS.register(name, SoundEvent::createVariableRangeEvent);
    }
}