package tools.redstone.redstonetools.features.arguments.serializers;

import java.util.Optional;

import com.google.auto.service.AutoService;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

@AutoService(GenericArgumentType.class)
public class LongSerializer extends IntLikeArgumentType<Long> {
    private static final LongSerializer INSTANCE = new LongSerializer(Long.MIN_VALUE, Long.MAX_VALUE);

    public static LongSerializer longArg() {
        return INSTANCE;
    }

    public static LongSerializer longArg(long min) {
        return new LongSerializer(min, Long.MAX_VALUE);
    }

    public static LongSerializer longArg(long min, long max) {
        return new LongSerializer(min, max);
    }

    private LongSerializer(long min, long max) {
        super(Long.class, min, max);
    }

    @Override
    protected Optional<Long> tryParseOptional(String string, int radix) {
        try {
            return Optional.of(Long.parseLong(string, radix));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<LongSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<LongSerializer> {

            @Override
            public LongSerializer createType(CommandRegistryAccess var1) {
                return longArg();
            }

            @Override
            public ArgumentSerializer<LongSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(LongSerializer serializer) {
            return new Properties();
        }
    }
}
