package liedge.ltxindustries.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.common.conditions.ConditionalOps;

import java.util.List;
import java.util.Map;

public final class EquipmentDamageModifiers extends SimpleJsonResourceReloadListener
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final EquipmentDamageModifiers INSTANCE = new EquipmentDamageModifiers();

    public static final String DIRECTORY = "equipment_damage_modifier";

    public static EquipmentDamageModifiers getInstance()
    {
        return INSTANCE;
    }

    private ObjectList<EquipmentDamageModifier> modifiers = ObjectLists.emptyList();

    private EquipmentDamageModifiers()
    {
        super(GSON, DIRECTORY);
    }

    public double apply(ItemStack stack, LootContext context, double baseDamage, double totalDamage)
    {
        List<EquipmentDamageModifier> toApply = modifiers.stream()
                .filter(o -> o.test(stack, context))
                .sorted()
                .toList();

        for (EquipmentDamageModifier modifier : toApply)
        {
            totalDamage = modifier.operation().applyCompoundingDouble(totalDamage, baseDamage, modifier.value().getFloat(context));
        }

        return totalDamage;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        ConditionalOps<JsonElement> ops = makeConditionalOps();
        ObjectList<EquipmentDamageModifier> list = new ObjectArrayList<>();

        // Read all modifiers
        for (JsonElement json : object.values())
        {
            EquipmentDamageModifier modifier = LimaCoreCodecs.tryFlatDecode(EquipmentDamageModifier.CODEC, ops, json);
            if (modifier != null) list.add(modifier);
        }

        LTXIndustries.LOGGER.info("Loaded {} equipment damage modifiers.", list.size());
        this.modifiers = ObjectLists.unmodifiable(list);
    }
}