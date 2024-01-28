package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

@AutoService(GenericArgumentType.class)
public class StringSerializer extends StringBrigadierSerializer<String> {

    private static final StringSerializer INSTANCE_WORD = new StringSerializer(StringArgumentType.word());
    private static final StringSerializer INSTANCE_STRING = new StringSerializer(StringArgumentType.string());
    private static final StringSerializer INSTANCE_GREEDY_STRING = new StringSerializer(
            StringArgumentType.greedyString());

    public static StringSerializer string() {
        return INSTANCE_STRING;
    }

    public static StringSerializer word() {
        return INSTANCE_WORD;
    }

    public static StringSerializer greedyString() {
        return INSTANCE_GREEDY_STRING;
    }

    protected StringSerializer(ArgumentType<String> argumentType) {
        super(String.class, argumentType);
    }

    @Override
    public String serialize(String value) {
        // TODO: Check if this is correct, doesn't StringArgumentType.string() require
        // quotes which this doesn't add?
        return value;
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<StringSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<StringSerializer> {

            @Override
            public StringSerializer createType(CommandRegistryAccess var1) {
                return string();
            }

            @Override
            public ArgumentSerializer<StringSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(StringSerializer serializer) {
            return new Properties();
        }
    }

}
