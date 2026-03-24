package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaJsonCodecProvider;
import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.data.LTXIReloadListeners;
import liedge.ltxindustries.lib.EquipmentDamageModifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.EntityTypes.HIGH_THREAT_TARGETS;
import static liedge.ltxindustries.LTXITags.EntityTypes.MEDIUM_THREAT_TARGETS;
import static liedge.ltxindustries.LTXITags.Items.LIGHTWEIGHT_WEAPONS;
import static liedge.ltxindustries.LTXITags.Items.SPECIALIST_WEAPONS;

class DamageModsGen extends LimaJsonCodecProvider<EquipmentDamageModifier>
{
    DamageModsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, PackOutput.Target.DATA_PACK, LTXIReloadListeners.EQUIPMENT_DAMAGE_MODIFIERS.getPath(), EquipmentDamageModifier.CODEC, registries, LTXIndustries.RESOURCES);
    }

    private void add(String name, EquipmentDamageModifier.Builder builder)
    {
        unconditional(name, builder.build());
    }

    @Override
    protected void gather(HolderLookup.Provider registries)
    {
        HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);
        HolderGetter<EntityType<?>> entities = registries.lookupOrThrow(Registries.ENTITY_TYPE);

        add("lightweight/high_threat", EquipmentDamageModifier.builder(-0.8f, MathOperation.ADD_PERCENT_OF_TOTAL).forEquipmentTag(items, LIGHTWEIGHT_WEAPONS).againstEntities(entities, HIGH_THREAT_TARGETS));
        add("lightweight/medium_threat", EquipmentDamageModifier.builder(-0.5f, MathOperation.ADD_PERCENT_OF_TOTAL).forEquipmentTag(items, LIGHTWEIGHT_WEAPONS).againstEntities(entities, MEDIUM_THREAT_TARGETS));
        add("specialist/high_threat", EquipmentDamageModifier.builder(-0.4f, MathOperation.ADD_PERCENT_OF_TOTAL).forEquipmentTag(items, SPECIALIST_WEAPONS).againstEntities(entities, HIGH_THREAT_TARGETS));
        add("specialist/medium_threat", EquipmentDamageModifier.builder(-0.25f, MathOperation.ADD_PERCENT_OF_TOTAL).forEquipmentTag(items, SPECIALIST_WEAPONS).againstEntities(entities, MEDIUM_THREAT_TARGETS));
    }
}