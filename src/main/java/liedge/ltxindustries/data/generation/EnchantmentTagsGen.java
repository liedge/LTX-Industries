package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.registry.bootstrap.LTXIEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.CompletableFuture;

class EnchantmentTagsGen extends LimaTagsProvider<Enchantment>
{
    protected EnchantmentTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(packOutput, Registries.ENCHANTMENT, LTXIndustries.MODID, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(EnchantmentTags.NON_TREASURE).add(LTXIEnchantments.RAZOR, LTXIEnchantments.AMMO_SCAVENGER);
    }
}