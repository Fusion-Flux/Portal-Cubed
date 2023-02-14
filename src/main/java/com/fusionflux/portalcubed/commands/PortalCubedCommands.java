package com.fusionflux.portalcubed.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandBuildContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.quiltmc.qsl.command.api.CommandRegistrationCallback;

public class PortalCubedCommands implements CommandRegistrationCallback {
    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandBuildContext buildContext, CommandManager.RegistrationEnvironment environment) {
        FizzleCommand.register(dispatcher);
        FireRocketCommand.register(dispatcher);
    }
}
