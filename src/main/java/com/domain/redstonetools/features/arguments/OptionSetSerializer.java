package com.domain.redstonetools.features.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class OptionSetSerializer<T> extends TypeSerializer<T> {

    /**
     * Get the set of options.
     *
     * @return The set.
     */
    protected abstract Set<T> getSet();

    /**
     * Get if this serializer should only
     * match exact options.
     *
     * @return True/false.
     */
    protected abstract boolean onlyMatchExact();

    protected OptionSetSerializer(Class<T> tClass) {
        super(tClass, null);
    }

    public T find(String input) throws CommandSyntaxException {
        var matches = getSet().stream()
                .filter(elem -> onlyMatchExact()
                        ? elem.toString().equals(input)
                        : elem.toString().startsWith(input))
                .toList();

        if (matches.isEmpty()) {
            throw new CommandSyntaxException(null, Text.of("No such option '" + input + "'"));
        }

        if (matches.size() > 1) {
            throw new CommandSyntaxException(null, Text.of("Ambiguous option '" + input + "'"));
        }

        return matches.get(0);
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        return find(reader.readString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (var option : getSet()) {
            builder.suggest(Objects.toString(option));
        }

        return builder.buildFuture();
    }

}