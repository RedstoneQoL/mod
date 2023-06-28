package tools.redstone.redstonetools.features.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
@AutoService(AbstractFeature.class)
@Feature(name = "Silent", description = "allows you to use commands without output", command = "silent")
public class SilentCommandFeature extends AbstractFeature{

    @Override
    protected void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        var commandManager = CommandManager.literal("silent").redirect(dispatcher.getRoot()).executes( context -> {
            return 1;
        });
        dispatcher.register(commandManager);
    }
}
