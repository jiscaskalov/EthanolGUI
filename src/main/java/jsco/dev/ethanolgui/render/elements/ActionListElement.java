package jsco.dev.ethanolgui.render.elements;

import jsco.dev.ethanolgui.client.action.auth.AuthActionsList;
import jsco.dev.ethanolgui.render.EthanolRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionListElement extends HudElement {
    public Color color1;
    private final int shadowWidth1, rad;
    public List<ActionElement> actions= new ArrayList<>();

    protected ActionListElement(int x, int y, int width, int height, int rad, Color color1, int shadowWidth1) {
        super(x, y, width, height);
        this.color1 = color1;
        this.shadowWidth1 = shadowWidth1;
        this.rad = rad;

        AuthActionsList.fillList(width - 10, 40, color1);
    }

    @Override
    protected void onRender(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        color1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 20);
        EthanolRenderer.renderRoundedShadowedQuad(matrices, color1, color1, x, y, width, height, rad, 30, shadowWidth1);
        EthanolRenderer.renderRoundedShadow(matrices, Color.WHITE.darker(), x, y, width, height, rad, 30, 2);

        if (getParent() instanceof BaseElement base && base.getCategoryListElement().selectedCategory != null) {
            String categoryTitle = base.getCategoryListElement().selectedCategory.getTitle();
            Map<String, List<ActionElement>> categoryActions = Map.of(
                    "Auth", AuthActionsList.getList()
            );

            actions = categoryActions.get(categoryTitle);
            if (actions != null) {
                for (int i = 0; i < actions.size(); i++) {
                    ActionElement action = actions.get(i);
                    if (!getChildren().contains(action)) addChild(action);
                    action.setX(x + 5);
                    action.setY(y + 5 + (40 * i));
                }
            }
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
}
