package tools.redstone.redstonetools.features.toggleable;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.features.feedback.FeedbackSender;
import tools.redstone.redstonetools.utils.ReflectionUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;
import tools.redstone.redstonetools.RedstoneToolsClient;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.arguments.Argument;
import tools.redstone.redstonetools.features.feedback.Feedback;
import tools.redstone.redstonetools.utils.ReflectionUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import java.util.ArrayList;
import java.util.List;

import static tools.redstone.redstonetools.RedstoneToolsClient.INJECTOR;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public abstract class ToggleableFeature extends AbstractFeature {

    private static final List<KeyBinding> keyBindings = new ArrayList<>();

    @Override
    public void register() {
        super.register();

        // load user settings
        // and register save hook
        loadConfig();
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            saveConfig();
        });

        var containsRequiredArguments = ReflectionUtils.getArguments(getClass()).stream()
                .anyMatch(a -> !a.isOptional());
        if (containsRequiredArguments) {
            return;
        }

        var info = ReflectionUtils.getFeatureInfo(getClass());
        var keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                info.name(),
                InputUtil.Type.KEYSYM,
                -1,
                "Redstone Tools"));

        keyBindings.add(keyBinding);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                assert client.player != null;
                client.player.networkHandler.sendChatCommand(info.command());
            }
        });
    }

    private static final Executor IO_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private volatile boolean enabled; // volatile for thread safety

    private final List<Argument<?>> arguments = ReflectionUtils.getArguments(getClass());

    private final Path configFile = RedstoneToolsClient.CONFIG_DIR
            .resolve("features").resolve(getID() + ".json");

    @SuppressWarnings({ "rawtypes", "unchecked" })

    @Override
    protected void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        var baseCommand = literal(getCommand())
                .executes(this::toggle);

        // add option configurations
        for (Argument argument : arguments) {
            String name = argument.getName();
            baseCommand.then(literal(name)
                    .executes(context -> {
                        Object value = argument.getValue();
                        return Feedback.success("Option {} of feature {} is set to: {}", name, getName(),
                                argument.getType().serialize(value)).send(context);
                    })
                    .then(argument("value", argument.getType()).executes(context -> {
                        Object value = context.getArgument("value", argument.getType().getTypeClass());

                        argument.setValue(value);

                        if (!enabled) {
                            enable(context);
                        }

                        IO_EXECUTOR.execute(this::saveConfig);

                        return Feedback.success("Set {} to {} for feature {}", name, value, getName()).send(context);
                    })));
        }

        dispatcher.register(baseCommand);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int toggle(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return toggle(context.getSource());
    }

    public int toggle(ServerCommandSource source) throws CommandSyntaxException {
        return !enabled ? enable(source) : disable(source);
    }

    public void setEnabled(boolean status) {
        if (status == enabled)
            return; // no work to do

        if (status) {
            enable();
        } else {
            disable();
        }
    }

    public void enable() {
        enabled = true;
        onEnable();
    }

    public int enable(ServerCommandSource source) throws CommandSyntaxException {
        enable();
        Feedback.success("Enabled feature {}", getName()).send(source);
        return 0;
    }

    public int enable(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return enable(context.getSource());
    }

    public void disable() {
        enabled = false;
        onDisable();
    }

    public int disable(ServerCommandSource source) throws CommandSyntaxException {
        disable();
        Feedback.success("Disabled feature {}", getName()).send(source);
        return 0;
    }

    public int disable(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return disable(context.getSource());
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    // todo: right now the configuration methods are assuming every
    // type is serialized to a string, this should be fixed in the future
    // but for now it works because every type right now serializes to a string
    // + it will probably be refactored soon

    /** Reloads the configuration from the disk. */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void loadConfig() {
        try {
            boolean enabled = false;
            if (Files.exists(configFile)) {
                JsonObject object = GSON.fromJson(new BufferedReader(
                        new InputStreamReader(Files.newInputStream(configFile), StandardCharsets.UTF_8)),
                        JsonObject.class);

                enabled = Boolean.parseBoolean(object.get("enabled").getAsString());

                // read options
                for (Argument argument : arguments) {
                    if (!object.has(argument.getName()))
                        continue;

                    String valueString = object.get(argument.getName()).getAsString();
                    Object value = argument.getType().deserialize(valueString);

                    argument.setValue(value);
                }
            }

            setEnabled(enabled);

            RedstoneToolsClient.LOGGER
                    .info("Loaded configuration for feature " + getID() + " file(" + configFile + ")");
        } catch (Exception e) {
            RedstoneToolsClient.LOGGER
                    .error("Failed to load configuration for feature " + getID() + " file(" + configFile + ")");
            e.printStackTrace();
        }
    }

    /** Saves the configuration to the disk. */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void saveConfig() {
        try {
            if (!Files.exists(configFile)) {
                if (!Files.exists(configFile.getParent()))
                    Files.createDirectories(configFile.getParent());
                Files.createFile(configFile);
            }

            // serialize configuration
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("enabled", Boolean.toString(enabled));
            for (Argument argument : arguments) {
                Object value = argument.getValue();
                String valueSerialized = (String) argument.getType().serialize(value);

                jsonObject.addProperty(argument.getName(), valueSerialized);
            }

            // write json document
            String json = GSON.toJson(jsonObject);
            OutputStream outputStream = Files.newOutputStream(configFile);
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            outputStream.close();

            RedstoneToolsClient.LOGGER.info("Saved configuration for feature " + getID() + " file(" + configFile + ")");
        } catch (Exception e) {
            RedstoneToolsClient.LOGGER
                    .error("Failed to save configuration for feature " + getID() + " file(" + configFile + ")");
            e.printStackTrace();
        }
    }

}
