package liedge.ltxindustries.client.renderer.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public final class LTXIItemRenderers
{
    private LTXIItemRenderers() {}

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

    public static void tickValidRenderers(Player localPlayer)
    {
        GRENADE_LAUNCHER.tickItemRenderer(localPlayer);
    }

    // Weapons
    public static final SMGRenderProperties SUBMACHINE_GUN = register(new SMGRenderProperties());
    public static final ShotgunRenderProperties SHOTGUN = register(new ShotgunRenderProperties());
    public static final GrenadeLauncherRenderProperties GRENADE_LAUNCHER = register(new GrenadeLauncherRenderProperties());
    public static final LinearFusionRenderProperties LINEAR_FUSION_RIFLE = register(new LinearFusionRenderProperties());
    public static final RocketLauncherRenderProperties ROCKET_LAUNCHER = register(new RocketLauncherRenderProperties());
    public static final MagnumRenderProperties MAGNUM = register(new MagnumRenderProperties());
}