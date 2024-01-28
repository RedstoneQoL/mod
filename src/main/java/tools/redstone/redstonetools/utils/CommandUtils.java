package tools.redstone.redstonetools.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import tools.redstone.redstonetools.features.arguments.Argument;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandUtils {
    private CommandUtils() { }

    public static LiteralArgumentBuilder<ServerCommandSource> build(String name, List<Argument<?>> arguments, Command<ServerCommandSource> executor) {
        var base = CommandManager.literal(name);

        if (arguments.stream().allMatch(Argument::isOptional)) {
            base.executes(executor);
        }

        if (!arguments.isEmpty()) {
            base.then(createArgumentChain(arguments.stream()
                    .sorted((a, b) -> Boolean.compare(a.isOptional(), b.isOptional()))
                    .toList(), executor));
        }

        return base;
    }

    public static void register(String name, List<Argument<?>> arguments, Command<ServerCommandSource> executor, CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        var base = build(name, arguments, executor);
        dispatcher.register(base);
    }

    private static ArgumentBuilder<ServerCommandSource, ?> createArgumentChain(List<Argument<?>> arguments, Command<ServerCommandSource> executor) {
        var reversedArguments = new ArrayList<>(arguments);
        Collections.reverse(reversedArguments);

        ArgumentBuilder<ServerCommandSource, ?> argument = null;
        for (var arg : reversedArguments) {
            if (argument == null) {
                argument = CommandManager.argument(arg.getName(), arg.getType()).executes(executor);
            } else {
                argument = CommandManager.argument(arg.getName(), arg.getType()).then(argument);

                // If the argument is optional or if this is the last required argument it should run the executor
                if (arg.isOptional() || reversedArguments.get(reversedArguments.indexOf(arg) - 1).isOptional()) {
                    argument.executes(executor);
                }
            }
        }

        return argument;
    }
}
