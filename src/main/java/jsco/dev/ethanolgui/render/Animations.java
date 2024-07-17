package jsco.dev.ethanolgui.render;

import java.awt.*;

public class Animations {
    public static double easeOut(double x) {
        if (x < 0.5) {
            double x2 = x * x;
            return 4 * x2 * x;
        } else {
            double x2 = (2 - 2 * x) * (2 - 2 * x);
            return 1 - 0.5 * x2;
        }
    }

    public static Color colorFade(float t, Color startColor, Color endColor) {
        if (t < 0.0f) t = 0.0f;
        if (t > 1.0f) t = 1.0f;

        int startRed = startColor.getRed();
        int startGreen = startColor.getGreen();
        int startBlue = startColor.getBlue();

        int endRed = endColor.getRed();
        int endGreen = endColor.getGreen();
        int endBlue = endColor.getBlue();

        int red = (int) ((1 - t) * startRed + t * endRed);
        int green = (int) ((1 - t) * startGreen + t * endGreen);
        int blue = (int) ((1 - t) * startBlue + t * endBlue);

        return new Color(red, green, blue);
    }

    public static int numberFade(float t, int startValue, int endValue) {
        if (t < 0.0f) t = 0.0f;
        if (t > 1.0f) t = 1.0f;

        int value = (int) ((1 - t) * startValue + t * endValue);

        return value;
    }
}
