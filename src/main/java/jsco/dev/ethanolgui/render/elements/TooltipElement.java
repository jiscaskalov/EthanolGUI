package jsco.dev.ethanolgui.render.elements;

import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.render.Animations;
import jsco.dev.ethanolgui.render.EthanolRenderer;
import jsco.dev.ethanolgui.render.RenderText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class TooltipElement extends HudElement implements TickableElement {
    public Color color1;
    private final int shadowWidth1, rad;
    private final String text;
    private double animProgress = 0;

    public TooltipElement(int x, int y, int width, int height, int rad, Color color1, String text, int shadowWidth1) {
        super(x,y,x+width,y+height);
        this.color1 = color1;
        this.shadowWidth1 = shadowWidth1;
        this.rad = rad;
        this.text = text;
    }

    @Override
    protected void onRender(DrawContext context) {
        String[] lines = text.split("\n");
        float animProgress = (float) Animations.easeOut(this.animProgress);
        float mX = (float) EthanolGUI.CLIENT.mouse.getX()+5;
        float mY = (float) EthanolGUI.CLIENT.mouse.getY()+5;
//        int width = (int) (mX+RenderText.getSmallRenderer().getStringWidth(text)*2)+15;
        int width = (int) (mX+100);
        int adjH = height+(10+text.split("\n").length);
        int tAdjH = 0;
//        int adjH = height+((RenderText.numLinesWrappedText(text, RenderText.getSmallRenderer(), width, 0.7F)-2)*10);
        Color color = Animations.colorFade(animProgress, new Color(0, 0, 0, 0), color1);
        Color color2 = Animations.colorFade(animProgress, new Color(0, 0, 0, 0), Color.WHITE);
        EthanolRenderer.renderRoundedShadowedQuad(context.getMatrices(), color, color, mX, mY, width, adjH, rad, 30, shadowWidth1);
        EthanolRenderer.renderRoundedShadow(context.getMatrices(), color2, mX, mY, width, adjH, rad, 30, 2);
        for (String line : lines) {
            tAdjH=tAdjH+10;
            RenderText.getSmallRenderer().drawString(context.getMatrices(), line, (mX / 2) + 5, (mY / 2) + tAdjH, 1, 1, 1, 1);
        }
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

    @Override
    public void onTick() {
        double delta = 0.05;
        if (!rendering) {
            delta *= -1;
        }
        animProgress = MathHelper.clamp(animProgress + delta, 0, 1);
    }
}
