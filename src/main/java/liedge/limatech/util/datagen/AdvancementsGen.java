package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaAdvancementGenerator;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

class AdvancementsGen extends LimaAdvancementGenerator
{
    AdvancementsGen() { }

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper helper)
    {
    }
}