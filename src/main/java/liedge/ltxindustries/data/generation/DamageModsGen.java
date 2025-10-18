package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaJsonCodecProvider;
import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.EquipmentDamageModifier;
import liedge.ltxindustries.lib.EquipmentDamageModifiers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.EntityTypes.HIGH_THREAT_TARGETS;
import static liedge.ltxindustries.LTXITags.EntityTypes.MEDIUM_THREAT_TARGETS;
import static liedge.ltxindustries.LTXITags.Items.LIGHTWEIGHT_WEAPONS;
import static liedge.ltxindustries.LTXITags.Items.SPECIALIST_WEAPONS;

class DamageModsGen extends LimaJsonCodecProvider<EquipmentDamageModifier>
{
    DamageModsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper)
    {
        super(output, PackOutput.Target.DATA_PACK, EquipmentDamageModifiers.DIRECTORY, PackType.SERVER_DATA, EquipmentDamageModifier.DIRECT_CODEC, registries, LTXIndustries.RESOURCES, helper);
    }

    private void add(String name, EquipmentDamageModifier.Builder builder)
    {
        unconditional(name, builder.build());
    }

    @Override
    protected void gather(HolderLookup.Provider registries)
    {
        add("lightweight/high_threat", EquipmentDamageModifier.builder(-0.8f, MathOperation.ADD_PERCENT_OF_TOTAL).forEquipmentTag(LIGHTWEIGHT_WEAPONS).againstEntities(HIGH_THREAT_TARGETS));
        add("lightweight/medium_threat", EquipmentDamageModifier.builder(-0.5f, MathOperation.ADD_PERCENT_OF_TOTAL).forEquipmentTag(LIGHTWEIGHT_WEAPONS).againstEntities(MEDIUM_THREAT_TARGETS));
        add("specialist/high_threat", EquipmentDamageModifier.builder(-0.4f, MathOperation.ADD_PERCENT_OF_TOTAL).forEquipmentTag(SPECIALIST_WEAPONS).againstEntities(HIGH_THREAT_TARGETS));
        add("specialist/medium_threat", EquipmentDamageModifier.builder(-0.25f, MathOperation.ADD_PERCENT_OF_TOTAL).forEquipmentTag(SPECIALIST_WEAPONS).againstEntities(MEDIUM_THREAT_TARGETS));
    }
}