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
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.quiltmc.qsl.command.api.EnumArgumentType;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FogCommand {
    @FunctionalInterface
    private interface DefaultDimensionAction {
        int run(CommandContext<ServerCommandSource> ctx, boolean defaultDimension) throws CommandSyntaxException;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(buildDefaultDimension(literal("fog"), FogCommand::getFog)
            .then(buildDefaultDimension(literal("reset"), FogCommand::resetFog))
            .then(buildSet(literal("set"), true)
                .then(buildSet(argument("dimension", DimensionArgumentType.dimension()), false))
            )
            .then(literal("preset")
                .then(buildDefaultDimension(argument("preset", EnumArgumentType.enumConstant(FogPreset.class)), FogCommand::presetFog))
            )
        );
    }

    private static <T extends ArgumentBuilder<ServerCommandSource, T>> ArgumentBuilder<ServerCommandSource, T> buildDefaultDimension(
        ArgumentBuilder<ServerCommandSource, T> builder,
        DefaultDimensionAction action
    ) {
        return builder.executes(ctx -> action.run(ctx, true))
            .then(argument("dimension", DimensionArgumentType.dimension())
                .executes(ctx -> action.run(ctx, false))
            );
    }

    private static ArgumentBuilder<ServerCommandSource, ?> buildSet(ArgumentBuilder<ServerCommandSource, ?> builder, boolean defaultDimension) {
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

    public static int getFog(CommandContext<ServerCommandSource> ctx, boolean defaultDimension) throws CommandSyntaxException {
        final ServerWorld dimension = getDimension(ctx, defaultDimension);
        ctx.getSource().sendFeedback(Text.translatable(
            "portalcubed.command.fog.success",
            dimension.getRegistryKey().getValue(),
            FogPersistentState.getOrCreate(dimension).getSettings()
        ), false);
        return Command.SINGLE_SUCCESS;
    }

    public static int resetFog(CommandContext<ServerCommandSource> ctx, boolean defaultDimension) throws CommandSyntaxException {
        final ServerWorld dimension = getDimension(ctx, defaultDimension);
        FogPersistentState.getOrCreate(dimension).setSettings(null);
        PortalCubed.syncFog(dimension);
        ctx.getSource().sendFeedback(Text.translatable(
            "portalcubed.command.fog.reset.success",
            dimension.getRegistryKey().getValue()
        ), true);
        return Command.SINGLE_SUCCESS;
    }

    public static int setFog(CommandContext<ServerCommandSource> ctx, boolean defaultDimension, boolean defaultShape) throws CommandSyntaxException {
        final ServerWorld dimension = getDimension(ctx, defaultDimension);
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
        ctx.getSource().sendFeedback(Text.translatable(
            "portalcubed.command.fog.set.success",
            dimension.getRegistryKey().getValue(),
            settings
        ), true);
        return Command.SINGLE_SUCCESS;
    }

    public static int presetFog(CommandContext<ServerCommandSource> ctx, boolean defaultDimension) throws CommandSyntaxException {
        final ServerWorld dimension = getDimension(ctx, defaultDimension);
        final FogPreset preset = getEnumConstant(ctx, "preset", FogPreset.class);
        FogPersistentState.getOrCreate(dimension).setSettings(preset.getSettings());
        PortalCubed.syncFog(dimension);
        ctx.getSource().sendFeedback(Text.translatable(
            "portalcubed.command.fog.preset.success",
            dimension.getRegistryKey().getValue(),
            preset
        ), true);
        return Command.SINGLE_SUCCESS;
    }

    private static ServerWorld getDimension(CommandContext<ServerCommandSource> ctx, boolean defaultDimension) throws CommandSyntaxException {
        return defaultDimension
            ? ctx.getSource().getWorld()
            : DimensionArgumentType.getDimensionArgument(ctx, "dimension");
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
