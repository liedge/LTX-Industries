package liedge.ltxindustries.integration.guideme;

import guideme.compiler.PageCompiler;
import guideme.compiler.tags.BlockTagCompiler;
import guideme.compiler.tags.MdxAttrs;
import guideme.document.block.LytBlockContainer;
import guideme.libs.mdast.mdx.model.MdxJsxElementFields;
import liedge.ltxindustries.lib.upgrades.UpgradeEntry;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Set;

final class UpgradeEntryCompiler extends BlockTagCompiler
{
    @Override
    protected void compile(PageCompiler compiler, LytBlockContainer parent, MdxJsxElementFields el)
    {
        Identifier upgradeId = MdxAttrs.getRequiredId(compiler, parent, el, "id");
        if (upgradeId == null) return;

        int rank = MdxAttrs.getInt(compiler, parent, el, "rank", 1);
        if (rank < 1 || rank > 10)
        {
            parent.appendError(compiler, "Upgrade rank outside valid range 1-10", el);
            return;
        }

        Level level = Minecraft.getInstance().level;
        if (level == null)
        {
            parent.appendError(compiler, "Missing client level for registry access.", el);
            return;
        }

        level.registryAccess().get(ResourceKey.create(LTXIRegistries.Keys.UPGRADES, upgradeId)).ifPresentOrElse(holder ->
        {
            UpgradeEntry entry = new UpgradeEntry(holder, rank);
            LytUpgradeEntry lytBlock = new LytUpgradeEntry(entry);
            parent.append(lytBlock);
        }, () -> parent.appendError(compiler, "No upgrade found for id " + upgradeId, el));
    }

    @Override
    public Set<String> getTagNames()
    {
        return Set.of("UpgradeEntry");
    }
}