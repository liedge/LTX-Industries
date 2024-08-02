package liedge.limatech.client.renderer.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.List;

public final class LimaTechItemRenderers
{
    private LimaTechItemRenderers() {}

    private static final List<ResourceManagerReloadListener> RELOAD_LISTENERS = new ObjectArrayList<>();

    private static <T extends ResourceManagerReloadListener> T register(T object)
    {
        RELOAD_LISTENERS.add(object);
        return object;
    }

    public static void reloadAll(ResourceManager manager)
    {
        RELOAD_LISTENERS.forEach(o -> o.onResourceManagerReload(manager));
    }

    public static void tickValidRenderers()
    {
        GRENADE_LAUNCHER.tickItemRenderer();
    }

    public static final SMGRenderProperties SUBMACHINE_GUN = register(new SMGRenderProperties());
    public static final ShotgunRenderProperties SHOTGUN = register(new ShotgunRenderProperties());
    public static final GrenadeLauncherRenderProperties GRENADE_LAUNCHER = register(new GrenadeLauncherRenderProperties());
    public static final RocketLauncherRenderProperties ROCKET_LAUNCHER = register(new RocketLauncherRenderProperties());
    public static final MagnumRenderProperties MAGNUM = register(new MagnumRenderProperties());
}