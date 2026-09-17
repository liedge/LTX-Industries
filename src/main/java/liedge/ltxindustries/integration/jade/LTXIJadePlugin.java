package liedge.ltxindustries.integration.jade;

import liedge.limacore.block.LimaEntityBlock;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.StateMachineBlock;
import net.minecraft.resources.Identifier;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class LTXIJadePlugin implements IWailaPlugin
{
    public static final Identifier UID_RECIPE_MODE = LTXIndustries.RESOURCES.id("recipe_mode");
    public static final Identifier UID_PG_FUEL = LTXIndustries.RESOURCES.id("pg_fuel");

    public static final Identifier UID_GENERIC_PROGRESS = LTXIndustries.RESOURCES.id("generic_progress");
    public static final Identifier UID_FABRICATOR_PROGRESS = LTXIndustries.RESOURCES.id("fabricator_progress");

    public static String optionLangKey(Identifier uid)
    {
        return String.format("config.jade.plugin_%s.%s", uid.getNamespace(), uid.getPath());
    }

    @Override
    public void register(IWailaCommonRegistration registration)
    {
        registration.registerBlockDataProvider(RecipeModeProvider.INSTANCE, LimaEntityBlock.class);
        registration.registerBlockDataProvider(PGFuelProvider.INSTANCE, StateMachineBlock.class);
        registration.registerProgress(GenericProgressExtension.INSTANCE, LimaEntityBlock.class);
        registration.registerProgress(FabricatorProgressExtension.INSTANCE, LimaEntityBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration)
    {
        registration.registerBlockComponent(RecipeModeProvider.Client.INSTANCE, LimaEntityBlock.class);
        registration.registerBlockComponent(PGFuelProvider.Client.INSTANCE, StateMachineBlock.class);
        registration.registerProgressClient(GenericProgressExtension.INSTANCE);
        registration.registerProgressClient(FabricatorProgressExtension.INSTANCE);
    }
}