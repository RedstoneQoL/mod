package tools.redstone.redstonetools.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.ServerCommandSource;

public class CommandSourceUtils {
    private CommandSourceUtils() {
    }

    public static void executeCommand(ServerCommandSource source, String command) throws CommandSyntaxException {
        source.getServer().getCommandManager().getDispatcher().execute(command, source);
    }
}
