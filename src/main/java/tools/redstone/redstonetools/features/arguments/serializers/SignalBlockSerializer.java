package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import tools.redstone.redstonetools.utils.SignalBlock;

@AutoService(GenericArgumentType.class)
public class SignalBlockSerializer extends EnumSerializer<SignalBlock> {
    private static final SignalBlockSerializer INSTANCE = new SignalBlockSerializer();

    private SignalBlockSerializer() {
        super(SignalBlock.class);
    }

    public static SignalBlockSerializer signalBlock() {
        return INSTANCE;
    }

    public static class Serializer
            extends GenericArgumentType.Serializer<SignalBlockSerializer, Serializer.Properties> {

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<SignalBlockSerializer> {

            @Override
            public SignalBlockSerializer createType(CommandRegistryAccess var1) {
                return signalBlock();
            }

            @Override
            public ArgumentSerializer<SignalBlockSerializer, ?> getSerializer() {
                return new Serializer();
            }
        }

        @Override
        public Properties getArgumentTypeProperties(SignalBlockSerializer serializer) {
            return new Properties();
        }
    }
}
