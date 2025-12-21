package liedge.ltxindustries.item;

import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXIItemAbilities
{
    private LTXIItemAbilities() {}

    public static final ItemAbility WRENCH_ROTATE = ItemAbility.get(RESOURCES.modid() + ":wrench_rotate");
    public static final ItemAbility WRENCH_DISMANTLE = ItemAbility.get(RESOURCES.modid() + ":wrench_dismantle");

    public static final Set<ItemAbility> WRENCH_ABILITIES = Set.of(WRENCH_ROTATE, WRENCH_DISMANTLE);
    public static final Set<ItemAbility> SWORD_NO_SWEEP = Set.of(ItemAbilities.SWORD_DIG);
}