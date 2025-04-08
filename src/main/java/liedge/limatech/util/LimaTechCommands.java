package liedge.limatech.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechCapabilities;
import liedge.limatech.entity.BubbleShieldUser;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = LimaTech.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class LimaTechCommands
{
    private LimaTechCommands() {}

    @SubscribeEvent
    public static void register(final RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("limatech")
                .then(Commands.literal("shield").requires(ctx -> ctx.hasPermission(2))
                        .then(shieldCmd("set"))
                        .then(shieldCmd("give"))
                        .then(shieldCmd("remove"))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> shieldCmd(String action)
    {
        return Commands.literal(action)
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", FloatArgumentType.floatArg())
                                .executes(ctx -> {
                                    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                    float amt = FloatArgumentType.getFloat(ctx, "amount");

                                    BubbleShieldUser user = player.getCapability(LimaTechCapabilities.ENTITY_BUBBLE_SHIELD);
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