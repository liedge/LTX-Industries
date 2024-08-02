package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaLanguageProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

import static liedge.limatech.registry.LimaTechSounds.*;
import static net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE;

class SoundsGen extends SoundDefinitionsProvider
{
    SoundsGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LimaTech.MODID, helper);
    }

    @Override
    public void registerSounds()
    {
        add(GRENADE_LAUNCHER_FIRE);
        addEvent(MISSILE_EXPLODE, GENERIC_EXPLODE);

        GRENADE_SOUNDS.forEach((element, holder) -> {
            if (element == OrbGrenadeElement.EXPLOSIVE)
            {
                addEvent(holder, GENERIC_EXPLODE);
            }
            else
            {
                add(holder);
            }
        });
    }

    private void add(DeferredHolder<SoundEvent, SoundEvent> holder)
    {
        add(holder, SoundDefinition.definition().with(sound(holder.getId())).subtitle(LimaLanguageProvider.soundSubtitleKey(holder)));
    }

    private void addEvent(DeferredHolder<SoundEvent, SoundEvent> holder, SoundEvent targetSound)
    {
        add(holder, SoundDefinition.definition().with(sound(targetSound.getLocation(), SoundDefinition.SoundType.EVENT)).subtitle(LimaLanguageProvider.soundSubtitleKey(holder)));
    }

    private void addEvent(DeferredHolder<SoundEvent, SoundEvent> holder, Holder<SoundEvent> targetHolder)
    {
        add(holder, SoundDefinition.definition().with(sound(targetHolder.getRegisteredName(), SoundDefinition.SoundType.EVENT)).subtitle(LimaLanguageProvider.soundSubtitleKey(holder)));
    }
}