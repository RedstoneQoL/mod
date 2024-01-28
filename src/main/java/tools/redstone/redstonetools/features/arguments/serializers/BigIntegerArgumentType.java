package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

import java.math.BigInteger;
import java.util.Optional;

@AutoService(GenericArgumentType.class)
public class BigIntegerArgumentType extends IntLikeArgumentType<BigInteger> {
    private static final BigIntegerArgumentType INSTANCE = new BigIntegerArgumentType(null, null);

    public static BigIntegerArgumentType bigInteger() {
        return INSTANCE;
    }

    public static BigIntegerArgumentType bigInteger(BigInteger min) {
        return new BigIntegerArgumentType(min, null);
    }

    public static BigIntegerArgumentType bigInteger(BigInteger min, BigInteger max) {
        return new BigIntegerArgumentType(min, max);
    }

    private BigIntegerArgumentType(BigInteger min, BigInteger max) {
        super(BigInteger.class, min, max);

    }

    @Override
    protected Optional<BigInteger> tryParseOptional(String string, int radix) {
        try {
            return Optional.of(new BigInteger(string, radix));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static class BigIntegerSerializer extends Serializer<BigIntegerArgumentType, ArgumentSerializer.ArgumentTypeProperties<BigIntegerArgumentType>>{

        @Override
        public ArgumentTypeProperties<BigIntegerArgumentType> getArgumentTypeProperties(BigIntegerArgumentType argumentType) {
            return new Properties(argumentType.max,argumentType.min);
        }

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<BigIntegerArgumentType>{
            final BigInteger max, min;

            public Properties(BigInteger max, BigInteger min) {
                this.max = max;
                this.min = min;
            }

            @Override
            public BigIntegerArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
                return new BigIntegerArgumentType(this.max,this.min);
            }

            @Override
            public ArgumentSerializer<BigIntegerArgumentType, ?> getSerializer() {
                return BigIntegerSerializer.this;
            }
        }
    }




}
