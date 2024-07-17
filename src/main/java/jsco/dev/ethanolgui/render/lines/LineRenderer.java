package jsco.dev.ethanolgui.render.lines;

import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static jsco.dev.ethanolgui.client.EthanolGUI.CLIENT;
import static jsco.dev.ethanolgui.client.EthanolGUI.ETHANOL_HUD;

public class LineRenderer {
    public final List<Line> cache = new ArrayList<>();
    private final Random random = new Random();

    public LineRenderer() {}

    public void init(int lines, float speed) {
        for (int i = 0; i < lines; i++) {
            Line line = new Line(random.nextInt(CLIENT.getWindow().getScaledWidth()), random.nextInt(CLIENT.getWindow().getScaledHeight()), randomEulerRotation(), speed);
            cache.add(line);
        }
    }

    public void tickPhysics() {
        for (Line l : cache) {
            l.x += Math.cos(l.vector) * l.speed;
            l.y += Math.sin(l.vector) * l.speed;
            int flagcode = checkOOB(l);
            if (flagcode > 0) {
                switch (flagcode) {
                    case 1 -> {
                        l.x = CLIENT.getWindow().getScaledWidth();
                        l.vector = Math.toRadians(180 - Math.toDegrees(l.vector));
                    }
                    case 2 -> {
                        l.x = 0;
                        l.vector = Math.toRadians(180 - Math.toDegrees(l.vector));
                    }
                    case 3 -> {
                        l.y = CLIENT.getWindow().getScaledHeight();
                        l.vector = Math.toRadians(360 - Math.toDegrees(l.vector));
                    }
                    case 4 -> {
                        l.y = 0;
                        l.vector = Math.toRadians(360 - Math.toDegrees(l.vector));
                    }
                }
            }
        }
    }

    public void render(MatrixStack matrix) {
        for (int i = 0; i < cache.size(); i++) {
            Line l = cache.get(i);
            float circleSize = 2.5f + (float) Math.sin(System.currentTimeMillis() / 500.0 + i) * 1.5f;
            Renderer2d.renderCircle(matrix, new Color(255, 255, 255, ETHANOL_HUD.getAlpha()), l.x + 1, l.y + 1, circleSize, 90);

            for (int j = i + 1; j < cache.size(); j++) {
                Line nr = cache.get(j);
                double distance = distanceto(l.x + 1, l.y + 1, nr.x + 1, nr.y + 1);

                if (distance < 175) {
                    float lum = (float) (1.0 - distance / 150.0);
                    int alpha = Math.clamp((int) (100 * lum), 0, 255);
                    Color lineColor = new Color(255, 255, 255, alpha);
                    Renderer2d.renderLine(matrix, lineColor, l.x + 1, l.y + 1, nr.x + 1, nr.y + 1);
                }
            }
        }
    }

    public int checkOOB(Line n) {
        Window window = CLIENT.getWindow();
        if (n.x > window.getScaledWidth()) {
            return 1;
        }
        if (n.x < 0) {
            return 2;
        }
        if (n.y > window.getScaledHeight()) {
            return 3;
        }
        if (n.y < 0) {
            return 4;
        }
        return 0;
    }

    public double distanceto(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static double randomEulerRotation() {
        return (Math.random() - 0.5) * Math.PI * 4;
    }
}