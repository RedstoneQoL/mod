package tools.redstone.redstonetools.macros.gui.widget.macrolist;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import tools.redstone.redstonetools.macros.Macro;
import tools.redstone.redstonetools.macros.MacroManager;
import tools.redstone.redstonetools.macros.gui.screen.MacroSelectScreen;

import static tools.redstone.redstonetools.RedstoneToolsClient.INJECTOR;

public class MacroListWidget extends AlwaysSelectedEntryListWidget<MacroEntry> {

    protected static final MacroManager macroManager = INJECTOR.getInstance(MacroManager.class);

    protected final MacroSelectScreen parent;
    protected final MinecraftClient client;


    public MacroListWidget(MacroSelectScreen parent, MinecraftClient client) {
        super(client, parent.width, parent.height, 20, 20);
        setHeight(parent.height - 62);

        this.parent = parent;
        this.client = client;

        for (Macro macro : macroManager.getMacros()) {
            addEntry(new MacroEntry(macro,this));
        }


        if (this.getSelectedOrNull() != null) {
            this.centerScrollOn(this.getEntry(0));
        }
    }

    public void addMacro(Macro macro) {
        macroManager.addMacro(macro);
        addEntry(new MacroEntry(macro, this));
    }

    public boolean canAdd(Macro macro) {
        Macro macroFromName = macroManager.getMacro(macro.name);

        return macroFromName == null || macro.isCopyOf(macroFromName);
    }

    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 20;
    }



    public boolean isFocused() {
        return parent.getFocused() == this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MacroEntry entry = super.getEntryAtPosition(width/2,mouseY);
        if (entry != null) entry.mouseClickedInRow(mouseX,mouseY,button);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public int getRowWidth() {
        return 120;
    }

    @Override
    public boolean removeEntry(MacroEntry entry){
        return super.removeEntry(entry);
    }

}
