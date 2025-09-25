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
    public static final SMGRenderer SUBMACHINE_GUN = register(new SMGRenderer());
    public static final ShotgunRenderer SHOTGUN = register(new ShotgunRenderer());
    public static final GrenadeLauncherRenderer GRENADE_LAUNCHER = register(new GrenadeLauncherRenderer());
    public static final LinearFusionRenderer LINEAR_FUSION_RIFLE = register(new LinearFusionRenderer());
    public static final RocketLauncherRenderer ROCKET_LAUNCHER = register(new RocketLauncherRenderer());
    public static final HeavyPistolRenderer HEAVY_PISTOL = register(new HeavyPistolRenderer());
}