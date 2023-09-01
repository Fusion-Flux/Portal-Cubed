package com.fusionflux.portalcubed.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.core.Direction;

public class DirectionArgumentType extends StringRepresentableArgument<Direction> {
	private DirectionArgumentType() {
		super(Direction.CODEC, Direction::values);
	}

	public static StringRepresentableArgument<Direction> direction() {
		return new DirectionArgumentType();
	}

	public static Direction getDirection(CommandContext<?> context, String name) {
		return context.getArgument(name, Direction.class);
	}
}
