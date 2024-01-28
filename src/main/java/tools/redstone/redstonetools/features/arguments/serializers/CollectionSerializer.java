package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Serializes any collection type as a list,
 * and handles it specially for configuration.
 *
 * @param <E> The element type.
 * @param <C> The collection type.
 */
@AutoService(GenericArgumentType.class)
public class CollectionSerializer<E, C extends Collection<E>>
        extends GenericArgumentType<C, List<Object>> {

    public static <E> CollectionSerializer<E, List<E>> listOf(GenericArgumentType<E, ?> element) {
        return new CollectionSerializer<>(List.class, element, ArrayList::new);
    }

    public static <E> CollectionSerializer<E, Set<E>> setOf(GenericArgumentType<E, ?> element) {
        return new CollectionSerializer<>(Set.class, element, HashSet::new);
    }

    final GenericArgumentType<E, Object> elementType;
    final Function<Collection<E>, C> collectionFactory;

    // cache example because
    // its relatively expensive
    // to compute correctly
    final String example;

    @SuppressWarnings("unchecked")
    protected CollectionSerializer(Class<?> clazz,
            GenericArgumentType<E, ?> elementType,
            Function<Collection<E>, C> collectionFactory) {
        super((Class<C>) clazz);
        this.elementType = (GenericArgumentType<E, Object>) elementType;
        this.collectionFactory = collectionFactory;

        // build example
        StringBuilder b = new StringBuilder("[");
        for (String elemStr : elementType.getExamples()) {
            b.append(elemStr);
            b.append(", ");
        }

        this.example = b.delete(b.length() - 3, b.length()).append("]").toString();
    }

    public GenericArgumentType<E, ?> getElementType() {
        return elementType;
    }

    @Override
    public C deserialize(StringReader reader) throws CommandSyntaxException {
        List<E> list = new ArrayList<>();
        reader.expect('[');
        reader.skipWhitespace();
        if (reader.peek() == ']') { // empty list
            reader.skip();
            return collectionFactory.apply(list);
        }

        while (reader.canRead()) {
            E element = elementType.deserialize(reader);
            list.add(element);
            reader.skipWhitespace();
            if (reader.peek() == ']')
                break;
            reader.expect(',');
            reader.skipWhitespace();
        }

        reader.skipWhitespace();
        reader.expect(']');

        return collectionFactory.apply(list);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("[a, b, c]");
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining();

        if (remaining.isBlank()) {
            // suggest opening the list
            builder.suggest("[");
        } else {
            builder.suggest(",");
            builder.suggest("]");

            // suggest element options
            // the start index is the index
            // of the last comma if open, otherwise
            // it will be -1 and no elements will
            // be suggested
            // TODO: fix because this doesnt work for some reason
            StringReader inputParser = new StringReader(remaining);
            int startIndex = -1;
            try {
                inputParser.skip(); // [
                while (inputParser.canRead()) {
                    // skip element
                    int oldCursor = inputParser.getCursor();
                    try {
                        elementType.deserialize(inputParser);
                    } catch (CommandSyntaxException ignored) {
                    }
                    if (oldCursor == inputParser.getCursor())
                        break;
                    inputParser.skipWhitespace();

                    // skip and register comma
                    if (inputParser.peek() == ',') {
                        startIndex = inputParser.getCursor();
                    }

                    inputParser.skip();
                    if (!inputParser.canRead())
                        break;

                    // end on closer
                    if (inputParser.peek() == ']') {
                        startIndex = -1;
                        break;
                    }

                    inputParser.skipWhitespace();
                }
            } catch (Exception e) {
                startIndex = -1;
            }

            if (startIndex != -1) {
                SuggestionsBuilder b2 = new SuggestionsBuilder(
                        remaining,
                        remaining.toLowerCase(),
                        startIndex + 1);

                elementType.listSuggestions(context, b2);
                builder.add(b2);
            }
        }

        return builder.buildFuture();
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public C deserialize(List<Object> in) {
        List<E> elementList = in.stream()
                .map(elementType::deserialize)
                .toList();
        return collectionFactory.apply(elementList);
    }

    @Override
    public List<Object> serialize(C value) {
        return value
                .stream()
                .map(elementType::serialize)
                .toList();
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<CollectionSerializer<?, ?>, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<CollectionSerializer<?, ?>> {

            @Override
            public CollectionSerializer<?, ?> createType(CommandRegistryAccess var1) {
                // TODO: Actually make this work, this is currently a work around to get it to
                // compile
                return listOf(IntegerSerializer.integer());
            }

            @Override
            public ArgumentSerializer<CollectionSerializer<?, ?>, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(CollectionSerializer<?, ?> serializer) {
            return new Properties();
        }
    }
}
