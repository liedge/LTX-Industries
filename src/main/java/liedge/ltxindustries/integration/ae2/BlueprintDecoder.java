package liedge.ltxindustries.integration.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.IPatternDetailsDecoder;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class BlueprintDecoder implements IPatternDetailsDecoder
{
    private static final BlueprintDecoder INSTANCE = new BlueprintDecoder();

    static void registerDecoder()
    {
        PatternDetailsHelper.registerDecoder(INSTANCE);
    }

    private BlueprintDecoder() {}

    @Override
    public boolean isEncodedPattern(ItemStack stack)
    {
        return stack.is(LTXIItems.FABRICATION_BLUEPRINT);
    }

    @Override
    public @Nullable IPatternDetails decodePattern(AEItemKey what, Level level)
    {
        return what != null && level != null ? AEFabricationPattern.tryCreate(what, level) : null;
    }
}