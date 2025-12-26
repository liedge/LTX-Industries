package liedge.ltxindustries.registry.game;

import com.mojang.serialization.Codec;
import liedge.limacore.util.LimaCoreUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.TurretTargetList;
import liedge.ltxindustries.lib.shield.PlayerBubbleShield;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import liedge.ltxindustries.lib.weapons.ServerExtendedInput;
import net.minecraft.network.codec.ByteBufCodecs;
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

    // Persistent data
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Float>> BUBBLE_SHIELD_HEALTH = ATTACHMENTS.register("shield_health", () -> AttachmentType.builder(() -> 0f).serialize(Codec.FLOAT).sync(ByteBufCodecs.FLOAT).build());

    // Transient 'live'/functional attachments
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerBubbleShield>> PLAYER_SHIELD = ATTACHMENTS.register("player_shield", () -> AttachmentType.builder(PlayerBubbleShield::new).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LTXIExtendedInput>> INPUT_EXTENSIONS = ATTACHMENTS.register("input", () -> AttachmentType.builder(holder -> {
        Player player = LimaCoreUtil.castOrThrow(Player.class, holder, () -> new IllegalStateException("LTXI input extensions can only be added to players."));
        return player.level().isClientSide() ? new ClientExtendedInput() : new ServerExtendedInput();
    }).build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TurretTargetList>> TARGET_LIST = ATTACHMENTS.register("target_list", () -> AttachmentType.builder(TurretTargetList::create).build());
}