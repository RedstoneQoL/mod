package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ArgumentSerializer.ArgumentTypeProperties;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * Base class for the 'wrapped' argument type.
 *
 * @param <T> The value type.
 * @param <S> The serialized type.
 */
public abstract class GenericArgumentType<T, S> implements ArgumentType<T> {

    protected final Class<T> clazz;

    // TODO: Consider moving this constructor to enum serializer as it's the only
    // class that uses the clazz field
    protected GenericArgumentType(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getTypeClass() {
        return clazz;
    }

    /* ArgumentType impl */
    @Override
    public final T parse(StringReader reader) throws CommandSyntaxException {
        return deserialize(reader);
    }

    /* String Serialization */
    public abstract T deserialize(StringReader reader) throws CommandSyntaxException;

    public abstract T deserialize(S serialized);

    public abstract S serialize(T value);

    /* Usage In Commands */
    public abstract Collection<String> getExamples();

    public abstract <R> CompletableFuture<Suggestions> listSuggestions(CommandContext<R> context,
            SuggestionsBuilder builder);


    public static abstract class Serializer<A extends ArgumentType<?>, T extends ArgumentTypeProperties<A>>
            implements ArgumentSerializer<A, T> {

        public void writePacket(T properties, PacketByteBuf packetByteBuf) {
            var jsonObject = new JsonObject();
            writeJson(properties, jsonObject);
            packetByteBuf.writeString(jsonObject.toString());
        }

        public T fromPacket(PacketByteBuf packetByteBuf) {
            String json = packetByteBuf.readString();
            TypeToken<T> typeToken = new TypeToken<>() {
            };
            var jsonReader = new JsonReader(new java.io.StringReader(json));
            return new Gson().fromJson(jsonReader, typeToken.getType());
        }

        public void writeJson(T var1, JsonObject var2) {
            var gson = new Gson();
            var newObject = gson.fromJson(gson.toJson(var1), JsonObject.class);
            newObject.entrySet().forEach(entry -> var2.addProperty(entry.getKey(), entry.getValue().toString()));
        }
    }

}
