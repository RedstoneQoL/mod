package tools.redstone.redstonetools.mixin.macros;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tools.redstone.redstonetools.macros.gui.screen.MacroSelectScreen;

@Mixin(ControlsOptionsScreen.class)
public class AddMacroButtonMixin extends GameOptionsScreen {
    public AddMacroButtonMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        ButtonWidget buttonWidget = ButtonWidget.builder(Text.of("Macros..."), (button) -> {
            this.client.setScreen(new MacroSelectScreen(this,super.gameOptions,Text.of("Macros")));
        }).dimensions(this.width / 2 - 155, this.height / 6 + 36+24, 150, 20).build();
        this.addDrawableChild(buttonWidget);

        moveDoneButton();
    }


    //TODO refactor this into mixin instead
    @Unique
    private void moveDoneButton() {
        for (Element e : this.children()) {
            if (!(e instanceof ButtonWidget button)) continue;
            if (((ButtonWidget) e).getMessage() == ScreenTexts.DONE) {
                button.setY(button.getY()+24);
            }
        }
    }
}
