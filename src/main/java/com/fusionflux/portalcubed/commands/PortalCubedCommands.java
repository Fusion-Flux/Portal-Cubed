package com.fusionflux.portalcubed.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

public class PortalCubedCommands implements CommandRegistrationCallback {
    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection environment) {
        FizzleCommand.register(dispatcher);
        FireRocketCommand.register(dispatcher);
        LaserSongCommand.register(dispatcher);
        FogCommand.register(dispatcher);
        RotatePortalCommand.register(dispatcher);
        if (QuiltLoader.isModLoaded("pehkui")) {
            ChellScaleCommand.register(dispatcher);
        }
    }
}
