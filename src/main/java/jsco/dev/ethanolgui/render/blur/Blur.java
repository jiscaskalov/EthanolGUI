package jsco.dev.ethanolgui.render.blur;

import lombok.Setter;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.function.Function;

import static java.lang.Math.*;

public class Blur {

    public static long start;
    public static float progress;

    public static boolean prevScreenHasBlur;
    public static boolean screenHasBlur = true;

    public static boolean doFade = false;

    // Config
    public static int fadeOutTimeMillis = 300;
    public static int fadeTimeMillis = 300;
    @Setter public static int gradientAlpha = 0;
    public static Easing animationCurve = Easing.FLAT;

    public static void start() {
        if (screenHasBlur) {
            if (doFade) {
                start = System.currentTimeMillis();
                doFade = false;
            }
        } else if (prevScreenHasBlur && fadeOutTimeMillis > 0) {
            start = System.currentTimeMillis();
            doFade = true;
        } else {
            start = -1;
            doFade = true;
        }
    }

    public static void updateProgress(boolean fadeIn) {
        double x;
        if (fadeIn) {
            x = Math.min((System.currentTimeMillis() - start) / (double) fadeTimeMillis, 1);
        }
        else {
            x = Math.max(1 + (start - System.currentTimeMillis()) / (double) fadeOutTimeMillis, 0);
            if (x <= 0) {
                start = -1;
            }
        }
        x = animationCurve.apply(x, fadeIn);
        x = Math.clamp(0, 1, x);

        Blur.progress = Double.valueOf(x).floatValue();
    }

    public static int getBackgroundColor(boolean second) {
        int a = gradientAlpha;
        var col = second ? Color.getHSBColor(RainbowColor.hue, 1, 1) : Color.getHSBColor(RainbowColor.hue2, 1, 1);
        int r = (col.getRGB() >> 16) & 0xFF;
        int b = (col.getRGB() >> 8) & 0xFF;
        int g = col.getRGB() & 0xFF;
        float prog = progress;
        a *= prog;
        r *= prog;
        g *= prog;
        b *= prog;
        return a << 24 | r << 16 | b << 8 | g;
    }
    public static double getRotation() {
        return RainbowColor.rotation;
    }
    public static void renderRotatedGradient(DrawContext context, int width, int height) {
        float diagonal = (float) sqrt((float) width*width + height*height);
        int smallestDimension = Math.min(width, height);

        context.getMatrices().peek().getPositionMatrix().rotationZ((float) toRadians(getRotation()));
        context.getMatrices().peek().getPositionMatrix().setTranslation(width / 2f, height / 2f, 0); // Make the gradient's center the pivot point
        context.getMatrices().peek().getPositionMatrix().scale(diagonal / smallestDimension); // Scales the gradient to the maximum diagonal value needed
        context.fillGradient(-width / 2, -height / 2, width / 2, height / 2, Blur.getBackgroundColor(false), Blur.getBackgroundColor(true)); // Actually draw the gradient
        context.getMatrices().peek().getPositionMatrix().rotationZ(0);
    }

    public enum Easing {
        FLAT(x -> x,                                    x -> x);

        final Function<Double, Number> functionIn;
        final Function<Double, Number> functionOut;

        Easing(Function<Double, Number> functionIn, Function<Double, Number> functionOut) {
            this.functionIn = functionIn;
            this.functionOut = functionOut;
        }
        public Double apply(Double x, boolean in) {
            if (in) return functionIn.apply(x).doubleValue();
            return functionOut.apply(x).doubleValue();
        }
    }
}
