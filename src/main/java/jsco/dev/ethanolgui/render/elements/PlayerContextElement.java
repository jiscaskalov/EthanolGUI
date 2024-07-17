package jsco.dev.ethanolgui.render.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import jsco.dev.ethanolgui.client.EthanolGUI;
import jsco.dev.ethanolgui.render.EthanolRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

public class PlayerContextElement extends HudElement {
    public Color color1;
    private final int shadowWidth1, rad;

    private static final Vector3f FLAT_LIT_VEC1 = (new Vector3f(0.2F, 0.5F, -0.7F)).normalize();
    private static final Vector3f FLAT_LIT_VEC2 = (new Vector3f(-0.2F, 0.5F, 0.7F)).normalize();

    public PlayerContextElement(int x, int y, int width, int height, int rad, Color color1, int shadowWidth1) {
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
        renderPlayer(matrices);
    }
    
    protected void renderPlayer(MatrixStack matrices) {
        ClientPlayerEntity player = EthanolGUI.CLIENT.player;
        if (player == null)
            return;

        matrices.push();

        int renderPosY = 60;
        if (player.isFallFlying())
            renderPosY = 60 - MathHelper.ceil(20 * 2 * (90 + player.getPitch()) / 180);
        else if (player.isSwimming()) {
            renderPosY = 60 - 20;
        }

        int posX = 30;
        int safeArea = 30;
        matrices.translate(posX + safeArea, renderPosY + safeArea, 0);
        matrices.scale((float) 20, (float) 20, -(float) 20);
        Quaternionf quaternion = new Quaternionf().rotateZ((float)Math.PI);
        matrices.multiply(quaternion);

        float bodyYaw = player.bodyYaw;
        float yaw = player.getYaw();
        float headYaw = player.headYaw;

        float angle = 145;
        if (player.isFallFlying() || player.isBlocking()) {
            player.headYaw = angle;
        } else {
            player.setYaw(headYaw - bodyYaw + angle);
            player.headYaw = player.getYaw();
        }
        player.bodyYaw = angle;

        RenderSystem.setupGuiFlatDiffuseLighting(FLAT_LIT_VEC1, FLAT_LIT_VEC2);

        EntityRenderDispatcher entityRenderDispatcher = EthanolGUI.CLIENT.getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = EthanolGUI.CLIENT.getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(player, 0, 0, 0, 0.0F, 1.0F, matrices, immediate, 0xF000F0));
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);

        player.bodyYaw = bodyYaw;
        player.setYaw(yaw);
        player.headYaw = headYaw;

        matrices.pop();

        DiffuseLighting.enableGuiDepthLighting();
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
