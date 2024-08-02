package liedge.limatech.registry;

import liedge.limacore.util.LimaCollectionsUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

public final class LimaTechSounds
{
    private LimaTechSounds() {}

    private static final DeferredRegister<SoundEvent> SOUNDS = LimaTech.RESOURCES.deferredRegister(Registries.SOUND_EVENT);

    public static void initRegister(IEventBus bus)
    {
        SOUNDS.register(bus);
    }

    public static final DeferredHolder<SoundEvent, SoundEvent> GRENADE_LAUNCHER_FIRE = register("grenade_launcher_fire");
    public static final DeferredHolder<SoundEvent, SoundEvent> MISSILE_EXPLODE = register("missile_explode");
    public static final Map<OrbGrenadeElement, DeferredHolder<SoundEvent, SoundEvent>> GRENADE_SOUNDS = LimaCollectionsUtil.immutableEnumMapFor(OrbGrenadeElement.class, e -> register(e.getSerializedName() + "_grenade_explode"));

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name)
    {
        return SOUNDS.register(name, SoundEvent::createVariableRangeEvent);
    }
}