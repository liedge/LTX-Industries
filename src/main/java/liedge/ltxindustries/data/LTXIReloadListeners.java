package liedge.ltxindustries.data;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.EquipmentDamageModifiers;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@EventBusSubscriber(modid = LTXIndustries.MODID)
public final class LTXIReloadListeners
{
    // Identifiers
    public static final Identifier BUBBLE_SHIELD_MODEL = LTXIndustries.RESOURCES.id("bubble_shield_model");
    public static final Identifier EQUIPMENT_DAMAGE_MODIFIERS = LTXIndustries.RESOURCES.id("equipment_damage_modifier");

    private static @Nullable EquipmentDamageModifiers damageModifiers;

    @SubscribeEvent
    public static void register(final AddServerReloadListenersEvent event)
    {
        event.addListener(EQUIPMENT_DAMAGE_MODIFIERS, damageModifiers = new EquipmentDamageModifiers());
    }

    public static EquipmentDamageModifiers equipmentDamageModifiers()
    {
        return Objects.requireNonNull(damageModifiers, "Equipment damage modifiers not yet initialized");
    }
}