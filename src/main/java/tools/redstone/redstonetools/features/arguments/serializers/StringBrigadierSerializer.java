package tools.redstone.redstonetools.features.arguments.serializers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public abstract class StringBrigadierSerializer<T> extends BrigadierArgumentType<T, String> {

    public StringBrigadierSerializer(Class<T> clazz, ArgumentType<T> argumentType) {
        super(clazz, argumentType);
    }

    @Override
    public T deserialize(String serialized) {
        try {
            return deserialize(new StringReader(serialized));
        } catch (CommandSyntaxException e) {
            throw new IllegalStateException("Syntax Exception: " + e.getMessage());
        }
    }

}
