package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import tools.redstone.redstonetools.utils.ColoredBlockType;

@AutoService(GenericArgumentType.class)
public class ColoredBlockTypeSerializer extends EnumSerializer<ColoredBlockType> {
    private static final ColoredBlockTypeSerializer INSTANCE = new ColoredBlockTypeSerializer();

    private ColoredBlockTypeSerializer() {
        super(ColoredBlockType.class);
    }

    public static ColoredBlockTypeSerializer coloredBlockType() {
        return INSTANCE;
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<ColoredBlockTypeSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<ColoredBlockTypeSerializer> {

            @Override
            public ColoredBlockTypeSerializer createType(CommandRegistryAccess var1) {
                return coloredBlockType();
            }

            @Override
            public ArgumentSerializer<ColoredBlockTypeSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(ColoredBlockTypeSerializer serializer) {
            return new Properties();
        }
    }
}
