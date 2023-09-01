package com.fusionflux.portalcubed.commands;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.fog.FogPersistentState;
import com.fusionflux.portalcubed.fog.FogPreset;
import com.fusionflux.portalcubed.fog.FogSettings;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.quiltmc.qsl.command.api.EnumArgumentType;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FogCommand {
	@FunctionalInterface
	private interface DefaultDimensionAction {
		int run(CommandContext<CommandSourceStack> ctx, boolean defaultDimension) throws CommandSyntaxException;
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(buildDefaultDimension(literal("fog"), FogCommand::getFog)
			.requires(s -> s.hasPermission(2))
			.then(buildDefaultDimension(literal("reset"), FogCommand::resetFog))
			.then(buildSet(literal("set"), true)
				.then(buildSet(argument("dimension", DimensionArgument.dimension()), false))
			)
			.then(literal("preset")
				.then(buildDefaultDimension(argument("preset", EnumArgumentType.enumConstant(FogPreset.class)), FogCommand::presetFog))
			)
		);
	}

	private static <T extends ArgumentBuilder<CommandSourceStack, T>> ArgumentBuilder<CommandSourceStack, T> buildDefaultDimension(
		ArgumentBuilder<CommandSourceStack, T> builder,
		DefaultDimensionAction action
	) {
		return builder.executes(ctx -> action.run(ctx, true))
			.then(argument("dimension", DimensionArgument.dimension())
				.executes(ctx -> action.run(ctx, false))
			);
	}

	private static ArgumentBuilder<CommandSourceStack, ?> buildSet(ArgumentBuilder<CommandSourceStack, ?> builder, boolean defaultDimension) {
		return builder.then(argument("near", FloatArgumentType.floatArg())
			.then(argument("far", FloatArgumentType.floatArg(0))
				.then(argument("red", IntegerArgumentType.integer(0, 255))
					.then(argument("green", IntegerArgumentType.integer(0, 255))
						.then(argument("blue", IntegerArgumentType.integer(0, 255))
							.executes(ctx -> setFog(ctx, defaultDimension, true))
							.then(argument("shape", EnumArgumentType.enumConstant(FogSettings.Shape.class))
								.executes(ctx -> setFog(ctx, defaultDimension, false))
							)
						)
					)
				)
			)
		);
	}

	public static int getFog(CommandContext<CommandSourceStack> ctx, boolean defaultDimension) throws CommandSyntaxException {
		final ServerLevel dimension = getDimension(ctx, defaultDimension);
		ctx.getSource().sendSuccess(() -> Component.translatable(
			"portalcubed.command.fog.success",
			dimension.dimension().location(),
			FogPersistentState.getOrCreate(dimension).getSettings()
		), false);
		return Command.SINGLE_SUCCESS;
	}

	public static int resetFog(CommandContext<CommandSourceStack> ctx, boolean defaultDimension) throws CommandSyntaxException {
		final ServerLevel dimension = getDimension(ctx, defaultDimension);
		FogPersistentState.getOrCreate(dimension).setSettings(null);
		PortalCubed.syncFog(dimension);
		ctx.getSource().sendSuccess(() -> Component.translatable(
			"portalcubed.command.fog.reset.success",
			dimension.dimension().location()
		), true);
		return Command.SINGLE_SUCCESS;
	}

	public static int setFog(CommandContext<CommandSourceStack> ctx, boolean defaultDimension, boolean defaultShape) throws CommandSyntaxException {
		final ServerLevel dimension = getDimension(ctx, defaultDimension);
		final FogSettings settings = new FogSettings(
			FloatArgumentType.getFloat(ctx, "near"),
			FloatArgumentType.getFloat(ctx, "far"),
			new FogSettings.Color(
				IntegerArgumentType.getInteger(ctx, "red"),
				IntegerArgumentType.getInteger(ctx, "green"),
				IntegerArgumentType.getInteger(ctx, "blue")
			),
			defaultShape
				? FogSettings.Shape.SPHERE
				: EnumArgumentType.getEnumConstant(ctx, "shape", FogSettings.Shape.class)
		);
		FogPersistentState.getOrCreate(dimension).setSettings(settings);
		PortalCubed.syncFog(dimension);
		ctx.getSource().sendSuccess(() -> Component.translatable(
			"portalcubed.command.fog.set.success",
			dimension.dimension().location(),
			settings
		), true);
		return Command.SINGLE_SUCCESS;
	}

	public static int presetFog(CommandContext<CommandSourceStack> ctx, boolean defaultDimension) throws CommandSyntaxException {
		final ServerLevel dimension = getDimension(ctx, defaultDimension);
		final FogPreset preset = getEnumConstant(ctx, "preset", FogPreset.class);
		FogPersistentState.getOrCreate(dimension).setSettings(preset.getSettings());
		PortalCubed.syncFog(dimension);
		ctx.getSource().sendSuccess(() -> Component.translatable(
			"portalcubed.command.fog.preset.success",
			dimension.dimension().location(),
			preset
		), true);
		return Command.SINGLE_SUCCESS;
	}

	private static ServerLevel getDimension(CommandContext<CommandSourceStack> ctx, boolean defaultDimension) throws CommandSyntaxException {
		return defaultDimension
			? ctx.getSource().getLevel()
			: DimensionArgument.getDimension(ctx, "dimension");
	}

	// Reimplemented here because QSL implements a validation that's also bugged and throws exceptions when it shouldn't
	private static <E extends Enum<E>> E getEnumConstant(CommandContext<?> context, String argumentName, Class<E> enumClass) throws CommandSyntaxException {
		String value = context.getArgument(argumentName, String.class);
		E[] constants = enumClass.getEnumConstants();

		for (var constant : constants) {
			if (constant.name().equalsIgnoreCase(value)) {
				return constant;
			}
		}

		throw EnumArgumentType.UNKNOWN_VALUE_EXCEPTION.create(argumentName);
	}
}
