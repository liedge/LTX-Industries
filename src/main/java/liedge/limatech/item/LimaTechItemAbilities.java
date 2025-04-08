package liedge.limatech.item;

import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechItemAbilities
{
    private LimaTechItemAbilities() {}

    public static final ItemAbility WRENCH_ROTATE = ItemAbility.get(RESOURCES.modid() + ":wrench_rotate");
    public static final ItemAbility WRENCH_DISMANTLE = ItemAbility.get(RESOURCES.modid() + ":wrench_dismantle");

    public static final Set<ItemAbility> WRENCH_ABILITIES = Set.of(WRENCH_ROTATE, WRENCH_DISMANTLE);
}