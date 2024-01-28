package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import tools.redstone.redstonetools.utils.DirectionArgument;

@AutoService(GenericArgumentType.class)
public class DirectionSerializer extends EnumSerializer<DirectionArgument> {
    private static final DirectionSerializer INSTANCE = new DirectionSerializer();

    private DirectionSerializer() {
        super(DirectionArgument.class);
    }

    public static DirectionSerializer direction() {
        return INSTANCE;
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<DirectionSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<DirectionSerializer> {

            @Override
            public DirectionSerializer createType(CommandRegistryAccess var1) {
                return direction();
            }

            @Override
            public ArgumentSerializer<DirectionSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(DirectionSerializer serializer) {
            return new Properties();
        }
    }
}
