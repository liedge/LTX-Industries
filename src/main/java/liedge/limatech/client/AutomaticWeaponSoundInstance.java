package liedge.limatech.client;

import liedge.limatech.registry.LimaTechAttachmentTypes;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class AutomaticWeaponSoundInstance extends AbstractTickableSoundInstance
{
    private final Player player;
    private boolean shouldStop = false;
    private int echoTicks = 1;

    public AutomaticWeaponSoundInstance(Holder<SoundEvent> soundHolder, Player player)
    {
        super(soundHolder.value(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.looping = true;
        this.volume = 0.8f;
    }

    @Override
    public void tick()
    {
        if (!player.isRemoved())
        {
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();

            if (!shouldStop && !player.getData(LimaTechAttachmentTypes.WEAPON_CONTROLS).isTriggerHeld())
            {
                shouldStop = true;
            }
            else if (shouldStop)
            {
                if (echoTicks > 0)
                {
                    echoTicks--;
                }
                else
                {
                    stop();
                }
            }
        }
        else
        {
            stop();
        }
    }
}