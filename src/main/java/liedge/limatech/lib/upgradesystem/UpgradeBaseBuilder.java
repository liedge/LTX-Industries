package liedge.limatech.lib.upgradesystem;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limatech.client.UpgradeIcon;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;

import java.util.List;

public abstract class UpgradeBaseBuilder<CTX, E extends UpgradeEffectBase, U extends UpgradeBase<CTX, E, U>, B extends UpgradeBaseBuilder<CTX, E, U, B>>
{
    private final ResourceKey<U> key;
    private final UpgradeBase.UpgradeFactory<CTX, E, U> factory;

    private final List<E> effects = new ObjectArrayList<>();
    private Component title;
    private Component description;
    private int maxRank = 1;
    private HolderSet<CTX> supportedSet;
    private HolderSet<U> exclusiveSet = HolderSet.empty();
    private UpgradeIcon icon = UpgradeIcon.DEFAULT_ICON;

    protected UpgradeBaseBuilder(ResourceKey<U> key, UpgradeBase.UpgradeFactory<CTX, E, U> factory)
    {
        this.key = key;
        this.factory = factory;
        this.title = Component.translatable(defaultTitleKey(key));
        this.description = Component.translatable(defaultDescriptionKey(key));
    }

    @SuppressWarnings("unchecked")
    private B selfUnchecked()
    {
        return (B) this;
    }

    public B setTitle(Component title)
    {
        this.title = title;
        return selfUnchecked();
    }

    public B setDescription(Component description)
    {
        this.description = description;
        return selfUnchecked();
    }

    public B setMaxRank(int maxRank)
    {
        this.maxRank = Mth.clamp(maxRank, 1, UpgradeBase.MAX_UPGRADE_RANK);
        return selfUnchecked();
    }

    public B supports(HolderSet<CTX> supportedSet)
    {
        this.supportedSet = supportedSet;
        return selfUnchecked();
    }

    public B supports(HolderGetter<CTX> holders, TagKey<CTX> tagKey)
    {
        return supports(holders.getOrThrow(tagKey));
    }

    public B supports(Holder<CTX> ctxObject)
    {
        return supports(HolderSet.direct(ctxObject));
    }

    @SafeVarargs
    public final B supports(Holder<CTX>... ctxObjects)
    {
        return supports(HolderSet.direct(ctxObjects));
    }

    public B exclusiveWith(HolderSet<U> exclusiveSet)
    {
        this.exclusiveSet = exclusiveSet;
        return selfUnchecked();
    }

    public B exclusiveWith(HolderGetter<U> holders, TagKey<U> tagKey)
    {
        return exclusiveWith(holders.getOrThrow(tagKey));
    }

    public B withEffect(E effect)
    {
        effects.add(effect);
        return selfUnchecked();
    }

    public B effectIcon(UpgradeIcon icon)
    {
        this.icon = icon;
        return selfUnchecked();
    }

    public U build()
    {
        return factory.apply(title, description, maxRank, supportedSet, exclusiveSet, effects, icon);
    }

    public void buildAndRegister(BootstrapContext<U> ctx)
    {
        ctx.register(key, build());
    }

    protected abstract String defaultTitleKey(ResourceKey<U> key);

    protected abstract String defaultDescriptionKey(ResourceKey<U> key);
}