package tools.redstone.redstonetools.macros.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import tools.redstone.redstonetools.macros.Macro;
import tools.redstone.redstonetools.macros.gui.widget.macrolist.MacroEntry;
import tools.redstone.redstonetools.macros.gui.widget.macrolist.MacroListWidget;

public class MacroSelectScreen extends GameOptionsScreen {
    private static final Text TITLE_TEXT = Text.of("Macros");
    private MacroListWidget macroList;
    private ButtonWidget createNewButton;

    public MacroSelectScreen(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, TITLE_TEXT);
    }

    @Override
    protected void initBody() {
        this.macroList = this.layout.addBody(new MacroListWidget(this, this.client));
    }


    @Override
    protected void addOptions() {
    }

    @Override
    protected void initFooter() {
        this.createNewButton = ButtonWidget.builder(Text.of("Create New..."), button -> {
            this.client.setScreen(new MacroEditScreen(this,gameOptions, Text.of("New Macro"), macroList));
        }).build();
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).build());
        directionalLayoutWidget.add(this.createNewButton);
    }

    @Override
    protected void initTabNavigation() {
        this.layout.refreshPositions();
        this.macroList.position(this.width, this.layout);
    }

    public void openEditScreen(MacroEntry entry) {
        client.setScreen(new MacroEditScreen(this,gameOptions,Text.of("Edit Macro"), macroList, entry.macro));
    }
}
