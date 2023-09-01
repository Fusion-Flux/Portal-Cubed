package com.fusionflux.portalcubed.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.Collection;
import java.util.Collections;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ChellScaleCommand {
	public static final float CHELL_SCALE = 1f / 1.62f;

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(literal("chellscale")
			.requires(s -> s.hasPermission(2))
			.executes(ctx -> scale(Collections.singleton(ctx.getSource().getEntity())))
			.then(argument("targets", EntityArgument.entities())
				.executes(ctx -> scale(EntityArgument.getEntities(ctx, "targets")))
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
