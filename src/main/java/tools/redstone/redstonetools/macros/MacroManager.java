package tools.redstone.redstonetools.macros;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import tools.redstone.redstonetools.RedstoneToolsClient;
import tools.redstone.redstonetools.macros.actions.Action;
import tools.redstone.redstonetools.macros.actions.CommandAction;

import javax.inject.Singleton;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MacroManager {
    private final Path macrosFilePath;
    private final List<Macro> macros;

    public MacroManager() {
        macrosFilePath = MinecraftClient.getInstance().runDirectory.toPath()
                .resolve("config")
                .resolve("redstonetools")
                .resolve("macros.json");


        JsonArray macrosJson = null;
        try {
            Files.createDirectories(macrosFilePath.getParent());
            if (Files.exists(macrosFilePath)) {
                var reader = Json.createReader(new FileReader(macrosFilePath.toFile()));
                macrosJson = reader.readArray();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (macrosJson == null) {
            macros = new ArrayList<>();
            macros.addAll(getDefaultMacros());
        } else {
            macros = getMacrosFromJson(macrosJson);
        }
    }

    public List<Macro> getMacros() {
        return macros;
    }

    public Macro getMacro(String name) {
        for (Macro macro : macros) {
            if (macro.name.equals(name)) {
                return macro;
            }
        }

        return null;
    }

    public void addMacro(Macro macro) {
        macros.add(macro);

        saveChanges();
    }

    public void removeMacro(Macro macro) {
        macros.remove(macro);

        saveChanges();
    }

    public void saveChanges() {
        // Write %appdata%/.minecraft/config/redstonetools/macros.json
        try {
            Files.createDirectories(macrosFilePath.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        var macrosJson = Json.createArrayBuilder();
        for (Macro macro : macros) {
            macrosJson.add(getMacroJson(macro));
        }

        try (var writer = Json.createWriter(new FileWriter(macrosFilePath.toFile()))) {
            writer.writeArray(macrosJson.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonObject getMacroJson(Macro macro) {
        var actionsJson = Json.createArrayBuilder();
        for (Action action : macro.actions) {
            actionsJson.add(getActionJson(action));
        }

        return Json.createObjectBuilder()
                .add("name", macro.name)
                .add("enabled", macro.enabled)
                .add("output", macro.output)
                .add("key", macro.getKey().getTranslationKey())
                .add("actions", actionsJson)
                .build();
    }

    private JsonObject getActionJson(Action action) {
        if (action instanceof CommandAction commandAction) {
            return Json.createObjectBuilder()
                    .add("type", "command")
                    .add("command", commandAction.command)
                    .build();
        }

        throw new RuntimeException("Unknown action type: " + action.getClass().getName());
    }

    private List<Macro> getDefaultMacros() {
        return List.of(
                createCommandMacro("redstoner", new String[] {
                        "/gamerule doTileDrops false",
                        "/gamerule doTraderSpawning false",
                        "/gamerule doWeatherCycle false",
                        "/gamerule doDaylightCycle false",
                        "/gamerule doMobSpawning false",
                        "/gamerule doContainerDrops false",
                        "/time set noon",
                        "/weather clear"
                })
        );
    }

    private Macro createCommandMacro(String name, String[] commands) {
        var actions = new Action[commands.length];
        for (int i = 0; i < commands.length; i++) {
            actions[i] = new CommandAction(commands[i]);
        }

        return new Macro(name, true, InputUtil.UNKNOWN_KEY, List.of(actions), true);
    }

    private List<Macro> getMacrosFromJson(JsonArray macrosJson) {
        List<Macro> macros = new ArrayList<>();

        for (int i = 0; i < macrosJson.size(); i++) {
            macros.add(getMacroFromJson(macrosJson.getJsonObject(i)));
        }

        return macros;
    }

    private Macro getMacroFromJson(JsonObject macroJson) {
        var name = macroJson.getString("name");
        var enabled = macroJson.getBoolean("enabled");
        var output = macroJson.getBoolean("output");
        var key = macroJson.getString("key");
        var actions = getActionsFromJson(macroJson.getJsonArray("actions"));

        return new Macro(name, enabled, InputUtil.fromTranslationKey(key), actions, output);
    }

    private List<Action> getActionsFromJson(JsonArray actionsJson) {
        List<Action> actions = new ArrayList<>();

        for (int i = 0; i < actionsJson.size(); i++) {
            actions.add(getActionFromJson(actionsJson.getJsonObject(i)));
        }

        return actions;
    }

    private Action getActionFromJson(JsonObject actionJson) {
        var type = actionJson.getString("type");

        if ("command".equals(type)) {
            return new CommandAction(actionJson.getString("command"));
        }

        throw new RuntimeException("Unknown action type: " + type);
    }

    public void updateMacroKeys() {
        for (Macro macro : macros) {
            macro.changeKeyBindingKeyCode();
        }
    }

}
