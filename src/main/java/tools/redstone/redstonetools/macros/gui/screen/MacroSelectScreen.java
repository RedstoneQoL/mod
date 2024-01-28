package tools.redstone.redstonetools.macros.gui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import tools.redstone.redstonetools.macros.gui.widget.macrolist.MacroEntry;
import tools.redstone.redstonetools.macros.gui.widget.macrolist.MacroListWidget;

public class MacroSelectScreen extends GameOptionsScreen {


    private MacroListWidget macroList;

    public MacroSelectScreen(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Override
    public void init() {
        super.init();

        this.macroList = new MacroListWidget(this,client);
        this.addSelectableChild(this.macroList);

        ButtonWidget buttonWidget = ButtonWidget.builder(Text.of("Create New..."), (button) -> {
            this.client.setScreen(new MacroEditScreen(this,gameOptions,Text.of("New Macro"), macroList));
        }).dimensions(this.width / 2 +1, this.height - 29, 150, 20).build();
        this.addDrawableChild(buttonWidget);

        buttonWidget = ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            this.client.setScreen(this.parent);
        }).dimensions(this.width / 2 - 151, this.height - 29, 150, 20).build();

        this.addDrawableChild(buttonWidget);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        macroList.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
        super.render(context, mouseX, mouseY, delta);
    }

    public void openEditScreen(MacroEntry entry) {
        client.setScreen(new MacroEditScreen(this,gameOptions,Text.of("Edit Macro"), macroList, entry.macro));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }
}
