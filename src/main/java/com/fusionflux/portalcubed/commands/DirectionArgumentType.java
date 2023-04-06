package com.fusionflux.portalcubed.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Direction;

public class DirectionArgumentType extends EnumArgumentType<Direction> {
    private DirectionArgumentType() {
        super(Direction.CODEC, Direction::values);
    }

    public static EnumArgumentType<Direction> direction() {
        return new DirectionArgumentType();
    }

    public static Direction getDirection(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Direction.class);
    }
}
