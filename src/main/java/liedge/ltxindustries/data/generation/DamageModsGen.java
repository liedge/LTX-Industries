package liedge.ltxindustries.data.generation;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.lib.math.MathOperation;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.lib.EquipmentDamageModifier;
import liedge.ltxindustries.lib.EquipmentDamageModifiers;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.WithConditions;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.EntityTypes.HIGH_THREAT_TARGETS;
import static liedge.ltxindustries.LTXITags.EntityTypes.MEDIUM_THREAT_TARGETS;
import static liedge.ltxindustries.LTXITags.Items.*;

class DamageModsGen extends JsonCodecProvider<EquipmentDamageModifier>
{
    DamageModsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper)
    {
        super(output, PackOutput.Target.DATA_PACK, EquipmentDamageModifiers.DIRECTORY, PackType.SERVER_DATA, EquipmentDamageModifier.DIRECT_CODEC, registries, LTXIndustries.MODID, helper);
    }

    private void add(String name, EquipmentDamageModifier.Builder builder)
    {
        unconditional(LTXIndustries.RESOURCES.location(name), builder.build());
    }

    @Override
    protected void gather()
    {
        add("lightweight/high_threat", EquipmentDamageModifier.builder(-0.8f, MathOperation.ADD_TOTAL_PERCENT).forEquipmentTag(LIGHTWEIGHT_WEAPONS).againstEntities(HIGH_THREAT_TARGETS));
        add("lightweight/medium_threat", EquipmentDamageModifier.builder(-0.5f, MathOperation.ADD_TOTAL_PERCENT).forEquipmentTag(LIGHTWEIGHT_WEAPONS).againstEntities(MEDIUM_THREAT_TARGETS));
        add("specialist/high_threat", EquipmentDamageModifier.builder(-0.4f, MathOperation.ADD_TOTAL_PERCENT).forEquipmentTag(SPECIALIST_WEAPONS).againstEntities(HIGH_THREAT_TARGETS));
        add("specialist/medium_threat", EquipmentDamageModifier.builder(-0.25f, MathOperation.ADD_TOTAL_PERCENT).forEquipmentTag(SPECIALIST_WEAPONS).againstEntities(MEDIUM_THREAT_TARGETS));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        return lookupProvider.thenCompose(registries -> run(cache, registries));
    }

    private CompletableFuture<?> run(CachedOutput cache, HolderLookup.Provider registries)
    {
        List<CompletableFuture<?>> futures = new ObjectArrayList<>();
        DynamicOps<JsonElement> ops = new ConditionalOps<>(registries.createSerializationContext(JsonOps.INSTANCE), ICondition.IContext.EMPTY);
        Codec<Optional<WithConditions<EquipmentDamageModifier>>> wrappedCodec = ConditionalOps.createConditionalCodecWithConditions(EquipmentDamageModifier.DIRECT_CODEC);
        gather();

        for (Map.Entry<ResourceLocation, WithConditions<EquipmentDamageModifier>> entry : conditions.entrySet())
        {
            Path path = pathProvider.json(entry.getKey());

            CompletableFuture<?> future = CompletableFuture
                    .supplyAsync(() -> LimaCoreCodecs.strictEncode(wrappedCodec, ops, Optional.of(entry.getValue())))
                    .thenComposeAsync(json -> DataProvider.saveStable(cache, json, path));

            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
}