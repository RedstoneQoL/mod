package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;

@AutoService(GenericArgumentType.class)
public class BoolSerializer extends StringBrigadierSerializer<Boolean> {

    private static final BoolSerializer INSTANCE = new BoolSerializer(BoolArgumentType.bool());

    public static BoolSerializer bool() {
        return INSTANCE;
    }

    private BoolSerializer(ArgumentType<Boolean> argumentType) {
        super(Boolean.class, argumentType);
    }

    @Override
    public String serialize(Boolean value) {
        return String.valueOf(value);
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<BoolSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<BoolSerializer> {

            @Override
            public BoolSerializer createType(CommandRegistryAccess var1) {
                return bool();
            }

            @Override
            public ArgumentSerializer<BoolSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(BoolSerializer serializer) {
            return new Properties();
        }
    }

}
