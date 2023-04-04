package com.fusionflux.portalcubed.commands;

import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.entity.RocketEntity;
import com.fusionflux.portalcubed.sound.PortalCubedSounds;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FireRocketCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("firerocket")
            .executes(ctx -> fireRocket(ctx.getSource(), null))
            .then(argument("rotation", RotationArgumentType.rotation())
                .executes(ctx -> fireRocket(ctx.getSource(), RotationArgumentType.getRotation(ctx, "rotation")))
            )
        );
    }

    public static int fireRocket(ServerCommandSource source, @Nullable PosArgument rotationArg) {
        final RocketEntity rocket = PortalCubedEntities.ROCKET.create(source.getWorld());
        if (rocket == null) return 0;
        final Vec2f rotation = rotationArg != null
            ? rotationArg.toAbsoluteRotation(source)
            : source.getRotation();
        rocket.setPosition(source.getEntityAnchor().positionAt(source));
        rocket.setYaw(rotation.y);
        rocket.setPitch(rotation.x);
        source.getWorld().spawnEntity(rocket);
        source.getWorld().playSoundFromEntity(null, rocket, PortalCubedSounds.ROCKET_FIRE_EVENT, SoundCategory.HOSTILE, 1, 1);
        return Command.SINGLE_SUCCESS;
    }
}
