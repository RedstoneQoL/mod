package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import tools.redstone.redstonetools.utils.BlockColor;

@AutoService(GenericArgumentType.class)
public class BlockColorSerializer extends EnumSerializer<BlockColor> {
    private static final BlockColorSerializer INSTANCE = new BlockColorSerializer();

    private BlockColorSerializer() {
        super(BlockColor.class);
    }

    public static BlockColorSerializer blockColor() {
        return INSTANCE;
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<BlockColorSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<BlockColorSerializer> {

            @Override
            public BlockColorSerializer createType(CommandRegistryAccess var1) {
                return blockColor();
            }

            @Override
            public ArgumentSerializer<BlockColorSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(BlockColorSerializer var1) {
            return new Properties();
        }
    }
}
