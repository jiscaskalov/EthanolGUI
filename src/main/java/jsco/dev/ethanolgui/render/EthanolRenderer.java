package jsco.dev.ethanolgui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import jsco.dev.ethanolgui.mixin.Renderer2dAccessor;
import me.x150.renderer.render.Renderer2d;
import me.x150.renderer.util.AlphaOverride;
import me.x150.renderer.util.RendererUtils;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.awt.*;

import static jsco.dev.ethanolgui.client.EthanolGUI.CLIENT;

public class EthanolRenderer {
    public static VertexSorter VERTEX_SORTER;

    public static void renderRoundedShadowedQuad(MatrixStack matrices, Color quadColor, Color shadowColor, double fromX, double fromY, double toX, double toY, float rad, float samples, double shadowWidth) {
        Renderer2d.renderRoundedQuad(matrices, quadColor, fromX, fromY, toX, toY, rad, samples);
        renderRoundedShadow(matrices, shadowColor, fromX, fromY, toX, toY, rad, samples, shadowWidth);
    }

    public static void renderGradientShadowedRoundedQuad(MatrixStack matrices, Color colorStart, Color colorEnd, Color shadowColor, double fromX, double fromY, double toX, double toY, float rad, float samples, double shadowWidth) {
        renderGradientRoundedQuad(matrices, colorStart, colorEnd, fromX, fromY, toX, toY, rad, rad, rad, rad, samples);
        renderRoundedShadow(matrices, shadowColor, fromX, fromY, toX, toY, rad, samples, shadowWidth);
    }

    public static void renderGradientRoundedQuad(MatrixStack matrices, Color colorStart, Color colorEnd,
                                                 double fromX, double fromY, double toX, double toY,
                                                 float radTL, float radTR, float radBL, float radBR, float samples) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float crStart = (float) colorStart.getRed() / 255.0F;
        float cgStart = (float) colorStart.getGreen() / 255.0F;
        float cbStart = (float) colorStart.getBlue() / 255.0F;
        float caStart = (float) colorStart.getAlpha() / 255.0F;

        float crEnd = (float) colorEnd.getRed() / 255.0F;
        float cgEnd = (float) colorEnd.getGreen() / 255.0F;
        float cbEnd = (float) colorEnd.getBlue() / 255.0F;
        float caEnd = (float) colorEnd.getAlpha() / 255.0F;

        RendererUtils.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        renderGradientRoundedQuadInternal(matrix, crStart, cgStart, cbStart, caStart, crEnd, cgEnd, cbEnd, caEnd,
                (float) fromX, (float) fromY, (float) toX, (float) toY,
                radTL, radTR, radBL, radBR, samples);

        RendererUtils.endRender();
    }

    private static void renderGradientRoundedQuadInternal(Matrix4f matrix, float crStart, float cgStart, float cbStart, float caStart,
                                                          float crEnd, float cgEnd, float cbEnd, float caEnd,
                                                          float fromX, float fromY, float toX, float toY,
                                                          float radC1, float radC2, float radC3, float radC4, float samples) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        Renderer2dAccessor.invoke$populateRC(toX - radC4, toY - radC4, radC4, 0);
        Renderer2dAccessor.invoke$populateRC(toX - radC2, fromY + radC2, radC2, 1);
        Renderer2dAccessor.invoke$populateRC(fromX + radC1, fromY + radC1, radC1, 2);
        Renderer2dAccessor.invoke$populateRC(fromX + radC3, toY - radC3, radC3, 3);

        float deltaR = (crEnd - crStart) / samples;
        float deltaG = (cgEnd - cgStart) / samples;
        float deltaB = (cbEnd - cbStart) / samples;
        float deltaA = (caEnd - caStart) / samples;

        for (int i = 0; i < samples; i++) {
            float currentR = crStart + deltaR * i;
            float currentG = cgStart + deltaG * i;
            float currentB = cbStart + deltaB * i;
            float currentA = caStart + deltaA * i;

            for (int j = 0; j < 4; j++) {
                float[] current = Renderer2dAccessor.getRoundedCache()[j];
                float rad = current[2];
                for (float r = j * 90f; r <= (j + 1) * 90f; r += 90f / samples) {
                    float rad1 = (float) Math.toRadians(r);
                    float sin = (float) Math.sin(rad1) * rad;
                    float cos = (float) Math.cos(rad1) * rad;

                    bufferBuilder.vertex(matrix, current[0] + sin, current[1] + cos, 0.0F)
                            .color(currentR, currentG, currentB, currentA);
                }
            }
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public static void renderRoundedShadow(MatrixStack matrices, Color innerColor, double fromX, double fromY, double toX, double toY, float rad, float samples, double shadowWidth) {
        int color = innerColor.getRGB();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float f = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        RendererUtils.setupRender();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        renderRoundedShadowInternal(matrix, g, h, k, transformColor(f), fromX, fromY, toX, toY, rad, samples, shadowWidth);
        RendererUtils.endRender();
    }

    private static void renderRoundedShadowInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, float rad, float samples,
                                                    double wid) {
        unscaledProjection();

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        double toX1 = toX - rad;
        double toY1 = toY - rad;
        double fromX1 = fromX + rad;
        double fromY1 = fromY + rad;
        double[][] map = new double[][] { new double[] { toX1, toY1 }, new double[] { toX1, fromY1 }, new double[] { fromX1, fromY1 },
                new double[] { fromX1, toY1 } };
        for (int i = 0; i < map.length; i++) {
            double[] current = map[i];
            for (double r = i * 90d; r < 360 / 4d + i * 90d; r += 90 / samples) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca);
                float sin1 = (float) (sin + Math.sin(rad1) * wid);
                float cos1 = (float) (cos + Math.cos(rad1) * wid);
                bufferBuilder.vertex(matrix, (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(cr, cg, cb, 0f);
            }
        }
        double[] current = map[0];
        float rad1 = (float) Math.toRadians(0);
        float sin = (float) (Math.sin(rad1) * rad);
        float cos = (float) (Math.cos(rad1) * rad);
        bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca);
        float sin1 = (float) (sin + Math.sin(rad1) * wid);
        float cos1 = (float) (cos + Math.cos(rad1) * wid);
        bufferBuilder.vertex(matrix, (float) current[0] + sin1, (float) current[1] + cos1, 0.0F).color(cr, cg, cb, 0f);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        scaledProjection();
    }

    public static float transformColor(float f) {
        return AlphaOverride.compute((int) (f * 255)) / 255f;
    }

    public static void unscaledProjection() {
        VERTEX_SORTER = RenderSystem.getVertexSorting();
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, CLIENT.getWindow().getFramebufferWidth(), CLIENT.getWindow().getFramebufferHeight(), 0, 1000, 21000), VertexSorter.BY_Z);
    }

    public static void scaledProjection() {
        RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0, (float) (CLIENT.getWindow().getFramebufferWidth() / CLIENT.getWindow().getScaleFactor()), (float) (CLIENT.getWindow().getFramebufferHeight() / CLIENT.getWindow().getScaleFactor()), 0, 1000, 21000), VERTEX_SORTER);
    }
}
