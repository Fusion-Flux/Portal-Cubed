package com.fusionflux.portalcubed.commands;

import com.fusionflux.portalcubed.entity.Portal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RotatePortalCommand {
    private static final SimpleCommandExceptionType NOT_PORTAL = new SimpleCommandExceptionType(Component.literal("Entity is not a Portal"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("rotateportal")
                .requires(source -> source.hasPermission(2))
                .then(argument("portal", EntityArgument.entity())
                        .then(argument("x", FloatArgumentType.floatArg())
                                .then(argument("y", FloatArgumentType.floatArg())
                                        .then(argument("z", FloatArgumentType.floatArg())
                                                .then(argument("w", FloatArgumentType.floatArg())
                                                        .executes(RotatePortalCommand::rotatePortal)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int rotatePortal(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(ctx, "portal");
        if (!(entity instanceof Portal portal))
            throw NOT_PORTAL.create();
        float x = FloatArgumentType.getFloat(ctx, "x");
        float y = FloatArgumentType.getFloat(ctx, "y");
        float z = FloatArgumentType.getFloat(ctx, "z");
        float w = FloatArgumentType.getFloat(ctx, "w");
        portal.setDisableValidation(true);
        portal.getRotation().lerpTo(new Quaternionf(x, y, z, w).normalize());
        return 1;
    }
}
