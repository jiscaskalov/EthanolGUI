package jsco.dev.ethanolgui.render.elements;

import jsco.dev.ethanolgui.render.EthanolRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class CategoryListElement extends HudElement {
    public Color color1;
    private final int shadowWidth1, rad;

    private final CategoryElement auth, player, server, world, misc, config;
    public CategoryElement selectedCategory;

    protected CategoryListElement(int x, int y, int width, int height, int rad, Color color1, int shadowWidth1) {
        super(x, y, x+width, y+height);
        this.color1 = color1;
        this.shadowWidth1 = shadowWidth1;
        this.rad = rad;

        auth = new CategoryElement(0, 0, width - 10, 40, "Auth", rad, color1, shadowWidth1);
        player = new CategoryElement(0, 0, width - 10, 40, "Player", rad, color1, shadowWidth1);
        server = new CategoryElement(0, 0, width - 10, 40, "Server", rad, color1, shadowWidth1);
        world = new CategoryElement(0, 0, width - 10, 40, "World", rad, color1, shadowWidth1);
        misc = new CategoryElement(0, 0, width - 10, 40, "Misc", rad, color1, shadowWidth1);
        config = new CategoryElement(0, 0, width - 10, 40, "Config", rad, color1, shadowWidth1);
        addChild(auth);
        addChild(player);
        addChild(server);
        addChild(world);
        addChild(misc);
//        addChild(config);
    }

    @Override
    protected void onRender(DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        color1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 130);
        EthanolRenderer.renderRoundedShadowedQuad(matrices, color1, color1, x, y, width, height, rad, 30, shadowWidth1);
        EthanolRenderer.renderRoundedShadow(matrices, Color.WHITE.darker(), x, y, width, height, rad, 30, 2);

        auth.setX(x+5);
        auth.setY(y+5);
        player.setX(x+5);
        player.setY(y+50);
        server.setX(x+5);
        server.setY(y+95);
        world.setX(x+5);
        world.setY(y+140);
        misc.setX(x+5);
        misc.setY(y+185);
        config.setX(x+5);
        config.setY(y+230);
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
