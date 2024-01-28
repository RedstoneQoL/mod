package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.text.Text;
import tools.redstone.redstonetools.utils.NumberBase;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@AutoService(GenericArgumentType.class)
public class NumberBaseArgumentType extends GenericArgumentType<Integer, String> {
    private static final IntegerSerializer INT_SERIALIZER = IntegerSerializer.integer(2, 36);
    private static final NumberBaseArgumentType INSTANCE = new NumberBaseArgumentType();

    public static NumberBaseArgumentType numberBase() {
        return INSTANCE;
    }

    protected NumberBaseArgumentType() {
        super(Integer.class);
    }

    @Override
    public Integer deserialize(StringReader reader) throws CommandSyntaxException {
        var input = reader.readUnquotedString();

        try {
            return deserialize(input);
        } catch (IllegalArgumentException e) {
            throw new CommandSyntaxException(null, Text.of(e.getMessage()));
        }
    }

    @Override
    public Integer deserialize(String serialized) {
        try {
            return NumberBase.fromString(serialized)
                    .map(NumberBase::toInt)
                    .orElseGet(() -> INT_SERIALIZER.deserialize(serialized));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid base '" + serialized + "'.", e);
        }
    }

    @Override
    public String serialize(Integer value) {
        return NumberBase.fromInt(value)
                .map(NumberBase::toString)
                .orElseGet(() -> INT_SERIALIZER.serialize(value));
    }

    public String serialize(NumberBase value) {
        return serialize(value.toInt());
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.stream(NumberBase.values())
                .map(this::serialize)
                .toList();
    }

    @Override
    public <R> CompletableFuture<Suggestions> listSuggestions(CommandContext<R> context, SuggestionsBuilder builder) {
        for (var value : NumberBase.values()) {
            builder = builder.suggest(serialize(value));
        }

        return builder.buildFuture();
    }

    public static class NumberBaseSerializer extends Serializer<NumberBaseArgumentType, ArgumentSerializer.ArgumentTypeProperties<NumberBaseArgumentType>>{

        @Override
        public ArgumentTypeProperties<NumberBaseArgumentType> getArgumentTypeProperties(NumberBaseArgumentType argumentType) {
            return new Properties();
        }

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<NumberBaseArgumentType>{

            @Override
            public NumberBaseArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
                return new NumberBaseArgumentType();
            }

            @Override
            public ArgumentSerializer<NumberBaseArgumentType, ?> getSerializer() {
                return NumberBaseSerializer.this;
            }
        }

    }


}
