package liedge.ltxindustries.lib;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.data.LTXIReloadListeners;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;
import java.util.Map;

public final class EquipmentDamageModifiers extends SimpleJsonResourceReloadListener<EquipmentDamageModifier>
{
    private ObjectList<EquipmentDamageModifier> modifiers = ObjectLists.emptyList();

    public EquipmentDamageModifiers()
    {
        super(EquipmentDamageModifier.CODEC, FileToIdConverter.json(LTXIReloadListeners.EQUIPMENT_DAMAGE_MODIFIERS.getPath()));
    }

    @Override
    protected void apply(Map<Identifier, EquipmentDamageModifier> object, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        ObjectList<EquipmentDamageModifier> list = new ObjectArrayList<>(object.values());
        this.modifiers = ObjectLists.unmodifiable(list);

        LTXIndustries.LOGGER.info("Loaded {} equipment damage modifiers.", modifiers.size());
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
}