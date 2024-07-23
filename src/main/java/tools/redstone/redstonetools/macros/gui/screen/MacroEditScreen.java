package tools.redstone.redstonetools.macros.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import tools.redstone.redstonetools.macros.Macro;
import tools.redstone.redstonetools.macros.MacroManager;
import tools.redstone.redstonetools.macros.actions.Action;
import tools.redstone.redstonetools.macros.actions.CommandAction;
import tools.redstone.redstonetools.macros.gui.widget.commandlist.CommandListWidget;
import tools.redstone.redstonetools.macros.gui.widget.macrolist.MacroListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tools.redstone.redstonetools.utils.KeyBindingUtils;

import java.util.List;

import static tools.redstone.redstonetools.RedstoneToolsClient.INJECTOR;


public class MacroEditScreen extends GameOptionsScreen {
    private final MacroListWidget macroListWidget;
    private final Macro macro;

    private CommandListWidget commandList;
    private TextFieldWidget nameField;
    private ButtonWidget doneButton;
    private ButtonWidget keyBindButton;

    private boolean overlapped = false;
    private boolean detectingKeycodeKey = false;

    public MacroEditScreen(Screen parent, GameOptions gameOptions, Text title, MacroListWidget macroListWidget) {
        super(parent, gameOptions, title);
        this.macroListWidget = macroListWidget;
        this.macro = Macro.buildEmpty();
    }

    public MacroEditScreen(Screen parent, GameOptions gameOptions, Text title, MacroListWidget macroListWidget, Macro macro) {
        super(parent, gameOptions, title);
        this.macroListWidget = macroListWidget;
        this.macro = macro.createCopy();
    }

    @Override
    protected void initBody() {
        overlapped = false;
        nameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 20, Text.of(""));
        nameField.setText(macro.name.trim());

        nameField.setChangedListener(s -> {
            macro.name = s.trim();
            doneButton.active = canClickDone();
        });

        Key keyCode = macro.getKey();
        Text text = keyCode.getLocalizedText();
        if (keyCode == InputUtil.UNKNOWN_KEY) text = Text.of("");
        if ( KeyBindingUtils.isKeyAlreadyBound(keyCode) ) { text = Text.literal(text.getString()).formatted(Formatting.RED); }

        keyBindButton = ButtonWidget.builder(text, (button) -> {
            detectingKeycodeKey = true;
            keyBindButton.setMessage((Text.literal("> ")).append(keyBindButton.getMessage().copy().formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW));
        }).build();
        if (detectingKeycodeKey) keyBindButton.onPress();

        int widgetWidth = 339;
        List<CommandListWidget.CommandEntry> entries = null;
        double scrollAmount = 0;
        if (commandList != null) {
            entries = commandList.children();
            scrollAmount = commandList.getScrollAmount();
        }

        commandList = new CommandListWidget(client, this, widgetWidth, this.height / 4 + 144 + 5 - 10-85, 85, 24);
        commandList.setX(width / 2 - widgetWidth / 2);

        if (entries != null) {
            commandList.children().clear();
            for (CommandListWidget.CommandEntry entry : entries) {
                entry.setOwner(commandList);
                commandList.children().add(entry);
            }
        } else {
            for (Action action : macro.actions) {
                if (action instanceof CommandAction commandAction) {
                    commandList.addCommand(commandAction.command);
                }
            }
        }
        commandList.setScrollAmount(scrollAmount);

        this.layout.addBody(nameField);
        this.layout.addBody(keyBindButton);
        this.layout.addBody(commandList);
    }

    @Override
    protected void initFooter() {
        doneButton = ButtonWidget.builder(Text.of("Done"), (button) -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) return;

            updateMacroActions();

            if (!macro.isCopy()) macroListWidget.addMacro(macro);
            else macro.applyChangesToOriginal();

            INJECTOR.getInstance(MacroManager.class).saveChanges();

            client.setScreen(parent);
        }).build();

        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.add(doneButton);
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> close()).build());
    }

    @Override
    protected void addOptions() {
    }

    @Override
    protected void initTabNavigation() {
        this.layout.refreshPositions();

        nameField.setDimensionsAndPosition(200, 20, this.width / 2 - 100, 30);

        keyBindButton.setDimensionsAndPosition(75, 20, this.width / 2 + 26, 55);

        commandList.setDimensionsAndPosition(339, this.height / 4 + 144 + 5 - 10 - 80,
                this.width / 2 - 339 / 2, 80);
        commandList.refreshScroll();
    }

    private boolean canClickDone() {
        return !nameField.getText().trim().isEmpty() && macroListWidget.canAdd(macro);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (overlapped) {
            mouseX = -1;
            mouseY = -1;
        }

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, "Key Bind", width / 2 - (99 - textRenderer.getWidth(
                "Key Bind") / 2), 55 + textRenderer.fontHeight / 2, 16777215);

        if (nameField.getText().isEmpty() && !nameField.isFocused()) {
            nameField.setSuggestion("Name");
        } else {
            nameField.setSuggestion("");
        }

        doneButton.active = canClickDone();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        if (overlapped) client.setScreen(new CommandEditScreen(this,gameOptions,commandList.getFocused().command));
    }


    @Override
    public void close(){
        if (!macro.isCopy() && macro.isEmpty()) {
            super.close();
            return;
        }

        updateMacroActions();
        if (macro.needsSaving()) {
            client.setScreen(new ConfirmScreen(accept -> {
                if (accept) client.setScreen(parent);
                else client.setScreen(this);
            }, Text.of("Unsaved changes"), Text.of("Are you sure you want to discard changes?")));
        } else {
            super.close();
        }
    }

    private void updateMacroActions() {
        macro.actions.clear();

        for (String command : commandList.getCommandList()) {
            macro.actions.add(new CommandAction(command));
        }
    }

    public void editCommandField(TextFieldWidget commandField) {
        client.setScreen(new CommandEditScreen(this,gameOptions, commandField));
        overlapped = true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Key key = InputUtil.fromKeyCode(keyCode, scanCode);
        if (keyCode == InputUtil.GLFW_KEY_ESCAPE) key = InputUtil.UNKNOWN_KEY;

        if (updateKeybinding(key)) return false;
        if (commandList.keyPressed(keyCode, scanCode, modifiers)) return false;
        if (keyCode == InputUtil.GLFW_KEY_TAB || keyCode == InputUtil.GLFW_KEY_SPACE) return false;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (commandList.keyReleased(keyCode, scanCode, modifiers)) return false;

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (commandList.charTyped(chr, modifiers)) return false;

        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!commandList.isMouseOver(mouseX, mouseY)) commandList.mouseClicked(mouseX, mouseY, button);
        if (updateKeybinding(Type.MOUSE.createFromCode(button))) return false;


        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean updateKeybinding(Key key) {
        if (detectingKeycodeKey) {
            detectingKeycodeKey = false;
            Text text = key.getLocalizedText();
            if (key == InputUtil.UNKNOWN_KEY) text = Text.of("");
            if ( KeyBindingUtils.isKeyAlreadyBound(key) ) { text = Text.literal(text.getString()).formatted(Formatting.RED); }

            keyBindButton.setMessage(text);
            macro.setKey(key);
            return true;
        }

        return false;
    }


}
