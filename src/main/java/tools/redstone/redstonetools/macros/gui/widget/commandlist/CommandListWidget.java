package tools.redstone.redstonetools.macros.gui.widget.commandlist;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import tools.redstone.redstonetools.macros.gui.MacroCommandSuggestor;
import tools.redstone.redstonetools.macros.gui.screen.MacroEditScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import tools.redstone.redstonetools.macros.gui.widget.IconButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class CommandListWidget extends EntryListWidget<CommandListWidget.CommandEntry> {

    private final MacroEditScreen parent;

    public CommandListWidget(MinecraftClient client, MacroEditScreen parent, int width, int height, int y, int itemHeight) {
        super(client, width, height, y, itemHeight);
        this.parent = parent;
        addEntry(new CommandEntryPlaceHolder(client,this,""));
    }



    @Override
    public void setSelected(@Nullable CommandListWidget.CommandEntry entry) {
    }

    public CommandEntry addCommand(String command) {
        CommandEntry entry = new CommandEntry(client, this, command);
        List<CommandEntry> entries = children();

        entries.add(entries.size()-1,entry);

        return entry;
    }

    protected void addCommandFromPlaceHolder(String command,CommandEntryPlaceHolder placeHolder) {
        CommandEntry entry = addCommand(command);
        placeHolder.setFocused(false);

        entry.command.setX(placeHolder.command.getX());
        entry.command.setY(placeHolder.command.getY());
        entry.setFocused(true);
    }

    public void centerScrollOn(CommandEntry entry) {
        super.centerScrollOn(entry);
    }



    protected void removeCommand(CommandEntry command) {
        removeEntry(command);
        setScrollAmount(getScrollAmount());
    }

    protected int getScrollbarPositionX() {
        return parent.width/2+this.width/2-4;
    }

    public List<String> getCommandList() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < children().size(); i++){
            CommandEntry command = getEntry(i);
            if (command instanceof CommandEntryPlaceHolder) continue;

            list.add(command.getText());
        }

        return list;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (CommandEntry entry : children()) {
            entry.charTyped(chr,modifiers);
        }

        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (CommandEntry entry : children()) {
            entry.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (CommandEntry entry : children()) {
            entry.keyReleased(keyCode, scanCode, modifiers);
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        CommandEntry clickedEntry = getEntryAtPosition(mouseX,mouseY);
        for (CommandEntry entry : children()) {
            if (entry != clickedEntry) entry.setFocused(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected MacroEditScreen getParent() {
        return parent;
    }

    @Override
    public int getRowWidth() {
        return width;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

     public static class CommandEntry extends EntryListWidget.Entry<CommandEntry> {

        protected CommandListWidget owner;

        public final TextFieldWidget command;
        protected final ButtonWidget deleteButton;


        public CommandEntry(MinecraftClient client, CommandListWidget owner, String text) {
            this.owner = owner;

            command = new TextFieldWidget(client.textRenderer, 0, 0, 300, 20, Text.of(""));
            command.setMaxLength(255);
            command.setText(text);

            deleteButton = new IconButtonWidget(IconButtonWidget.CROSS_ICON,0,0,20,20,Text.empty(),(button) -> {
                this.owner.removeCommand(this);
            },null);

            MacroCommandSuggestor commandMacroCommandSuggestor = new MacroCommandSuggestor(client, owner.getParent(), command,client.textRenderer,true,false, 0,0,0);
            commandMacroCommandSuggestor.setWindowActive(false);
            commandMacroCommandSuggestor.refresh();
            commandMacroCommandSuggestor.close();
        }


        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            command.setX(owner.getParent().width / 2 - owner.getWidth() / 2 + 5);
            command.setY(y);
            command.render(context,mouseX,mouseY,tickDelta);


            deleteButton.setX(command.getX() + command.getWidth() + 5);
            deleteButton.setY(y);
            deleteButton.render(context,mouseX,mouseY,tickDelta);

            if (edit) {
                edit = false;
                owner.getParent().editCommandField(command);
            }
        }

        private boolean edit = false;

        public void setFocused(boolean focused){
            command.setFocused(focused);
            if (focused){
                owner.centerScrollOn(this);
                edit = true;
            }
        }

        protected String getText() {
            return command.getText();
        }

        public void setOwner(CommandListWidget owner) {
            this.owner = owner;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (command.mouseClicked(mouseX,mouseY,button)) {
                owner.centerScrollOn(this);
                edit = true;
                return true;
            }
            deleteButton.mouseClicked(mouseX,mouseY,button);

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            if (command.isFocused()) return command.charTyped(chr,modifiers);

            return super.charTyped(chr, modifiers);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (command.isFocused()) {
                return command.keyPressed(keyCode, scanCode, modifiers);
            }

            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            if (command.isFocused()) return command.keyReleased(keyCode, scanCode, modifiers);

            return super.keyReleased(keyCode, scanCode, modifiers);
        }
    }
}
