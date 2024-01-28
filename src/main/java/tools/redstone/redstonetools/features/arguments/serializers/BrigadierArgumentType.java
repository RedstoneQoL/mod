package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public abstract class BrigadierArgumentType<T, S> extends GenericArgumentType<T, S> {

    // the wrapped brigadier argument type
    private final ArgumentType<T> argumentType;

    public BrigadierArgumentType(Class<T> clazz, ArgumentType<T> argumentType) {
        super(clazz);
        this.argumentType = argumentType;
    }

    @Override
    public T deserialize(StringReader reader) throws CommandSyntaxException {
        return argumentType.parse(reader);
    }

    @Override
    public <R> CompletableFuture<Suggestions> listSuggestions(CommandContext<R> context, SuggestionsBuilder builder) {
        return argumentType.listSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return argumentType.getExamples();
    }

    public abstract class BrigadierSerializer extends Serializer<BrigadierArgumentType<T,S>,ArgumentSerializer.ArgumentTypeProperties<BrigadierArgumentType<T,S>>>{

        public abstract ArgumentTypeProperties<BrigadierArgumentType<T,S>> getArgumentTypeProperties(BrigadierArgumentType<T,S> argumentType);

    }
}
