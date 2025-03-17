package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaSoundDefinitionsProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.lib.weapons.GrenadeType;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static liedge.limatech.registry.LimaTechSounds.*;
import static net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE;

class SoundsGen extends LimaSoundDefinitionsProvider
{
    SoundsGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LimaTech.RESOURCES, helper);
    }

    @Override
    public void registerSounds()
    {
        addSingleDirectSound(WEAPON_MODE_SWITCH);
        addSingleDirectSound(TURRET_TARGET_FOUND);

        addSingleDirectSound(SUBMACHINE_GUN_LOOP);
        addSingleDirectSound(SHOTGUN_FIRE);
        addSingleDirectSound(GRENADE_LAUNCHER_FIRE);
        addSingleDirectSound(ROCKET_LAUNCHER_FIRE);
        addSingleDirectSound(MAGNUM_FIRE);
        addSingleEventRedirectSound(ROCKET_EXPLODE, GENERIC_EXPLODE);

        GRENADE_EXPLOSIONS.forEach((element, holder) -> {
            if (element == GrenadeType.EXPLOSIVE)
            {
                addSingleEventRedirectSound(holder, GENERIC_EXPLODE);
            }
            else
            {
                addSingleDirectSound(holder);
            }
        });
    }
}