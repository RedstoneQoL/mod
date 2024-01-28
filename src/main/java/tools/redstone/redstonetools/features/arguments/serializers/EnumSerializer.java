package tools.redstone.redstonetools.features.arguments.serializers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

public abstract class EnumSerializer<T extends Enum<T>>
        extends GenericArgumentType<T, String> {

    protected EnumSerializer(Class<T> clazz) {
        super(clazz);
    }

    // common method for stringification
    // of the enum constants, can be overridden
    @Override
    public String serialize(T value) {
        return value.toString();
    }

    @Override
    public T deserialize(StringReader reader) throws CommandSyntaxException {
        var input = reader.readUnquotedString();

        try {
            return deserialize(input);
        } catch (IllegalArgumentException e) {
            throw new CommandSyntaxException(null, Text.of(e.getMessage()));
        }
    }

    @Override
    public T deserialize(String input) {
        String inputLowerCase = input.toLowerCase();

        var matches = EnumSet.allOf(clazz)
                .stream()
                .filter(elem -> serialize(elem).toLowerCase().startsWith(inputLowerCase))
                .toList();

        var exactMatch = matches.stream().filter(elem -> serialize(elem).toLowerCase().equals(input)).findFirst();
        if (exactMatch.isPresent()) {
            return exactMatch.get();
        }

        if (matches.isEmpty()) {
            throw new IllegalArgumentException("No such option '" + input + "'");
        }

        if (matches.size() > 1) {
            throw new IllegalArgumentException("Ambiguous option '" + input + "'");
        }

        return matches.get(0);
    }

    @Override
    public Collection<String> getExamples() {
        return EnumSet.allOf(clazz).stream()
                .map(this::serialize)
                .toList();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (var value : EnumSet.allOf(clazz)) {
            builder = builder.suggest(serialize(value));
        }

        return builder.buildFuture();
    }

}
