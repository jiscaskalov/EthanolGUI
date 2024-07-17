package jsco.dev.ethanolgui.render.blur;

import java.awt.*;

public class RainbowColor {
    public static double rotation;
    public static float hue;
    public static float hue2 = 0.35f;

    public static void render() {
        if (hue >= 1) hue = 0f;
        hue += 0.001f;
        if (hue2 >= 1) hue2 = 0f;
        hue2 += 0.001f;

        if (rotation >= 360) rotation = 0;
        rotation += .1;
    }

    public static Color average() {
        Color c1 = Color.getHSBColor(hue, 1, 1);
        Color c2 = Color.getHSBColor(hue2, 1, 1);

        int ar = (c1.getRed() + c2.getRed()) / 2;
        int ag = (c1.getGreen() + c2.getGreen()) / 2;
        int ab = (c1.getBlue() + c2.getBlue()) / 2;
        int aa = (c1.getAlpha() + c2.getAlpha()) / 2;

        return new Color(ar, ag, ab, aa);
    }
}
