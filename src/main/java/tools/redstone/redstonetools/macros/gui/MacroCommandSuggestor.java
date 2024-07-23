package tools.redstone.redstonetools.macros.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.HashMap;

public class MacroCommandSuggestor extends ChatInputSuggestor {
    private static final HashMap<MacroCommandSuggestor,Integer> yMap =  new HashMap<>();

    public MacroCommandSuggestor(MinecraftClient client, Screen owner, TextFieldWidget textField, TextRenderer textRenderer, boolean slashOptional, boolean suggestingWhenEmpty, int y, int maxSuggestionSize, int color) {
        super(client, owner, textField, textRenderer, slashOptional, suggestingWhenEmpty, 0, maxSuggestionSize, false, color);
        yMap.put(this,y);

    }

    public void close(){
        yMap.remove(this);
    }

    public static boolean instance(Object object) {
        return object instanceof MacroCommandSuggestor;
    }

    public static int getY(Object object){
        return yMap.get(object);
    }

    @Override
    public void refresh() {
        if (MinecraftClient.getInstance().player == null) return;
        super.refresh();

    }


}
