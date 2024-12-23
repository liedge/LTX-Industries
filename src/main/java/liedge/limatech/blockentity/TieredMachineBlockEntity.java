package liedge.limatech.blockentity;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limatech.lib.machinetiers.MachineTier;
import liedge.limatech.registry.LimaTechDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TieredMachineBlockEntity extends MachineBlockEntity
{
    private MachineTier tier = MachineTier.TIER_1;

    protected TieredMachineBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public MachineTier getTier()
    {
        return tier;
    }

    public final void setTier(MachineTier tier)
    {
        this.tier = tier;
        applyMachineTier(tier);
    }

    protected abstract void applyMachineTier(MachineTier tier);

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries)
    {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tier", tier.getTierLevel());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider)
    {
        setTier(MachineTier.getByTierLevel(tag.getInt("tier")));
    }

    @Override
    protected void onLoadServer(Level level)
    {
        applyMachineTier(tier); // Apply tier effects only on server, will be applied on client by update packet
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        super.applyImplicitComponents(componentInput);
        MachineTier tier = componentInput.get(LimaTechDataComponents.MACHINE_TIER);
        if (tier != null) this.tier = tier;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);
        components.set(LimaTechDataComponents.MACHINE_TIER, tier);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        super.removeComponentsFromTag(tag);
        tag.remove("tier");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        tier = MachineTier.getByTierLevel(tag.getInt("tier"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putInt("tier", tier.getTierLevel());
    }
}