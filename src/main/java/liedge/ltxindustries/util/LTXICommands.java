package liedge.ltxindustries.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.ltxindustries.LTXICapabilities;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.entity.BubbleShieldUser;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

@EventBusSubscriber(modid = LTXIndustries.MODID)
public final class LTXICommands
{
    private LTXICommands() {}

    @SubscribeEvent
    public static void register(final RegisterCommandsEvent event)
    {
        List<LiteralArgumentBuilder<CommandSourceStack>> subCommands = new ObjectArrayList<>();

        subCommands.add(Commands.literal("shield").requires(ctx -> ctx.hasPermission(2))
                .then(shieldCmd("set"))
                .then(shieldCmd("give"))
                .then(shieldCmd("remove")));

        LiteralArgumentBuilder<CommandSourceStack> masterCommand = subCommands.stream().reduce(Commands.literal(LTXIndustries.MODID), LiteralArgumentBuilder::then);
        event.getDispatcher().register(masterCommand);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> shieldCmd(String action)
    {
        return Commands.literal(action)
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", FloatArgumentType.floatArg())
                                .executes(ctx -> {
                                    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                    float amt = FloatArgumentType.getFloat(ctx, "amount");

                                    BubbleShieldUser user = player.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
                                    if (user != null)
                                    {
                                        switch (action)
                                        {
                                            case "set" -> user.setShieldHealth(amt);
                                            case "give" -> user.addShieldHealth(amt, BubbleShieldUser.MAX_SHIELD_HEALTH);
                                            case "remove" -> user.removeShieldHealth(amt, 0f);
                                        }
                                    }

                                    return Command.SINGLE_SUCCESS;
                                })));
    }
}