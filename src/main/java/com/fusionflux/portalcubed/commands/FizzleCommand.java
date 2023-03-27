package com.fusionflux.portalcubed.commands;

import com.fusionflux.portalcubed.entity.Fizzleable;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FizzleCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("fizzle")
            .requires(s -> s.hasPermissionLevel(2))
            .then(argument("entity", EntityArgumentType.entities())
                .executes(ctx -> {
                    final List<Fizzleable> entities = EntityArgumentType.getOptionalEntities(ctx, "entity")
                        .stream()
                        .filter(e -> e instanceof Fizzleable)
                        .map(e -> (Fizzleable)e)
                        .toList();
                    if (entities.isEmpty()) {
                        throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
                    }
                    fizzle(entities);
                    ctx.getSource().sendFeedback(
                        Text.translatable("portalcubed.command.fizzle.success", entities.size()),
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
