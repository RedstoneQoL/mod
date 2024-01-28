package tools.redstone.redstonetools.macros.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButtonWidget extends ButtonWidget {

    public static Identifier CROSS_ICON = new Identifier("redstonetools","gui/cross.png");
    public static Identifier PENCIL_ICON = new Identifier("redstonetools","gui/pencil.png");

    private final Identifier texture;
    public IconButtonWidget(Identifier texture ,int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
        this.texture = texture;
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableDepthTest();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        context.drawTexture(this.texture,this.getX(), this.getY(), 0,0, 20, this.height, 20, 20);
    }

}
