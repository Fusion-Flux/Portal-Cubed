package com.fusionflux.portalcubed.commands;

import com.fusionflux.portalcubed.entity.Fizzleable;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FizzleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("fizzle")
            .requires(s -> s.hasPermission(2))
            .then(argument("entity", EntityArgument.entities())
                .executes(ctx -> {
                    final List<Fizzleable> entities = EntityArgument.getOptionalEntities(ctx, "entity")
                        .stream()
                        .filter(e -> e instanceof Fizzleable)
                        .map(e -> (Fizzleable)e)
                        .toList();
                    if (entities.isEmpty()) {
                        throw EntityArgument.NO_ENTITIES_FOUND.create();
                    }
                    fizzle(entities);
                    ctx.getSource().sendSuccess(
                        () -> Component.translatable("portalcubed.command.fizzle.success", entities.size()),
                        true
                    );
                    return entities.size();
                })
            )
        );
    }

    public static void fizzle(Collection<Fizzleable> entities) {
        for (final Fizzleable physics : entities) {
            physics.fizzle();
        }
    }
}
