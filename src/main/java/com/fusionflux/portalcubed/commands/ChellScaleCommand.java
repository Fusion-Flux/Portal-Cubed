package com.fusionflux.portalcubed.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Collection;
import java.util.Collections;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ChellScaleCommand {
    public static final float CHELL_SCALE = 1f / 1.62f;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("chellscale")
            .executes(ctx -> scale(Collections.singleton(ctx.getSource().getEntity())))
            .then(argument("targets", EntityArgumentType.entities())
                .executes(ctx -> scale(EntityArgumentType.getEntities(ctx, "targets")))
            )
        );
    }

    public static int scale(Collection<? extends Entity> entities) {
        for (final Entity entity : entities) {
            ScaleTypes.BASE.getScaleData(entity).setTargetScale(CHELL_SCALE);
        }
        return entities.size();
    }
}
