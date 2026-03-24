package liedge.ltxindustries.lib.upgrades;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.util.context.ContextKey;

public final class UpgradeContextKeys
{
    private UpgradeContextKeys() {}

    public static final ContextKey<Integer> UPGRADE_RANK = LTXIndustries.RESOURCES.contextKey("upgrade_rank"); // Unused for now
    public static final ContextKey<Float> DAMAGE = LTXIndustries.RESOURCES.contextKey("damage");
}