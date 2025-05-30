package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaSoundDefinitionsProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.lib.weapons.GrenadeType;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;

import static liedge.limatech.registry.game.LimaTechSounds.*;
import static net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE;
import static net.minecraft.sounds.SoundEvents.WARDEN_SONIC_BOOM;

class SoundsGen extends LimaSoundDefinitionsProvider
{
    SoundsGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LimaTech.RESOURCES, helper);
    }

    @Override
    public void registerSounds()
    {
        addSingleDirectSound(UPGRADE_INSTALL);
        addSingleDirectSound(UPGRADE_REMOVE);
        addSingleDirectSound(WEAPON_MODE_SWITCH);
        addSingleDirectSound(TURRET_TARGET_FOUND);

        addSingleDirectSound(SUBMACHINE_GUN_LOOP);
        addSingleDirectSound(SHOTGUN_FIRE);
        addSingleDirectSound(GRENADE_LAUNCHER_FIRE);
        addSingleDirectSound(LINEAR_FUSION_CHARGE);
        addSound(LINEAR_FUSION_FIRE, def -> def
                .with(beginSound("linear_fusion_fire0", SoundDefinition.SoundType.SOUND))
                .with(beginSound("linear_fusion_fire1", SoundDefinition.SoundType.SOUND)));
        addSingleDirectSound(ROCKET_LAUNCHER_FIRE);
        addSingleDirectSound(MAGNUM_FIRE);
        addSingleEventSound(ROCKET_EXPLODE, GENERIC_EXPLODE);

        GRENADE_EXPLOSIONS.forEach((element, holder) -> {
            if (element == GrenadeType.EXPLOSIVE)
            {
                addSingleEventSound(holder, GENERIC_EXPLODE);
            }
            else
            {
                addSingleDirectSound(holder);
            }
        });

        addSingleEventSound(RAILGUN_BOOM, WARDEN_SONIC_BOOM);
     }
}