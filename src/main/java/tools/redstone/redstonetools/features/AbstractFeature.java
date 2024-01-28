package tools.redstone.redstonetools.features;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;

public abstract class AbstractFeature {

    private final Feature featureInfo;
    private final String id;

    {
        featureInfo = getClass().getAnnotation(Feature.class);

        if (featureInfo == null) {
            throw new IllegalStateException("Feature " + getClass() + " is not annotated with @Feature");
        }

        String id = featureInfo.id();
        if (id.isEmpty()) {
            // derive id from name
            // Air Place -> airplace
            id = featureInfo.name()
                    .toLowerCase()
                    .replace(" ", "");
        }

        this.id = id;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return featureInfo.name();
    }

    public String getDescription() {
        return featureInfo.description();
    }

    public String getCommand() {
        return featureInfo.command();
    }

    public boolean requiresWorldEdit() {
        return featureInfo.worldedit();
    }

    /**
     * Register this feature.
     */
    public void register() {
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    protected abstract void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment);

}
