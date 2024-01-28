package tools.redstone.redstonetools.macros.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import tools.redstone.redstonetools.macros.gui.MacroCommandSuggestor;


public class CommandEditScreen extends GameOptionsScreen {

    private final TextFieldWidget commandField;
    private final MacroCommandSuggestor commandMacroCommandSuggestor;
    private boolean changed = false;

    public CommandEditScreen(Screen parent, GameOptions gameOptions, TextFieldWidget commandField) {
        super(parent, gameOptions, Text.of(""));
        this.commandField = commandField;
        client = MinecraftClient.getInstance();
        this.commandMacroCommandSuggestor = new MacroCommandSuggestor(client, parent, commandField,client.textRenderer,true,false, commandField.getY() -20,5,-805306368);

        commandField.setChangedListener((s) -> changed = true);
        commandMacroCommandSuggestor.setWindowActive(true);
        commandMacroCommandSuggestor.refresh();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        parent.render(context, mouseX, mouseY, delta);
        context.getMatrices().translate(0,0,0.03F);

        context.fillGradient( 0, 0, this.width, this.height, -1072689136, -804253680);

        commandField.render(context, mouseX, mouseY, delta);

        commandMacroCommandSuggestor.render(context, mouseX, mouseY);
        commandMacroCommandSuggestor.tryRenderWindow(context,mouseX,mouseY);

        if (changed) {
            commandMacroCommandSuggestor.refresh();
            changed = false;
        }

        super.render(context, mouseX, mouseY, delta);

    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        parent.resize(client,width,height);
    }

    @Override
    public void close() {
        super.close();
        commandField.setFocused(false);
        commandField.setChangedListener(null);
        commandMacroCommandSuggestor.setWindowActive(false);
        commandMacroCommandSuggestor.refresh();
        commandMacroCommandSuggestor.close();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!commandField.mouseClicked(mouseX, mouseY, button)) {
            if (!commandMacroCommandSuggestor.mouseClicked(mouseX, mouseY, button)) {
                close();
            } else {
                commandField.setFocused(true);
            }
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return commandMacroCommandSuggestor.mouseScrolled(verticalAmount);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return commandField.charTyped(chr,modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputUtil.GLFW_KEY_ESCAPE || keyCode == InputUtil.GLFW_KEY_ENTER || keyCode == InputUtil.GLFW_KEY_KP_ENTER) {
            close();
            return true;
        }
        commandMacroCommandSuggestor.keyPressed(keyCode, scanCode, modifiers);

        return commandField.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return commandField.keyReleased(keyCode, scanCode, modifiers);
    }
}
