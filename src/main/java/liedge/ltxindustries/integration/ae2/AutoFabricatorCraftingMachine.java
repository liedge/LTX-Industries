package liedge.ltxindustries.integration.ae2;

import appeng.api.AECapabilities;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.ltxindustries.blockentity.AutoFabricatorBlockEntity;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

public final class AutoFabricatorCraftingMachine implements ICraftingMachine
{
    static void registerCapability(final RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(AECapabilities.CRAFTING_MACHINE, LTXIBlockEntities.AUTO_FABRICATOR.get(), (be, ignored) -> new AutoFabricatorCraftingMachine(be));
    }

    private final PatternContainerGroup display;
    private final AutoFabricatorBlockEntity blockEntity;

    public AutoFabricatorCraftingMachine(AutoFabricatorBlockEntity blockEntity)
    {
        this.display = new PatternContainerGroup(AEItemKey.of(LTXIBlocks.AUTO_FABRICATOR), LTXIBlocks.AUTO_FABRICATOR.get().getName(), List.of());
        this.blockEntity = blockEntity;
    }

    @Override
    public PatternContainerGroup getCraftingMachineInfo()
    {
        return display;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputs, Direction ejectionDirection)
    {
        if (patternDetails instanceof AEFabricationPattern fabricationPattern)
        {
            Level level = blockEntity.getLevel();
            if (level != null && blockEntity.canInsertRecipeResults(level, fabricationPattern.recipe()))
            {
                // If enabled, auto-reconfigure the item IO config
                if (LTXIMachinesConfig.FABRICATOR_AE2_AUTO_RECONFIGURE_IO.getAsBoolean())
                {
                    RelativeHorizontalSide beSide = RelativeHorizontalSide.of(blockEntity.getFacing(), ejectionDirection);
                    BlockIOConfiguration configuration = BlockIOConfiguration.create(blockEntity.getIOConfigRules(BlockEntityInputType.ITEMS),
                            side -> side == beSide ? IOAccess.OUTPUT_ONLY : IOAccess.DISABLED)
                            .setAutoOutput(true);
                    blockEntity.setIOConfiguration(BlockEntityInputType.ITEMS, configuration);
                }

                // Set crafting state
                blockEntity.getRecipeCheck().setLastUsedRecipe(fabricationPattern.recipe());
                blockEntity.setCrafting(true);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean acceptsPlans()
    {
        return !blockEntity.isCrafting() && blockEntity.getAuxInventory().getStackInSlot(BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT).isEmpty();
    }
}