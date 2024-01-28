package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

@AutoService(GenericArgumentType.class)
public class FloatSerializer extends StringBrigadierSerializer<Float> {

    private static final FloatSerializer INSTANCE = new FloatSerializer(FloatArgumentType.floatArg());

    public static FloatSerializer floatArg() {
        return INSTANCE;
    }

    public static FloatSerializer floatArg(float min) {
        return new FloatSerializer(FloatArgumentType.floatArg(min));
    }

    public static FloatSerializer floatArg(float min, float max) {
        return new FloatSerializer(FloatArgumentType.floatArg(min, max));
    }

    private FloatSerializer(ArgumentType<Float> argumentType) {
        super(Float.class, argumentType);
    }

    @Override
    public String serialize(Float value) {
        return String.valueOf(value);
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<FloatSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<FloatSerializer> {

            @Override
            public FloatSerializer createType(CommandRegistryAccess var1) {
                return floatArg();
            }

            @Override
            public ArgumentSerializer<FloatSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(FloatSerializer serializer) {
            return new Properties();
        }
    }
}
