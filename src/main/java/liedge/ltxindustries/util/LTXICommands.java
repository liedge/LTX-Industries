package liedge.ltxindustries.util;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.ltxindustries.LTXICapabilities;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.shield.EntityBubbleShield;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
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

        subCommands.add(Commands.literal("shield")
                .requires(ctx -> ctx.hasPermission(2))
                .then(shieldCmd("set"))
                .then(shieldCmd("give"))
                .then(shieldCmd("remove")));

        LiteralArgumentBuilder<CommandSourceStack> masterCommand = subCommands.stream().reduce(Commands.literal(LTXIndustries.MODID), LiteralArgumentBuilder::then);
        event.getDispatcher().register(masterCommand);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> shieldCmd(String action)
    {
        return Commands.literal(action)
                .then(Commands.argument("amount", FloatArgumentType.floatArg())
                        .requires(ctx -> ctx.getEntity() instanceof ServerPlayer)
                        .executes(ctx -> modifyShields(ctx, action, List.of(ctx.getSource().getPlayerOrException()))))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", FloatArgumentType.floatArg())
                                .executes(ctx -> modifyShields(ctx, action, getLivingEntities(ctx, "targets")))));
    }

    private static int modifyShields(CommandContext<CommandSourceStack> context, String action, List<LivingEntity> targets)
    {
        float amount = FloatArgumentType.getFloat(context, "amount");
        int result;
        if (targets.size() == 1)
            result = modifyShield(targets.getFirst(), action, amount);
        else
            result = targets.stream().mapToInt(e -> modifyShield(e, action, amount)).sum();

        context.getSource().sendSuccess(() -> LTXILangKeys.SHIELD_COMMAND_MSG.translateArgs(result), true);
        return result;
    }

    private static int modifyShield(LivingEntity entity, String action, float amount)
    {
        EntityBubbleShield shield = entity.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
        if (shield != null)
        {
            switch (action)
            {
                case "set" -> shield.setShieldHealth(entity, amount);
                case "give" -> shield.addShieldHealth(entity, amount, EntityBubbleShield.GLOBAL_MAX_SHIELD);
                case "remove" -> shield.reduceShieldHealth(entity, amount);
            }

            return 1;
        }

        return 0;
    }

    private static List<LivingEntity> getLivingEntities(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException
    {
        List<LivingEntity> entities = EntityArgument.getOptionalEntities(context, name)
                .stream()
                .filter(e -> e instanceof LivingEntity)
                .map(e -> (LivingEntity) e)
                .toList();

        if (entities.isEmpty())
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        else
            return entities;
    }
}