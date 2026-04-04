package liedge.ltxindustries.block.mesh;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import liedge.ltxindustries.LTXIIdentifiers;
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

    public static final Identifier FABRICATOR = RESOURCES.id(LTXIIdentifiers.ID_FABRICATOR);
    public static final Identifier DIGITAL_GARDEN = RESOURCES.id(LTXIIdentifiers.ID_DIGITAL_GARDEN);
    public static final Identifier TURRET = RESOURCES.id("turret");

    private static final BiMap<Identifier, BlockMesh> REGISTRY = Util.make(() -> {
        ImmutableBiMap.Builder<Identifier, BlockMesh> builder = new ImmutableBiMap.Builder<>();

        BlockMesh fabricator = BlockMesh.builder().add(0, 0, 0, BlockMeshPartType.PRIMARY).add(0, 1, 0).add(-1, 0, 0).add(-1, 1, 0).build();
        BlockMesh digitalGarden = BlockMesh.builder().add(0, 0, 0, BlockMeshPartType.PRIMARY).add(0, 1, 0).build();
        BlockMesh turret = BlockMesh.builder().add(0, 0, 0, BlockMeshPartType.PRIMARY).add(0, 1, 0).build();

        builder.put(FABRICATOR, fabricator);
        builder.put(DIGITAL_GARDEN, digitalGarden);
        builder.put(TURRET, turret);

        return builder.build();
    });
}