package jsco.dev.ethanolgui.render.elements;

import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.render.Animations;
import jsco.dev.ethanolgui.render.EthanolRenderer;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class BaseElement extends HudElement implements TickableElement {
    public Color color1, color2;
    private final int shadowWidth1, shadowWidth2, rad;
    private double animProgress = 0;
    private final TitleBarElement titleBar;
    @Getter private final CategoryListElement categoryListElement;
    private final ActionListElement actionListElement;

    public BaseElement(int x, int y, int width, int height, int rad, Color color1, Color color2, int shadowWidth1, int shadowWidth2) {
        super(x,y,x+width,y+height);
        super.setCanDrag(true);
        this.color1 = color1;
        this.color2 = color2;
        this.shadowWidth1 = shadowWidth1;
        this.shadowWidth2 = shadowWidth2;
        this.rad = rad;

        titleBar = new TitleBarElement(x+5, 0, width - 15, 23, 4, color1.brighter().brighter().brighter(), 2);
        categoryListElement = new CategoryListElement(0, 0, width/2, (height*2)-90, 4, color1.brighter().brighter().brighter(), 2);
        actionListElement = new ActionListElement(0, 0, width + 315, (height*2)-90, 4, color1.brighter().brighter().brighter(), 2);
        addChild(titleBar);
        addChild(categoryListElement);
        addChild(actionListElement);
    }

    @Override
    protected void onRender(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        float animProgress = (float) Animations.easeOut(this.animProgress);
        color2 = Animations.colorFade(animProgress, Color.WHITE.darker(), Color.WHITE);
        color1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 120);
        EthanolRenderer.renderRoundedShadowedQuad(matrices, color1, color1, x, y, width, height, rad, 30, shadowWidth1);
        EthanolRenderer.renderRoundedShadow(matrices, color2, x, y, width, height, rad, 30, shadowWidth2);
        titleBar.setY(y+5);
        categoryListElement.setY(y+35);
        categoryListElement.setX(x+5);
        actionListElement.setY(y+35);
        actionListElement.setX(x+237);
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
        if (!isHovered(EthanolGUI.CLIENT.mouse.getX(), EthanolGUI.CLIENT.mouse.getY())) {
            delta *= -1;
        }
        animProgress = MathHelper.clamp(animProgress + delta, 0, 1);
    }
}