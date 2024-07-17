package jsco.dev.ethanolgui.render.elements;

import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.render.Animations;
import jsco.dev.ethanolgui.render.EthanolRenderer;
import jsco.dev.ethanolgui.render.RenderText;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class ActionElement extends HudElement implements TickableElement {
    public Color color1;
    private final int shadowWidth1, rad;
    @Getter @Setter private double animProgress = 0;
    private final String title, desc;

    private TooltipElement tooltipElement;

    public ActionElement(int width, int height, int rad, String title, String desc, Color color1, int shadowWidth1) {
        super(0,0,width,height);
        this.color1 = color1;
        this.shadowWidth1 = shadowWidth1;
        this.rad = rad;
        this.title = title;
        this.desc = desc;

        tooltipElement = new TooltipElement(0, 0, width, height, rad, color1.brighter(), desc, shadowWidth1);
        addChild(tooltipElement);
        tooltipElement.setRendering(false);
    }

    @Override
    protected void onRender(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        float animProgress = (float) Animations.easeOut(this.animProgress);
        Color color2 = Animations.colorFade(animProgress, Color.WHITE.darker(), Color.WHITE);
        Color color = Animations.colorFade(animProgress, color1, color1.brighter());
        EthanolRenderer.renderRoundedShadowedQuad(matrices, color, color, x, y, width, height, rad, 30, shadowWidth1);
        EthanolRenderer.renderRoundedShadow(matrices, color2, x, y, width, height, rad, 30, 2);
        RenderText.getLargeRenderer().drawString(matrices, title, (float) (x/2)+3, (float) (y/2)+2, 1, 1, 1, 1);

        if (isHovered(EthanolGUI.CLIENT.mouse.getX(), EthanolGUI.CLIENT.mouse.getY())) {
            tooltipElement.setRendering(true);
            tooltipElement.setX((int) EthanolGUI.CLIENT.mouse.getX());
            tooltipElement.setY((int) EthanolGUI.CLIENT.mouse.getY());
        } else tooltipElement.setRendering(false);
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {
        if (isHovered(EthanolGUI.CLIENT.mouse.getX(), EthanolGUI.CLIENT.mouse.getY())) {
            EthanolGUI.CLIENT.player.sendMessage(Text.of("clicky"));
        }
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
