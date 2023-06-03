package com.fusionflux.portalcubed.commands;

import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.RocketEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FireRocketCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("firerocket")
            .executes(ctx -> fireRocket(ctx.getSource(), null))
            .then(argument("rotation", RotationArgument.rotation())
                .executes(ctx -> fireRocket(ctx.getSource(), RotationArgument.getRotation(ctx, "rotation")))
            )
        );
    }

    public static int fireRocket(CommandSourceStack source, @Nullable Coordinates rotationArg) {
        final RocketEntity rocket = PortalCubedEntities.ROCKET.create(source.getLevel());
        if (rocket == null) return 0;
        final Vec2 rotation = rotationArg != null
            ? rotationArg.getRotation(source)
            : source.getRotation();
        rocket.setPos(source.getAnchor().apply(source));
        rocket.setYRot(rotation.y);
        rocket.setXRot(rotation.x);
        source.getLevel().addFreshEntity(rocket);
        source.getLevel().playSound(null, rocket, PortalCubedSounds.ROCKET_FIRE_EVENT, SoundSource.HOSTILE, 1, 1);
        return Command.SINGLE_SUCCESS;
    }
}
