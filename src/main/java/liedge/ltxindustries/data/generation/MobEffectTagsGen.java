package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.effect.MobEffects.*;

class MobEffectTagsGen extends LimaTagsProvider.RegistryTags<MobEffect>
{
    MobEffectTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, BuiltInRegistries.MOB_EFFECT, LTXIndustries.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(LTXITags.MobEffects.VISION_DEBUFF).add(CONFUSION, BLINDNESS, DARKNESS);
        buildTag(LTXITags.MobEffects.MOVEMENT_DEBUFF).add(MOVEMENT_SLOWDOWN, LEVITATION);
    }
}