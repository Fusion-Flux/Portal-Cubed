package com.fusionflux.portalcubed.commands;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.blockentities.LaserNodeBlockEntity;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class LaserSongCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(literal("lasersong")
			.requires(s -> s.hasPermission(2))
			.then(argument("node", BlockPosArgument.blockPos())
				.then(argument("song", ResourceLocationArgument.id())
					.suggests(SuggestionProviders.AVAILABLE_SOUNDS)
					.executes(ctx -> {
						final BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "node");
						final ResourceLocation song = ResourceLocationArgument.getId(ctx, "song");
						final LaserNodeBlockEntity entity = ctx.getSource()
							.getLevel()
							.getBlockEntity(pos, PortalCubedBlocks.LASER_NODE_BLOCK_ENTITY)
							.orElse(null);
						if (entity == null) {
							ctx.getSource().sendFailure(
								Component.translatable("portalcubed.command.lasersong.failed", pos)
									.withStyle(ChatFormatting.RED)
							);
							return 0;
						}
						entity.setSound(song);
						ctx.getSource().sendSuccess(
							() -> Component.translatable("portalcubed.command.lasersong.success", pos, song),
							true
						);
						return 1;
					})
				)
			)
		);
	}
}
