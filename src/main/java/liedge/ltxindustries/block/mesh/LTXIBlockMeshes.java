package liedge.ltxindustries.block.mesh;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import liedge.ltxindustries.LTXIIdentifiers;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.slf4j.Logger;

public final class LTXIBlockMeshes
{
    private LTXIBlockMeshes() {}

    private static final Logger LOG = LogUtils.getLogger();
    private static final ExtraCodecs.LateBoundIdMapper<Identifier, BlockMesh> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
    public static final Codec<BlockMesh> CODEC = ID_MAPPER.codec(Identifier.CODEC);

    // note to self: neg x is right, pos z is forward (rel to player facing when place)

    public static final BlockMesh FABRICATOR = BlockMesh.builder(LTXIIdentifiers.ID_FABRICATOR)
            .add(0, 0, 0, BlockMeshPartType.PRIMARY)
            .add(0, 1, 0)
            .add(-1, 0, 0)
            .add(-1, 1, 0)
            .build();
    public static final BlockMesh DIGITAL_GARDEN = BlockMesh.builder(LTXIIdentifiers.ID_DIGITAL_GARDEN).add(0, 0, 0, BlockMeshPartType.PRIMARY).add(0, 1, 0).build();
    public static final BlockMesh TURRET = BlockMesh.builder("turret").add(0, 0, 0, BlockMeshPartType.PRIMARY).add(0, 1, 0).build();

    public static void bootstrap()
    {
        register(FABRICATOR);
        register(DIGITAL_GARDEN);
        register(TURRET);

        LOG.info("Initialized {} block meshes", ID_MAPPER.values().size());
    }

    private static void register(BlockMesh mesh)
    {
        ID_MAPPER.put(mesh.getId(), mesh);
    }
}