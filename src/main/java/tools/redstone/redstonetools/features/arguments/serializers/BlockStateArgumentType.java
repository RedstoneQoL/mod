package tools.redstone.redstonetools.features.arguments.serializers;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.registry.Registries;

@AutoService(GenericArgumentType.class)
public class BlockStateArgumentType extends BrigadierArgumentType<BlockStateArgument, String> {

    private static BlockStateArgumentType INSTANCE;

    private BlockStateArgumentType(CommandRegistryAccess registryAccess) {
        super(BlockStateArgument.class, net.minecraft.command.argument.BlockStateArgumentType.blockState(registryAccess));
    }

    public static BlockStateArgumentType blockState(CommandRegistryAccess registryAccess) {
        if (INSTANCE == null) {
            INSTANCE = new BlockStateArgumentType(registryAccess);
        }

        return INSTANCE;
    }

    @Override
    public BlockStateArgument deserialize(String serialized) {
        try {
            return deserialize(new StringReader(serialized));
        } catch (CommandSyntaxException e) {
            throw new IllegalStateException("Syntax Exception: " + e.getMessage());
        }
    }

    @Override
    public String serialize(BlockStateArgument value) {
        var state = value.getBlockState();
        var block = state.getBlock();

        var builder = new StringBuilder()
                .append(Registries.BLOCK.getId(block));

        if (state.getProperties().size() == 0) {
            return builder.toString();
        }

        builder.append('[');
        var first = true;
        for (var prop : state.getProperties()) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }

            builder.append(prop.getName())
                    .append('=')
                    .append(state.get(prop));
        }
        builder.append(']');

        return builder.toString();
    }

    public static class BlockStateArgumentSerializer extends Serializer<BlockStateArgumentType, ArgumentSerializer.ArgumentTypeProperties<BlockStateArgumentType>>{

        @Override
        public ArgumentTypeProperties<BlockStateArgumentType> getArgumentTypeProperties(BlockStateArgumentType argumentType) {
            return new Properties();
        }

        public final class Properties
                implements ArgumentSerializer.ArgumentTypeProperties<BlockStateArgumentType>{

            @Override
            public BlockStateArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
                return new BlockStateArgumentType(commandRegistryAccess);
            }

            @Override
            public ArgumentSerializer<BlockStateArgumentType, ?> getSerializer() {
                return BlockStateArgumentSerializer.this;
            }
        }
    }

}
