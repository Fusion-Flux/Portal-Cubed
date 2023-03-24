package com.fusionflux.portalcubed.commands;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.LaserNodeBlockEntity;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class LaserSongCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("lasersong")
            .requires(s -> s.hasPermissionLevel(2))
            .then(RequiredArgumentBuilder.<ServerCommandSource, PosArgument>argument("node", BlockPosArgumentType.blockPos())
                .then(RequiredArgumentBuilder.<ServerCommandSource, Identifier>argument("song", IdentifierArgumentType.identifier())
                    .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                    .executes(ctx -> {
                        final BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "node");
                        final Identifier song = IdentifierArgumentType.getIdentifier(ctx, "song");
                        final LaserNodeBlockEntity entity = ctx.getSource()
                            .getWorld()
                            .getBlockEntity(pos, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY)
                            .orElse(null);
                        if (entity == null) {
                            ctx.getSource().sendError(
                                Text.translatable("portalcubed.command.lasersong.failed", pos)
                                    .formatted(Formatting.RED)
                            );
                            return 0;
                        }
                        entity.setSound(song);
                        ctx.getSource().sendFeedback(
                            Text.translatable("portalcubed.command.lasersong.success", pos, song),
                            true
                        );
                        return 1;
                    })
                )
            )
        );
    }
}
