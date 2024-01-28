package tools.redstone.redstonetools.macros.gui.widget.commandlist;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import tools.redstone.redstonetools.macros.gui.widget.commandlist.CommandListWidget.CommandEntry;

public class CommandEntryPlaceHolder extends CommandEntry {
    public CommandEntryPlaceHolder(MinecraftClient client, CommandListWidget owner, String text) {
        super(client, owner, text);
        super.deleteButton.visible = false;
    }

    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        command.setSuggestion("Add new command");
        if (!super.command.getText().isEmpty()) {
            super.owner.addCommandFromPlaceHolder(command.getText(),this);
            command.setText("");
        }

        super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
    }

}
