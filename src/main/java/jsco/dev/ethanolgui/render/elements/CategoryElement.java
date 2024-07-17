package jsco.dev.ethanolgui.render.elements;

import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.render.Animations;
import jsco.dev.ethanolgui.render.EthanolRenderer;
import jsco.dev.ethanolgui.render.RenderText;
import jsco.dev.ethanolgui.render.blur.RainbowColor;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class CategoryElement extends HudElement implements TickableElement {
    @Getter private final String title;
    public Color color1, startColor, endColor;
    private final int shadowWidth1, rad;
    private double animProgress = 0;

    public CategoryElement(int x, int y, int width, int height, String title, int rad, Color color1, int shadowWidth1) {
        super(x, y, width, height);
        this.title = title;
        this.color1 = color1;
        this.shadowWidth1 = shadowWidth1;
        this.rad = rad;
    }

    @Override
    protected void onRender(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        float animProgress = (float) Animations.easeOut(this.animProgress);
        Color color = Animations.colorFade(animProgress, color1, color1.brighter());
        Color color2 = Animations.colorFade(animProgress, Color.WHITE.darker(), Color.WHITE);
        if (getParent() instanceof CategoryListElement list && list.selectedCategory != this)
            EthanolRenderer.renderRoundedShadowedQuad(
                    matrices,
                    color,
                    color,
                    x, y, width, height,
                    rad,
                    30,
                    shadowWidth1
            );
        else {
//            Color hsbStartColor = Color.getHSBColor(RainbowColor.hue, 1, 1);
//            Color hsbEndColor = Color.getHSBColor(RainbowColor.hue2, 1, 1);
            int alpha = Math.clamp(EthanolGUI.ETHANOL_HUD.getAlpha()*3L, 0, 255);
            Color rColor = new Color(RainbowColor.average().getRed(), RainbowColor.average().getGreen(), RainbowColor.average().getBlue(), alpha).brighter();
//            startColor = new Color(hsbStartColor.getRed(), hsbStartColor.getGreen(), hsbStartColor.getBlue(), alpha);
//            endColor = new Color(hsbEndColor.getRed(), hsbEndColor.getGreen(), hsbEndColor.getBlue(), alpha);
//            EthanolRenderer.renderGradientShadowedRoundedQuad(
//                    matrices,
//                    startColor,
//                    endColor,
//                    color1,
//                    x, y, width, height,
//                    rad,
//                    30,
//                    shadowWidth1
//            );
            EthanolRenderer.renderRoundedShadowedQuad(
                    matrices,
                    rColor,
                    color1,
                    x, y, width, height,
                    rad,
                    30, shadowWidth1
            );
        }
        EthanolRenderer.renderRoundedShadow(matrices, color2, x, y, width, height, rad, 30, 2);
        RenderText.getLargeRenderer().drawString(matrices, title, (float) (x/2)+3, (float) (y/2)+2, 1, 1, 1, 1);
    }

    @Override
    protected void onClick(double mouseX, double mouseY, int button) {
        if (this.getParent() instanceof CategoryListElement list && isHovered(EthanolGUI.CLIENT.mouse.getX(), EthanolGUI.CLIENT.mouse.getY())) {
            if (this.getParent().getParent() instanceof BaseElement base && list.selectedCategory != this) {
                for (HudElement child : base.getChildren()) {
                    if (child instanceof ActionListElement aList) {
                        aList.clearChildren();
                    }
                }
            }
            EthanolGUI.CLIENT.player.sendMessage(Text.of("Clicked: '%s'".formatted(title)));
            list.selectedCategory = this;
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
