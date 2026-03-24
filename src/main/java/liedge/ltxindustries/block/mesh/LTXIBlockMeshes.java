package liedge.ltxindustries.block.mesh;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXIBlockMeshes
{
    private LTXIBlockMeshes() {}

    public static @Nullable BlockMesh getBlockMesh(Identifier id)
    {
        return REGISTRY.get(id);
    }

    public static Identifier getBlockMeshId(BlockMesh mesh)
    {
        return Objects.requireNonNull(REGISTRY.inverse().get(mesh), "Unregistered mesh.");
    }

    public static final Identifier WIDE_STATION = RESOURCES.id("wide_station");
    public static final Identifier DOUBLE_VERTICAL = RESOURCES.id("double_vertical");

    private static final BiMap<Identifier, BlockMesh> REGISTRY = Util.make(() -> {
        ImmutableBiMap.Builder<Identifier, BlockMesh> builder = new ImmutableBiMap.Builder<>();

        BlockMesh wideStation = BlockMesh.builder().add(0, 0, 0, BlockMeshPartType.PRIMARY).add(0, 1, 0).add(-1, 0, 0).add(-1, 1, 0).build();
        BlockMesh doubleVertical = BlockMesh.builder().add(0, 0, 0, BlockMeshPartType.PRIMARY).add(0, 1, 0).build();

        builder.put(WIDE_STATION, wideStation);
        builder.put(DOUBLE_VERTICAL, doubleVertical);

        return builder.build();
    });
}