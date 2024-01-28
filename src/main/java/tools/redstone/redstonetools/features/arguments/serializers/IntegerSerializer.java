package tools.redstone.redstonetools.features.arguments.serializers;

import java.util.Optional;

import com.google.auto.service.AutoService;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

@AutoService(GenericArgumentType.class)
public class IntegerSerializer extends IntLikeArgumentType<Integer> {
    private static final IntegerSerializer INSTANCE = new IntegerSerializer(Integer.MIN_VALUE, Integer.MAX_VALUE);

    public static IntegerSerializer integer() {
        return INSTANCE;
    }

    public static IntegerSerializer integer(int min) {
        return new IntegerSerializer(min, Integer.MAX_VALUE);
    }

    public static IntegerSerializer integer(int min, int max) {
        return new IntegerSerializer(min, max);
    }

    private IntegerSerializer(int min, int max) {
        super(Integer.class, min, max);
    }

    @Override
    protected Optional<Integer> tryParseOptional(String string, int radix) {
        try {
            return Optional.of(Integer.parseInt(string, radix));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<IntegerSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<IntegerSerializer> {

            @Override
            public IntegerSerializer createType(CommandRegistryAccess var1) {
                return integer();
            }

            @Override
            public ArgumentSerializer<IntegerSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(IntegerSerializer var1) {
            return new Properties();
        }
    }
}
