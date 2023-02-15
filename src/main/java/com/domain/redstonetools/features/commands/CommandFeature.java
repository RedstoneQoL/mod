package com.domain.redstonetools.features.commands;

import com.domain.redstonetools.features.AbstractFeature;
import com.domain.redstonetools.features.options.Argument;
import com.domain.redstonetools.features.options.Options;
import com.domain.redstonetools.utils.CommandUtils;
import com.domain.redstonetools.utils.ReflectionUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

public abstract class CommandFeature<O extends Options> extends AbstractFeature<O> {
    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        var info = ReflectionUtils.getFeatureInfo(this);

        CommandUtils.register(
                info.name(),
                getArguments(),
                context -> {
                    var argumentObj = ReflectionUtils.getArgumentInstance(this);

                    try {
                        for (var argument : ReflectionUtils.getArguments(argumentObj)) {
                            argument.updateValue(context);
                        }
                    } catch (IllegalArgumentException e) {
                        // This should be unreachable, if it isn't, there is something wrong with
                        // registering commands
                        throw new RuntimeException(e);
                    }

                    return execute(context.getSource(), argumentObj);
                },
                dispatcher,
                dedicated);
    }

    private List<Argument<?>> getArguments() {
        return List.of(ReflectionUtils.getArguments(ReflectionUtils.getArgumentInstance(this)));
    }

    protected abstract int execute(ServerCommandSource source, O options) throws CommandSyntaxException;
}
