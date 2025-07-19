package liedge.ltxindustries.block.mesh;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXIBlockMeshes
{
    private LTXIBlockMeshes() {}

    public static @Nullable BlockMesh getBlockMesh(ResourceLocation id)
    {
        return REGISTRY.get(id);
    }

    public static ResourceLocation getBlockMeshId(BlockMesh mesh)
    {
        return Objects.requireNonNull(REGISTRY.inverse().get(mesh), "Unregistered mesh.");
    }

    public static final ResourceLocation DOUBLE_VERTICAL = RESOURCES.location("double_vertical");
    public static final ResourceLocation TRIPLE_VERTICAL = RESOURCES.location("triple_vertical");

    private static final BiMap<ResourceLocation, BlockMesh> REGISTRY = Util.make(() -> {
        ImmutableBiMap.Builder<ResourceLocation, BlockMesh> builder = new ImmutableBiMap.Builder<>();

        BlockMesh doubleVertical = BlockMesh.builder().add(0, 0, 0, BlockMeshPartType.PRIMARY).add(0, 1, 0, BlockMeshPartType.DUMMY).build();
        BlockMesh tripleVertical = BlockMesh.builder().add(0, 0, 0, BlockMeshPartType.PRIMARY).add(0, 1, 0).add(0, 2, 0).build();

        builder.put(DOUBLE_VERTICAL, doubleVertical);
        builder.put(TRIPLE_VERTICAL, tripleVertical);

        return builder.build();
    });
}