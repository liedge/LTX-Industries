package liedge.ltxindustries.registry.game;

import liedge.limacore.util.LimaCoreUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.StandaloneBubbleShield;
import liedge.ltxindustries.lib.TurretTargetList;
import liedge.ltxindustries.lib.weapons.AbstractWeaponControls;
import liedge.ltxindustries.lib.weapons.ClientWeaponControls;
import liedge.ltxindustries.lib.weapons.ServerWeaponControls;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class LTXIAttachmentTypes
{
    private LTXIAttachmentTypes() {}

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = LTXIndustries.RESOURCES.deferredRegister(NeoForgeRegistries.ATTACHMENT_TYPES);

    public static void register(IEventBus bus)
    {
        ATTACHMENTS.register(bus);
    }

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<StandaloneBubbleShield>> BUBBLE_SHIELD = ATTACHMENTS.register("bubble_shield", () -> AttachmentType.serializable(StandaloneBubbleShield::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AbstractWeaponControls>> WEAPON_CONTROLS = ATTACHMENTS.register("weapon_controls", () -> AttachmentType.builder(holder -> {
        Player player = LimaCoreUtil.castOrThrow(Player.class, holder, () -> new IllegalStateException("Weapon controls attachment can only be added to players."));
        return player.level().isClientSide() ? new ClientWeaponControls() : new ServerWeaponControls();
    }).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TurretTargetList>> TARGET_LIST = ATTACHMENTS.register("target_list", () -> AttachmentType.builder(TurretTargetList::create).build());
}