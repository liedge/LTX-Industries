package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaSoundDefinitionsProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.SoundDefinition;

import static liedge.ltxindustries.registry.game.LTXISounds.*;
import static net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE;
import static net.minecraft.sounds.SoundEvents.WARDEN_SONIC_BOOM;

class SoundsGen extends LimaSoundDefinitionsProvider
{
    SoundsGen(PackOutput output)
    {
        super(output, LTXIndustries.RESOURCES);
    }

    @Override
    public void registerSounds()
    {
        addSingleDirectSound(UPGRADE_INSTALL);
        addSingleDirectSound(UPGRADE_REMOVE);
        addSingleDirectSound(EQUIPMENT_MODE_SWITCH);
        addSingleDirectSound(TURRET_TARGET_FOUND);
        addSingleDirectSound(BUBBLE_SHIELD_BREAK);

        addSound(WAYFINDER_FIRE, def -> def.with(beginSound(HANABI_FIRE, SoundDefinition.SoundType.SOUND).pitch(1.25f)));
        addSound(SERENITY_FIRE, def -> def.with(beginSound("auto_weapon_fire", SoundDefinition.SoundType.SOUND).pitch(0.99f)));
        addSound(MIRAGE_FIRE, def -> def.with(beginSound("auto_weapon_fire", SoundDefinition.SoundType.SOUND).pitch(0.925f)));
        addSingleDirectSound(AURORA_FIRE);
        addSingleDirectSound(HANABI_FIRE);
        addSingleDirectSound(STARGAZER_CHARGE);
        addSound(STARGAZER_FIRE, def -> def
                .with(beginSound("stargazer_fire_0", SoundDefinition.SoundType.SOUND))
                .with(beginSound("stargazer_fire_1", SoundDefinition.SoundType.SOUND)));
        addSingleDirectSound(DAYBREAK_FIRE);
        addSingleDirectSound(NOVA_FIRE);

        addSingleEventSound(ROCKET_EXPLODE, GENERIC_EXPLODE);
        addSingleEventSound(EXPLOSIVE_SHELL_IMPACT, GENERIC_EXPLODE);
        addSingleDirectSound(FLAME_SHELL_IMPACT);
        addSingleDirectSound(CRYO_SHELL_IMPACT);
        addSingleDirectSound(ELECTRIC_SHELL_IMPACT);
        addSingleDirectSound(ACID_SHELL_IMPACT);
        addSingleDirectSound(GLOOM_GAS_SHELL_IMPACT);

        addSingleEventSound(ROCKET_TURRET_FIRE, DAYBREAK_FIRE);
        addSingleEventSound(RAILGUN_TURRET_FIRE, WARDEN_SONIC_BOOM);
     }
}