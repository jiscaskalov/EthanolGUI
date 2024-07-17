package jsco.dev.ethanolgui.render.elements;

import jsco.dev.ethanolgui.render.EthanolRenderer;
import jsco.dev.ethanolgui.render.RenderText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class TitleBarElement extends HudElement {
    public Color color1;
    private final int shadowWidth1, rad;

    public TitleBarElement(int x, int y, int width, int height, int rad, Color color1, int shadowWidth1) {
        super(x,y,x+width,y+height);
        this.color1 = color1;
        this.shadowWidth1 = shadowWidth1;
        this.rad = rad;
    }

    @Override
    protected void onRender(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        color1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 130);
        EthanolRenderer.renderRoundedShadowedQuad(matrices, color1, color1, x, y, width, height, rad, 30, shadowWidth1);
        EthanolRenderer.renderRoundedShadow(matrices, Color.WHITE.darker(), x, y, width, height, rad, 30, 2);
        RenderText.getSmallRenderer().drawString(matrices, "Ethanol Menu", (float) (x /2)+5, (float) y /2, 1, 1, 1, 1);
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {

    }

    @Override
    public void onRelease(double mouseX, double mouseY, int button) {

    }

    @Override
    public void onScroll(double mouseX, double mouseY, double delta) {

    }
}
