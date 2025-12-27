package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public record ModularTool(Optional<HolderSet<Block>> effective, Optional<HolderSet<Block>> limit)
{
    public static final Codec<ModularTool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("effective").forGetter(ModularTool::effective),
            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("limit").forGetter(ModularTool::limit))
            .apply(instance, ModularTool::new));

    public static ModularTool limitedTo(HolderSet<Block> limit)
    {
        return new ModularTool(Optional.empty(), Optional.of(limit));
    }

    public static ModularTool effectiveOn(HolderSet<Block> effective)
    {
        return new ModularTool(Optional.of(effective), Optional.empty());
    }
}