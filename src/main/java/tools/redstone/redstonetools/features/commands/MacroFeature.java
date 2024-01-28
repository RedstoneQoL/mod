package tools.redstone.redstonetools.features.commands;

import com.google.auto.service.AutoService;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.arguments.Argument;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.macros.Macro;
import tools.redstone.redstonetools.macros.MacroManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import static tools.redstone.redstonetools.RedstoneToolsClient.INJECTOR;
import static tools.redstone.redstonetools.features.arguments.serializers.MacroNameSerializer.macroName;

@AutoService(AbstractFeature.class)
@Feature(command = "macro", description = "Allows you to execute a macro", name = "Macro")
public class MacroFeature extends CommandFeature {
    public static final Argument<String> macro = Argument.ofType(macroName());

    @Override
    protected Feedback execute(ServerCommandSource source) throws CommandSyntaxException {
        var macroObj = INJECTOR.getInstance(MacroManager.class).getMacro(macro.getValue());


        if (macroObj == null) {
            return Feedback.invalidUsage("Macro \"{}\" does not exist.", macro.getValue());
        }

        macroObj.run();
        return Feedback.none();
    }
}
