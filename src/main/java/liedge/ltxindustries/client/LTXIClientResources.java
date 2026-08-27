package liedge.ltxindustries.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.custom.BubbleShieldModel;
import liedge.ltxindustries.client.renderer.BubbleShieldRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Consumer;

public final class LTXIClientResources implements ResourceManagerReloadListener
{
    public static final LTXIClientResources INSTANCE = new LTXIClientResources();
    private static final Logger LOGGER = LogUtils.getLogger();

    private final BubbleShieldRenderer shieldRenderer = new BubbleShieldRenderer();

    private LTXIClientResources() { }

    public BubbleShieldRenderer getShieldRenderer()
    {
        return shieldRenderer;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager)
    {
        readWithCodec(manager, "core/bubble_shield_model", BubbleShieldModel.CODEC, model -> {
            shieldRenderer.setModel(model);
            LOGGER.info("Loaded bubble shield model.");
        });
    }

    private <T> void readWithCodec(ResourceManager manager, String path, Codec<T> codec, Consumer<T> consumer)
    {
        Identifier resourcePath = LTXIndustries.RESOURCES.id(path + ".json");

        try (BufferedReader reader = manager.openAsReader(resourcePath))
        {
            JsonObject root = GsonHelper.parse(reader);
            T data = LimaCoreCodecs.tryDecode(codec, JsonOps.INSTANCE, root);
            if (data != null) consumer.accept(data);
        }
        catch (JsonParseException | IOException e)
        {
            LOGGER.error("LTXI client resource {} didn't decode correctly. It might be missing or corrupted.", resourcePath);
        }
    }
}