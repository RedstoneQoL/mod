package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import tools.redstone.redstonetools.macros.MacroManager;

import java.util.concurrent.CompletableFuture;

import static tools.redstone.redstonetools.RedstoneToolsClient.INJECTOR;

@AutoService(GenericArgumentType.class)
public class MacroNameSerializer extends StringSerializer {
    private static final MacroNameSerializer INSTANCE = new MacroNameSerializer();

    private MacroNameSerializer() {

        super(StringSerializer.greedyString());

    }

    public static MacroNameSerializer macroName() {
        return INSTANCE;
    }

    @Override
    public <R> CompletableFuture<Suggestions> listSuggestions(CommandContext<R> context, SuggestionsBuilder builder) {

        for (var macro : INJECTOR.getInstance(MacroManager.class).getMacros()) {

            builder = builder.suggest(serialize(macro.name));
        }

        return builder.buildFuture();
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<MacroNameSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<MacroNameSerializer> {

            @Override
            public MacroNameSerializer createType(CommandRegistryAccess var1) {
                return macroName();
            }

            @Override
            public ArgumentSerializer<MacroNameSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(MacroNameSerializer serializer) {
            return new Properties();
        }
    }
}
