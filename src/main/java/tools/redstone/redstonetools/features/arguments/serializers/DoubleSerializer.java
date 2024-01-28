package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

@AutoService(GenericArgumentType.class)
public class DoubleSerializer extends StringBrigadierSerializer<Double> {

    private static final DoubleSerializer INSTANCE = new DoubleSerializer(DoubleArgumentType.doubleArg());

    public static DoubleSerializer doubleArg() {
        return INSTANCE;
    }

    public static DoubleSerializer doubleArg(double min) {
        return new DoubleSerializer(DoubleArgumentType.doubleArg(min));
    }

    public static DoubleSerializer doubleArg(double min, double max) {
        return new DoubleSerializer(DoubleArgumentType.doubleArg(min, max));
    }

    private DoubleSerializer(ArgumentType<Double> argumentType) {
        super(Double.class, argumentType);
    }

    @Override
    public String serialize(Double value) {
        return String.valueOf(value);
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<DoubleSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<DoubleSerializer> {

            @Override
            public DoubleSerializer createType(CommandRegistryAccess var1) {
                return doubleArg();
            }

            @Override
            public ArgumentSerializer<DoubleSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(DoubleSerializer serializer) {
            return new Properties();
        }
    }
}
