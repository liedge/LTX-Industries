package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import liedge.limatech.lib.StandaloneBubbleShield;
import liedge.limatech.lib.weapons.ServerWeaponInput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class LimaTechAttachmentTypes
{
    private LimaTechAttachmentTypes() {}

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = LimaTech.RESOURCES.deferredRegister(NeoForgeRegistries.ATTACHMENT_TYPES);

    public static void initRegister(IEventBus bus)
    {
        ATTACHMENTS.register(bus);
    }

    public static final Supplier<AttachmentType<StandaloneBubbleShield>> BUBBLE_SHIELD = ATTACHMENTS.register("bubble_shield", () -> AttachmentType.serializable(StandaloneBubbleShield::new).build());
    public static final Supplier<AttachmentType<ServerWeaponInput>> WEAPON_INPUT = ATTACHMENTS.register("weapon_input", () -> AttachmentType.builder(ServerWeaponInput::new).build());
}